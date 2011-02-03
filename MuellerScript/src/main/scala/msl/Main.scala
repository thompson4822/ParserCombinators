package msl

//import dsl._
import collection.mutable.HashMap
import generator.Generator
import msl.dsl.MslParser
import msl.dsl.Types._
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
      showUsage

    val input = fromFile(args(0), "utf-8").mkString

    val m = new MslParser
    m.parseAll(m.statements, input) match {
      case m.Success(result, _) => generate(result)
      case other => error("Produced unexpected result: " + other.toString)
    }
  }

  def generate(statements: List[Statement]) = {
    statements foreach {
      case flexPackage: FlexPackage => Context.setFlexContext(flexPackage)
      case s @ Service(name, namespace, methods) => {
        val service: Service = Context.services.get(name).get
        Context.setNetService(namespace)
        service.methods.foreach{
          m =>
            save(new CommandRequestGen(m))
            save(new CommandResponseGen(m))
            save(new CommandGen(name, m))
        }
        save(new ServiceGen(service))
        save(new ServiceInterfaceGen(service))
      }
      case f @ Factory(name, dependencies, methods) => {
        val factory: Factory = Context.factories.get(name).get
        save(new FactoryInterfaceGen(factory))
        save(new FactoryTestInterfaceGen(factory))
        save(new FactoryTestGen(factory))
        if(factory.dependencies != Nil)
          save(new FactoryGen(factory))
        save(new FactoryClass(factory))
        save(new FactoryTestClass(factory))
      }
      case d @ Dto(name, definitions) => {
        save(new DtoGen(d))
      }
      case d @ Dao(name, definitions) => {
        save(new DaoGen(d))
        save(new DaoInterfaceGen(d))
      }
      case _ => error("I don't know how to generate that yet!")
    }
  }

  private def showUsage() = {
    println("""
Mueller Script Language (2011 by Steve Thompson)
usage:

java -jar <JARFILE NAME> <SCRIPT NAME>

where JARFILE NAME is the filename of the msl tool, and SCRIPT NAME is the
name of the script that instructs the tool on what files to generate.
    """)
    exit(1)
  }

  private def save(g: Generator) = {
    // Make sure the path exists
    (new File(g.filepath)).mkdirs
    // Save the file
    if(shouldSave(g)) {
      println("Writing: " + g.filePathName)
      val writer = new PrintWriter(new BufferedWriter(new FileWriter(g.filePathName,false)));
      writer.print(g.toString)
      writer.close()
    }
    //if(g.overwrite == false && (new File()).exists)
  }

  private def shouldSave(g: Generator) = {
    g.overwrite match {
      case true => true
      case _ => (new File(g.filePathName)).exists == false
    }
  }
}