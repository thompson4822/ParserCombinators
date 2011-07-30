package msl

import io.Source
import java.io.{PrintWriter, BufferedWriter, FileWriter}
import scala.xml._
/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/8/11
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */

class CsProjectFileManager(pathFileName: String) {
  val nl = System.getProperty("line.separator")

  private def fromFile(): Elem = {
    def normalize(s: String) = if(s.indexOf("<Project") > 0) s.dropWhile(_ != '<') else s
    val fileContent = Source.fromFile(pathFileName, "utf-8").getLines().filter(x => x.indexOf("""<?xml""") == -1).map(normalize).mkString(nl)
    XML.loadString(fileContent)
  }

  private def toFile(xml: Elem) {
    val string = """<?xml version="1.0" encoding="utf-8"?>""" + nl + xml.toString
    val writer = new PrintWriter(new BufferedWriter(new FileWriter(pathFileName,false)));
    writer.print(string)
    writer.close()
  }

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

  def updateSources(sourceFileNames: List[String]) {
    val xml = fromFile()
    val compileSection = {
      val includes: List[String] = (sourceFileNames ::: ( xml \\ "Compile").map(x => (x \ "@Include").text).toList).distinct.sortWith(_ < _)
      includes map (x => <Compile Include={x}/>)
    }
    val toolsVersion = (xml \ "@ToolsVersion").text
    val defaultTargets = (xml \ "@DefaultTargets").text

    // TODO: Don't like to hard code this, but I couldn't get it parsed for some inexplicable reason.
    val xmlns = "http://schemas.microsoft.com/developer/msbuild/2003"

    val resultingXml = transformForPrinting(
      <Project ToolsVersion={toolsVersion} DefaultTargets={defaultTargets} xmlns={xmlns}>
        {xml \\ "PropertyGroup" }
        <ItemGroup>{xml \\ "Reference" }</ItemGroup>
        <ItemGroup>{compileSection}</ItemGroup>
        <ItemGroup>{xml \\ "ProjectReference" }</ItemGroup>
        <ItemGroup>{xml \\ "EmbeddedResource" }</ItemGroup>
        {xml \ "Import"}
        {xml \\ "Target" }
      </Project>)
    toFile(resultingXml)
  }
}
