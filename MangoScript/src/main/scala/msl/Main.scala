package msl

import generator.Generator
import dsl._
import Types._
import generator.csharp._
import generator.flex._
import java.io._

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 6:58 AM
 * To change this template use File | Settings | File Templates.
 */

import scala.io.Source._

object Main {
  def main(args: Array[String]) {
    if(args.length == 0)
      showUsage()

    GenerationManager.parseFile(args(0))
    Context.elements.map{ f=> val (_, value) = f; value}.foreach(GenerationManager.generate)

    updateProjectFiles()
    updateSpringFiles()
  }

  private def showUsage() = {
    println("""
Mango Script Language (2011 by Steve Thompson)
usage:

java -jar <JARFILE NAME> <SCRIPT NAME>

where JARFILE NAME is the filename of the msl tool, and SCRIPT NAME is the
name of the script that instructs the tool on what files to generate.
    """)
    sys.exit(1)
  }

  private def updateSpringFiles() {
    updateDaoXml()
    updateBusinessXml()
    updateServiceXml()
  }

  import SpringFileManager._
  import msl.generator.StringExtensions._

  private def updateDaoXml() {
    // TODO - Could this be done more cleanly with a fold?
    val items:List[Dao] = Context.elements.collect{case (_, d: Dao) => d}.toList
    if(items.length > 0) {
      val manager = new SpringFileManager(List(Context.netDao, "NHibernate").mkString("."), "Dao.xml")
      println("Updating " + manager.pathFileName)
      manager.updateObjects(items.map(i => PackageIdentifier(i.name)).toList)
    }
  }

  private def updateBusinessXml() {
    def objectPropertiesFor(f: Factory): List[ObjectProperty] =
      f.dependencies.map(_.definitionType.variableType.name).sortWith(_ < _).map(ident => ObjectProperty(ident, ident.unCapitalize))

    val items: List[Factory] = Context.elements.collect{case (_, f: Factory) => f}.toList
    if(items.length > 0) {
      val manager = new SpringFileManager(Context.netFactory, "Business.xml")
      println("Updating " + manager.pathFileName)
      manager.updateObjects(items.map(i => PackageIdentifier(i.name, objectPropertiesFor(i))).toList)
    }
  }

  private def updateServiceXml() {
    val items: List[Service] = Context.elements.collect{ case(_, s: Service) => s}.toList
    def packageIdentifiersFor(namespace: String): List[PackageIdentifier] = {
      def refName(serviceName: String) = serviceName.unCapitalize.dropRight(7) + "Factory"
      items.
        filter(service => Context.getNetService(service.flexPackage.get) == namespace).
        map(service => PackageIdentifier(service.name, List(ObjectProperty(name="Factory", ref=refName(service.name))))).
        toList
    }
    Context.netServices foreach {
      namespace =>
      packageIdentifiersFor(namespace) match {
        case Nil =>
        case packageIdentifiers: List[PackageIdentifier] =>
          val manager = new SpringFileManager(namespace, "Services.xml")
          println("Updating " + manager.pathFileName)
          manager.updateObjects(packageIdentifiers)
      }
    }
  }

  private def updateProjectFiles() {
    Context.projectMapping.foreach {
      f =>
        val (projectName, sources) = f
        println("Updating " + projectName + " project file")
        // The following is an ugly hack.  Service consumer does not follow the rules.
        val projectFile = (projectName == Context.netServiceConsumer) match {
          case false =>
            new CsProjectFileManager(List(Context.netPath, projectName, projectName + ".csproj").mkString("/"))
          case _ =>
            new CsProjectFileManager(List(Context.netPath, projectName, projectName + ".Consumer.csproj").mkString("/"))
        }
        projectFile.updateSources(sources)
    }
  }

}

object GenerationManager {
  var currentFile: String = _

  def parseFile(filename: String) = {
    val input = fromFile(filename, "utf-8").mkString
    currentFile = filename

    val m = new MslParser
    m.parseAll(m.statements, input) match {
      case m.Success(result, _) => result
      case other => sys.error("Produced unexpected result: " + other.toString)
    }
  }

  def generate(statement: Statement) = statement match {
    //case flexPackage: FlexPackage => Context.setFlexContext(flexPackage)
    case s @ Service(name, Some(namespace), methods, _) => {
      val service: Service = Context.elements.get(name).get.asInstanceOf[Service]
      Context.setNetService(namespace)
      service.methods.foreach{
        m =>
          save(new CommandRequestGen(m, namespace))
          save(new CommandResponseGen(m, namespace))
          save(new CommandGen(name, m, namespace))
      }
      save(new ServiceGen(service))
      save(new ServiceInterfaceGen(service))
    }
    case f @ Factory(name, dependencies, methods, _) => {
      val factory: Factory = Context.elements.get(name).get.asInstanceOf[Factory]
      save(new FactoryInterfaceGen(factory))
      save(new FactoryTestInterfaceGen(factory))
      save(new FactoryTestGen(factory))
      save(new FactoryGen(factory))
      save(new FactoryClass(factory))
      save(new FactoryTestClass(factory))
    }
    case d @ Dto(name, namespace, definitions, _) => {
      save(new DataSourceGen(d))
      save(new DtoGen(d))
      namespace.map(nspace => save(new FlexDtoGen(d, nspace)))
    }
    case d: Dao => {
      save(new DaoGen(d))
      save(new DaoClassMaker(d))
      save(new DaoInterfaceGen(d))
    }
    case e @ Enum(_, namespace, _, _) => {
      save(new EnumGen(e))
      namespace.map(nspace => save(new FlexEnumGen(e, nspace)))
    }
    case f @ Flags(_, namespace, _, _) => {
      save(new FlagsGen(f))
      namespace.map(nspace => save(new FlexFlagsGen(f, nspace)))
    }
    case other => sys.error("I don't know how to generate " + other + " yet!")
  }

  private def save(g: Generator) {
    // Make sure the path exists
    (new File(g.filePath)).mkdirs

    // Save the file
    if(shouldSave(g)) {
      println("Writing: " + g.filePathName)
      val writer = new PrintWriter(new BufferedWriter(new FileWriter(g.filePathName,false)));
      writer.print(g.toString)
      writer.close()
    }
  }

  private def shouldSave(g: Generator) = {
    g.overwrite match {
      case true => true
      case _ => (new File(g.filePathName)).exists == false
    }
  }
}