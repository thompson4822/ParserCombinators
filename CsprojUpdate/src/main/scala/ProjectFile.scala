import io.Source
import java.io.{PrintWriter, BufferedWriter, FileWriter}
import xml.{Elem, NodeSeq, XML}

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/8/11
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */

class ProjectFile(pathFileName: String) {
  val nl = System.getProperty("line.separator")

  private def fromFile(): Elem = {
    val fileContent = Source.fromFile(pathFileName, "utf-8").getLines.filter(x => x.indexOf("""<?xml""") == -1).mkString(nl)
    println(fileContent)
    XML.loadString(fileContent)
  }

  private def toFile(xml: Elem) = {
    var string = """<?xml version="1.0" encoding="utf-8"?>""" + nl + xml.toString
    val writer = new PrintWriter(new BufferedWriter(new FileWriter(pathFileName,false)));
    writer.print(string)
    writer.close()
  }

  def updateSources(sourceFileNames: List[String]) = {
    val xml = fromFile
    val compileSection = {
      val includes: List[String] = (sourceFileNames ::: ( xml \\ "Compile").map(x => (x \ "@Include").text).toList).distinct
      includes map (x => <Compile Include={x}/>)
    }
    val toolsVersion = (xml \ "@ToolsVersion").text
    val defaultTargets = (xml \ "@DefaultTargets").text

    // TODO: Don't like to hard code this, but I couldn't get it parsed for some inexplicable reason.
    val xmlns = "http://schemas.microsoft.com/developer/msbuild/2003"

    toFile(
      <Project ToolsVersion={toolsVersion} DefaultTargets={defaultTargets} xmlns={xmlns}>
        {xml \\ "PropertyGroup"}
        <ItemGroup>{xml \\ "Reference"}</ItemGroup>
        <ItemGroup>{compileSection}</ItemGroup>
        <ItemGroup>{xml \\ "ProjectReference"}</ItemGroup>
        <ItemGroup>{xml \\ "EmbeddedResource"}</ItemGroup>
        {xml \ "Import"}
        {xml \\ "Target"}
      </Project>)
  }
}

object Main {
  def main(args: Array[String]) {
    val newFiles = List("Garbage.cs", "Trash.cs", "Garbage.cs", "Trash.cs", "Crap.cs")
    val projectFile = new ProjectFile("MyProject.csproj")
    projectFile.updateSources(newFiles)
  }
}