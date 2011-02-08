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
object MslParser {
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

}

class MslParser extends RegexParsers {
  import MslParser._


  lazy val statements: Parser[List[Statement]] =
    rep(statement) ^^ { case l => l.flatMap(x => x) }

  lazy val statement: Parser[Option[Statement]] =
    (dao | dto | factory | service | enum | flags) ^^ { case s => Some(s) } |
    (singleLineComment | multiLineComment) ^^^ { None }

  lazy val flexPackage: Parser[FlexPackage] = ("[" ~> packageType <~ "->") ~ (ident <~ "]") ^^ {
    case pkg ~ name => pkg(name)
  }

  lazy val packageType: Parser[String=>FlexPackage] =
    "Common" ^^^ { FlexPackage(_: String, NamespaceType.Common) } |
    "Utility" ^^^ { FlexPackage(_: String, NamespaceType.Utility) } |
    "Consumer" ^^^ { FlexPackage(_: String, NamespaceType.Consumer) }

  lazy val dao = daoIdent ~ daoDtoBody ^^ {
    case name ~ defs =>
    val result = Dao(name, defs)
    elements.update(name, result)
    result
  }

  lazy val dto = dtoIdent ~ flexPackage ~ daoDtoBody ^^ {
    case name ~ packageDef ~ defs =>
    val result = Dto(name, Some(packageDef), defs)
    elements.update(name, result)
    result
  }

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
    serviceIdent ~ flexPackage ~ serviceBody ^^ {
    case name ~ nspace ~ methods =>
      val serv = Service(name, Some(nspace), methods)
      addService(serv)
      serv
  }

  lazy val serviceBody = "{" ~> rep(method) <~ "}"

  lazy val enum = enumIdent ~ flexPackage ~ enumFlagsBody ^^ {
    case name ~ namespace ~ identifiers =>
      val result = Enum(name, Some(namespace), identifiers)
      elements(name) = result
      result
  }

  lazy val flags = flagsIdent ~ flexPackage ~ enumFlagsBody ^^ {
    case name ~ namespace ~ identifiers =>
      val result = Flags(name, Some(namespace), identifiers)
      elements(name) = result
      result
  }

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
          case daoIdent(name) =>
            new DefinitionType(findElementFunc(name), Some(generic))
          case dtoIdent(name) =>
            new DefinitionType(findElementFunc(name), Some(generic))
          case _ => new DefinitionType(() => Primitive(genType), Some(generic))

        }
    } |
    basicType ^^ { new DefinitionType(_, None) }

  lazy val basicType =
    ("int" | "long" | "string" | "double" | "char" | "bool" | "DateTime") ^^ { case s => () => Primitive(s) } |
    daoIdent ^^ { case daoIdent(name) => findElementFunc(name) } |
    dtoIdent ^^ { case dtoIdent(name) => findElementFunc(name) } |
    enumIdent ^^ { case name => findElementFunc(name) } |
    flagsIdent ^^ { case name => findElementFunc(name) } |
    ident ^^ { case s => () => Primitive(s) }

  def findElementFunc(elementName: String): () => Type = {
    () => {
      elements.get(elementName) match {
        case Some(value: Type) => value
        case _ => error("The identifier '" + elementName + "' was used, but was never defined in your script.")
      }
    }
  }

  def addFactory(f: Factory): Unit = {
    elements.get(f.name) match {
      case Some(factory: Factory) =>
        val dependencies = (factory.dependencies ::: f.dependencies).distinct
        val methods = (factory.methods ::: f.methods).distinct
        elements(f.name) = f.copy(dependencies = dependencies, methods = methods)
      case _ =>
        elements(f.name) = f
    }
  }

  def addDto(d: Dto) = {
    elements.get(d.name) match {
      case Some(dto: Dto) =>
        elements(d.name) = d.copy(definitions = dto.definitions ::: d.definitions)
      case _ =>
        elements(d.name) = d
    }
  }

  def addDao(d: Dao) = {
    elements.get(d.name) match {
      case Some(dao: Dao) =>
        elements(d.name) = d.copy(definitions = dao.definitions ::: d.definitions)
      case _ =>
        elements(d.name) = d
    }
  }

  def addService(s: Service): Unit = {
    elements.get(s.name) match {
      case Some(service: Service) =>
        elements(s.name) = s.copy(methods = (service.methods ::: s.methods).distinct)
      case _ =>
        elements(s.name) = s
    }
    addFactory(factoryFor(s))
  }

  def factoryFor(s: Service): Factory = {
    val factoryName = s.name.replace("Service", "Factory")
    Factory(factoryName, Nil, s.methods)
  }
}