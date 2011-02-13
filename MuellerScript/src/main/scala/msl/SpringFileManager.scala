package msl

import scala.xml.{Elem, XML, Node, TopScope}
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

class SpringFileManager(packageName: String, fileName: String) {
  val nl = System.getProperty("line.separator")
  val pathFileName = List(Context.netPath, packageName, fileName).mkString("/")

  private def fromFile(): Elem = {
    //def normalize(s: String) = if(s.indexOf("<Project") > 0) s.dropWhile(_ != '<') else s
    val fileContent = Source.fromFile(pathFileName, "utf-8").getLines.filter(x => x.indexOf("""<?xml""") == -1).mkString(nl)
    XML.loadString(fileContent)
  }

  private def toFile(xml: Elem) = {
    var string = """<?xml version="1.0" encoding="utf-8"?>""" + nl + xml.toString
    println(string)
    /*
    val writer = new PrintWriter(new BufferedWriter(new FileWriter(pathFileName,false)));
    writer.print(string)
    writer.close()
    */
  }

  import msl.generator.StringExtensions._

  // Extremely important!  This gets rid of all the xml namespaces that would otherwise trip up visual studio
  private def transformForPrinting(doc : Elem) : Elem = {
     def stripNamespaces(node : Node) : Node = {
         node match {
             case e : Elem =>
                 e.copy(scope = TopScope, child = e.child map (stripNamespaces))
             case _ => node;
         }
     }
     doc.copy( child = doc.child map (stripNamespaces) )
  }

  def updateObjects(packageIdentifiers: List[PackageIdentifier]) = {
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
    toFile(transformForPrinting(resultingXml))
  }
}