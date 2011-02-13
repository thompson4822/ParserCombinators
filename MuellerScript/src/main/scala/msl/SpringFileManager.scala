package msl

import scala.xml.{Elem, XML}
import java.io.{FileWriter, BufferedWriter, PrintWriter}
import scala.io.Source
import msl.SpringFileManager.PackageIdentifier

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

class SpringFileManager(pathFileName: String) {
  val nl = System.getProperty("line.separator")

  private def fromFile(): Elem = {
    //def normalize(s: String) = if(s.indexOf("<Project") > 0) s.dropWhile(_ != '<') else s
    val fileContent = Source.fromFile(pathFileName, "utf-8").getLines.filter(x => x.indexOf("""<?xml""") == -1).mkString(nl)
    XML.loadString(fileContent)
  }

  private def toFile(xml: Elem) = {
    var string = """<?xml version="1.0" encoding="utf-8"?>""" + nl + xml.toString
    val writer = new PrintWriter(new BufferedWriter(new FileWriter(pathFileName,false)));
    writer.print(string)
    writer.close()
  }

  import msl.generator.StringExtensions._

  def updateObjects(packageName: String, packageIdentifiers: List[PackageIdentifier]) = {
    val xml = fromFile
    val objectSection = {
      val replacementNames: List[String] = packageIdentifiers.map(_.name.unCapitalize)
      // Only keep existing object nodes if they aren't among the new ones we want to generate!
      val oldNodes = (xml \\ "object").filter(x => replacementNames.contains((x \ "@id").text) == false)
      val newNodes = packageIdentifiers.map {
        p =>
        <object id={p.name.unCapitalize} type={ List(packageName, p.name).mkString(".") + ", " + packageName}>
          {p.properties.map(prop => <property name={prop.name} ref={prop.ref}/>)}
        </object>
      }
      oldNodes ++ newNodes
    }
    val resultingXml =
      <objects>
        {xml \\ "description" }
        {objectSection}
      </objects>
    toFile(resultingXml)
  }
}