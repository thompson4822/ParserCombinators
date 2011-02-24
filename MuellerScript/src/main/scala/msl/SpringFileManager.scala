package msl

import java.io.{FileWriter, BufferedWriter, PrintWriter}
import scala.io.Source
import msl.SpringFileManager.PackageIdentifier
import scala.xml._

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/13/11
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */

object SpringFileManager {
  case class ObjectProperty(name: String, ref: String)
  case class PackageIdentifier(name: String, properties: List[ObjectProperty] = List(ObjectProperty("SessionFactory", "SessionFactory")))
}

class SpringFileManager(packageName: String, fileName: String) {
  val nl = System.getProperty("line.separator")
  val pathFileName = List(Context.netPath, packageName, fileName).mkString("/")

  private def fromFile(): Elem = {
    //def normalize(s: String) = if(s.indexOf("<Project") > 0) s.dropWhile(_ != '<') else s
    val fileContent = Source.fromFile(pathFileName, "utf-8").getLines.filter(x => x.indexOf("""<?xml""") == -1).mkString(nl)
    XML.loadString(fileContent)
  }

  private def toFile(xml: Elem) = {
    val prettyPrinter = new PrettyPrinter(256, 2)
    var string = """<?xml version="1.0" encoding="utf-8"?>""" + nl + prettyPrinter.format(xml)

    val writer = new PrintWriter(new BufferedWriter(new FileWriter(pathFileName,false)));
    writer.print(string)
    writer.close()
  }

  import msl.generator.StringExtensions._

  def updateObjects(packageIdentifiers: List[PackageIdentifier]) = {
    val xml = fromFile
    def objectSection = {
      val replacementNames: List[String] = packageIdentifiers.map(_.name.unCapitalize)
      // Only keep existing object nodes if they aren't among the new ones we want to generate!
      val oldNodes: Seq[Node] = (xml \\ "object").filter(x => replacementNames.contains((x \ "@id").text) == false)
      val newNodes: Seq[Node] = packageIdentifiers.map {
        p =>
        <object id={p.name.unCapitalize} type={ List(packageName, p.name).mkString(".") + ", " + packageName}>
          {p.properties.map(prop => <property name={prop.name} ref={prop.ref}/>)}
        </object>
      }
      // Combine all nodes and sort their order by their id attribute
      (oldNodes ++ newNodes).sortWith((x, y) => (x \ "@id").text < (y \ "@id").text)
    }

    val optionalParts = List((xml \\ "provider").headOption, (xml \\ "attribute-driven").headOption).flatten
    val content = (xml \\ "description").head ++ optionalParts ++ objectSection
    toFile(Elem(prefix = null, label = "objects", attributes = scala.xml.Null, scope = xml.scope, content: _*))
  }
}