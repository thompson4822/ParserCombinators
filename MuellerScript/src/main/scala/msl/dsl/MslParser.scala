package msl.dsl

import util.parsing.combinator.RegexParsers
import Types._
import msl.Context._
/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/6/11
 * Time: 7:53 AM
 * To change this template use File | Settings | File Templates.
 */

class MslParser extends RegexParsers {
  //this.

  lazy val genericType = """([a-zA-Z][a-zA-Z0-9_]*)<([a-zA-Z][a-zA-Z0-9_]*)>""".r
  lazy val ident = """([a-zA-Z][a-zA-Z0-9_]*)""".r
  lazy val factoryIdent = """([a-zA-Z][a-zA-Z0-9_]*Factory)""".r
  lazy val serviceIdent = """([a-zA-Z][a-zA-Z0-9_]*Service)""".r
  lazy val packageIdent = """[a-zA-Z][a-zA-Z0-9_]*(\.[a-zA-Z][a-zA-Z0-9_]*)*""".r
  lazy val dtoIdent = """([a-zA-Z][a-zA-Z0-9_]*Dto)""".r
  lazy val daoIdent = """I([a-zA-Z][a-zA-Z0-9_]*Dao)""".r
  lazy val enumIdent = """([a-zA-Z][a-zA-Z0-9_]*Enum)""".r
  lazy val flagsIdent = """([a-zA-Z][a-zA-Z0-9_]*Flags)""".r
  lazy val integer = """[1-9][0-9]*""".r


  lazy val singleLineComment = """//.*""".r
  lazy val multiLineComment = """/\*[^*]*\*+(?:[^*/][^*]*\*+)*/""".r

  lazy val statements: Parser[List[Statement]] =
    rep(statement) ^^ { case l => l.flatMap(x => x) }

  lazy val statement: Parser[Option[Statement]] =
    (dao | dto | factory | service | flexPackage | enum | flags) ^^ { case s => Some(s) } |
    (singleLineComment | multiLineComment) ^^^ { None }

  lazy val flexPackage: Parser[FlexPackage] = "flex" ~> ("[" ~> packageType <~ "]") ~ ("=" ~> ident) ^^ {
    case pkg ~ name => pkg(name)
  }

  lazy val packageType: Parser[String=>FlexPackage] =
    "Common" ^^^ { FlexPackage(_: String, NamespaceType.Common) } |
    "Utility" ^^^ { FlexPackage(_: String, NamespaceType.Utility) } |
    "Consumer" ^^^ { FlexPackage(_: String, NamespaceType.Consumer) }

  lazy val dao = daoIdent ~ daoDtoBody ^^ { case name ~ defs => Dao(name, defs) }

  lazy val dto = dtoIdent ~ daoDtoBody ^^ { case name ~ defs => Dto(name, defs) }

  lazy val daoDtoBody = "{" ~> rep(definition) <~ "}"

  lazy val factory: Parser[Factory] =
    factoryIdent ~ factoryBody ^^ {
      case name ~ body =>
        val fact = body(name)
        addFactory(fact)
        fact
    }

  lazy val factoryBody: Parser[String=>Factory] =
    "{" ~> rep(inject) ~ rep(method) <~ "}" ^^ { case injections ~ methods => Factory(_: String, injections, methods) }

  lazy val service: Parser[Service] =
    serviceIdent ~ namespace ~ serviceBody ^^ {
    case name ~ nspace ~ methods =>
      val serv = Service(name, nspace, methods)
      addService(serv)
      serv
  }

  lazy val serviceBody = "{" ~> rep(method) <~ "}"

  lazy val enum = enumIdent ~ enumFlagsBody ^^ { case name ~ identifiers => Enum(name, identifiers) }

  lazy val flags = flagsIdent ~ enumFlagsBody ^^ { case name ~ identifiers => Flags(name, identifiers)}

  lazy val enumFlagsBody = "{" ~> repsep(ident, ",") <~ "}"

  lazy val namespace: Parser[NamespaceType.Value] =
    "[" ~> ("Common" ^^^ { NamespaceType.Common } |
    "Utility" ^^^ { NamespaceType.Utility } |
    "Consumer" ^^^ { NamespaceType.Consumer} ) <~ "]"

  lazy val method = definitionType ~ ident ~ ( "(" ~> repsep(definition, ",") <~ ")" ) ^^ {
    case returnType ~ methodName ~ parameters =>
        Method(methodName, returnType, parameters)
  }

  lazy val inject = "inject" ~> definition

  lazy val definition: Parser[Definition] =
    definitionType ~ ident ^^ { case defType ~ identifier => Definition(identifier, defType) }

  /*
    Note that order of the following is important.  Placing the more general basicType before genericType will
    prevent the latter from being recognized.
   */
  lazy val definitionType: Parser[DefinitionType] =
    genericType ^^ {
      case genericType(generic, genType) =>
        genType match {
          case daoIdent(name) => addDaoReference(new Dao(name, Nil))
          case dtoIdent(name) => addDtoReference(new Dto(name, Nil))
          case _ => println(genType + " was not a DAO/DTO")
        }
        DefinitionType(genType, Some(generic))
    } |
    basicType ^^ { DefinitionType(_, None) }

  lazy val basicType =
    "int" | "long" | "string" | "double" | "char" | "bool" |
    daoIdent ^^ { case name => addDaoReference(new Dao(name, Nil)); name } |
    dtoIdent ^^ { case name => addDtoReference(new Dto(name, Nil)); name } |
    ident

  def addDaoReference(d: Dao) = daos.getOrElseUpdate(d.name, d)

  def addDtoReference(d: Dto) = dtos.getOrElseUpdate(d.name, d)

  def addFactory(f: Factory): Unit = {
    factories.get(f.name) match {
      case Some(factory) =>
        val dependencies = (factory.dependencies ::: f.dependencies).distinct
        val methods = (factory.methods ::: f.methods).distinct
        factories(f.name) = f.copy(dependencies = dependencies, methods = methods)
      case _ =>
        factories(f.name) = f
    }
  }

  def addService(s: Service): Unit = {
    services.get(s.name) match {
      case Some(service) =>
        services(s.name) = s.copy(methods = (service.methods ::: s.methods).distinct)
      case _ =>
        services(s.name) = s
    }
    addFactory(factoryFor(s))
  }

  def factoryFor(s: Service): Factory = {
    val factoryName = s.name.replace("Service", "Factory")
    Factory(factoryName, Nil, s.methods)
  }

  /*
  lazy val entity = "entity" ~> ident ~ entityBody ^^ {case name ~ body => Entity(name, body) }

  lazy val entityBody = "{" ~> entityElements <~ "}"

  lazy val entityElements = definition*

  lazy val mapping = "mapping" ~> ident ~ mappingBody

  lazy val mappingBody = "{" ~ "}"

  lazy val artifactType = "enum" | "dao" | "entity" | "mapping" | "dto" | "factory" | "service"
  */
}