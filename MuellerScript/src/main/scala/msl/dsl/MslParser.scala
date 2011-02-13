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
  lazy val daoIdent = """([a-zA-Z][a-zA-Z0-9_]*Dao)""".r
  lazy val enumIdent = """([a-zA-Z][a-zA-Z0-9_]*Enum)""".r
  lazy val flagsIdent = """([a-zA-Z][a-zA-Z0-9_]*Flags)""".r
  lazy val intValue = """(0|[1-9][0-9]*)""".r
  lazy val hexValue = """(0x[01-9a-fA-F]*)""".r
  lazy val binaryValue = """([0-1]+b)""".r

  lazy val singleLineComment = """//.*""".r
  lazy val multiLineComment = """/\*[^*]*\*+(?:[^*/][^*]*\*+)*/""".r

}

class MslParser extends RegexParsers {
  import MslParser._

  implicit def stringToExtender(string: String) = new StringExtender(string)

  class StringExtender(string: String) {

    // Redo as a shift expression!
    def fromHex: Int = {
      def fromHexRec(value: Int, remainder: List[Char]): Int = remainder match {
        case Nil => value
        case x :: rest =>
          val increment = if(x <= '9') x - '0' else (x - 'A') + 10
          fromHexRec((value * 16) + increment, rest)
      }
      fromHexRec(0, string.drop(2).toUpperCase.toList)
    }

    def fromBinary: Int = {
      def binaryValue(c: Char): Int = if(c=='1') 1 else 0
      string.dropRight(1).toList.foldLeft(0)(_ * 2 + binaryValue(_))
    }
  }

  lazy val statements: Parser[List[Statement]] =
    rep(statement) ^^ { case l => l.flatMap(x => x) }

  lazy val statement: Parser[Option[Statement]] =
    (dao | dto | factory | service | enum | flags) ^^ { case s => Some(s) } | comment ^^^ { None }

  lazy val comment: Parser[Option[Statement]] = (singleLineComment | multiLineComment) ^^^ { None }

  lazy val flexPackage: Parser[FlexPackage] = ("[" ~> packageType <~ "->") ~ (ident <~ "]") ^^ {
    case pkg ~ name => pkg(name)
  }

  lazy val packageType: Parser[String=>FlexPackage] =
    "Common" ^^^ { FlexPackage(_: String, NamespaceType.Common) } |
    "Utility" ^^^ { FlexPackage(_: String, NamespaceType.Utility) } |
    "Consumer" ^^^ { FlexPackage(_: String, NamespaceType.Consumer) }

  lazy val dao = daoIdent ~ methodBody ^^ {
    case name ~ methods =>
    val result = Dao(name, methods)
    elements.update(name, result)
    result
  }

  lazy val dto = dtoIdent ~ flexPackage ~ dtoBody ^^ {
    case name ~ packageDef ~ defs =>
    val result = Dto(name, Some(packageDef), defs)
    elements.update(name, result)
    result
  }

  lazy val dtoBody = "{" ~> rep(definition) <~ "}" ^^ { case defs => defs.flatten }

  lazy val factory: Parser[Factory] =
    factoryIdent ~ factoryBody ^^ {
      case name ~ body =>
        val fact = body(name)
        addFactory(fact)
        fact
    }

  lazy val factoryBody: Parser[String=>Factory] =
    "{" ~> rep(inject) ~ rep(method) <~ "}" ^^ { case injections ~ methods => Factory(_: String, injections, methods.flatten) }

  lazy val service: Parser[Service] =
    serviceIdent ~ flexPackage ~ methodBody ^^ {
    case name ~ nspace ~ methods =>
      val serv = Service(name, Some(nspace), methods)
      addService(serv)
      serv
  }

  lazy val methodBody: Parser[List[Method]] = "{" ~> rep(method) <~ "}" ^^ { case methods => methods.flatten }

  lazy val enum = enumIdent ~ flexPackage ~ enumBody ^^ {
    case name ~ namespace ~ items =>
      val result = Enum(name, Some(namespace), items)
      elements(name) = result
      result
  }

  lazy val flags = flagsIdent ~ flexPackage ~ flagsBody ^^ {
    case name ~ namespace ~ items =>
      val result = Flags(name, Some(namespace), items)
      elements(name) = result
      result
  }

  lazy val enumBody = "{" ~> repsep(enumItem, ",") <~ "}" ^^ { resolveEnumValues(_, x => x + 1) }

  lazy val flagsBody = "{" ~> repsep(enumItem, ",") <~ "}" ^^ { resolveEnumValues(_, nextBinaryEnum) }

  def resolveEnumValues(items: List[Tuple2[String, Option[Int]]], nextValueFunc: Int => Int): List[EnumItem] = {
    def resolveValuesRec(currentValue: Int, parsed: List[EnumItem], remaining: List[Tuple2[String, Option[Int]]]): List[EnumItem] = remaining match {
      case Nil => parsed.reverse
      case (name, None) :: tail =>
        val nextValue = nextValueFunc(currentValue) // + 1
        resolveValuesRec(nextValue, EnumItem(name, nextValue) :: parsed, tail)
      case (name, Some(value)) :: tail => resolveValuesRec(value, EnumItem(name, value) :: parsed, tail)
    }
    resolveValuesRec(0, Nil, items)
  }

  def nextBinaryEnum(value: Int): Int = {
    def nextBinaryEnumRec(shift: Int, currentValue: Int): Int = (currentValue > 0) match {
      case false => 1 << shift
      case _ => nextBinaryEnumRec(shift + 1, currentValue >> 1)
    }
    nextBinaryEnumRec(0, value)
  }

  lazy val enumItem: Parser[Tuple2[String, Option[Int]]] =
    ident ~ opt(enumDefinition) ^^ { case name ~ optEnumDef => (name, optEnumDef) }

  lazy val enumDefinition: Parser[Int] = "=" ~> numericConstant

  lazy val numericConstant: Parser[Int] =
    hexValue ^^ { case hexValue(v) => v.fromHex } |
    binaryValue ^^ { case binaryValue(v) => v.fromBinary } |
    intValue ^^ { case intValue(v) => v.toInt }


  lazy val namespace: Parser[NamespaceType.Value] =
    "[" ~> ("Common" ^^^ { NamespaceType.Common } |
    "Utility" ^^^ { NamespaceType.Utility } |
    "Consumer" ^^^ { NamespaceType.Consumer} ) <~ "]"

  lazy val method: Parser[Option[Method]] =
    comment ^^^ { None } |
    definitionType ~ ident ~ ( "(" ~> repsep(definitionNoComment, ",") <~ ")" ) ^^ {
    case returnType ~ methodName ~ parameters =>
        Some(Method(methodName, returnType, parameters))
  }

  lazy val inject = "inject" ~> definitionNoComment

  lazy val definitionNoComment: Parser[Definition] =
    definitionType ~ ident ^^ { case defType ~ identifier => Definition(identifier, defType) }

  lazy val definition: Parser[Option[Definition]] =
    comment ^^^ { None } |
    definitionNoComment ^^ { case define => Some(define) }

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
          case enumIdent(name) =>
            new DefinitionType(findElementFunc(name), Some(generic))
          case flagsIdent(name) =>
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
        elements(d.name) = d.copy(methods = dao.methods ::: d.methods)
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