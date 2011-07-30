package msl.dsl

import msl.PropertiesFileManager

object Types {
  sealed trait Statement {
    def name: String
  }

  trait Type {
    self: Statement =>
    def name: String

    def forFlex: String
    def forCSharp: String
  }

  case class Entity(name: String, entityElements: List[Definition]) extends Statement

  case class Dao(name: String, methods: List[Method], documentation: Option[String] = None) extends Statement with Type {
    def forFlex: String = ""
    def forCSharp: String = name
  }

  case class DataSource(name: String, arguments: String="()") extends Statement

  class DtoDefinition(override val name: String, override val definitionType: DefinitionType, val dataSource: Option[DataSource] = None) extends Definition(name, definitionType) {}

  case class Dto(name: String, flexPackage: Option[FlexPackage], definitions: List[DtoDefinition], documentation: Option[String] = None) extends Statement with Type {
    def forFlex: String = name
    def forCSharp: String = name
  }

  case class Primitive(name: String, isNullable: Boolean = false, isArray: Boolean = false) extends Statement with Type {
    object TypeMappings {
      val mappings = PropertiesFileManager.read("msltypes.properties") match {
        case None => sys.error("Can't find the file 'msltypes.properties' or it has no definitions")
        case Some(map: Map[String,String]) => map
      }
    }

    def forFlex: String =
      TypeMappings.mappings.getOrElse(name, "Object")

    def forCSharp: String = (isNullable, isArray) match {
      case (true, _) => name + "?"
      case (_, true) => name + "[]"
      case _ => name
    }
  }

  case class Mapping(name: String) extends Statement

  case class Factory(name: String, dependencies: List[Definition], methods: List[Method], documentation: Option[String] = None) extends Statement with Type {
    def forFlex: String = ""
    def forCSharp: String = name
  }

  case class Service(name: String, flexPackage: Option[FlexPackage], methods: List[Method], documentation: Option[String] = None) extends Statement

  case class EnumItem(name: String, value: Int)

  case class Enum(name: String, flexPackage: Option[FlexPackage], items: List[EnumItem], documentation: Option[String] = None) extends Statement with Type {
    def forFlex: String = "int"
    def forCSharp: String = name
  }

  case class Flags(name: String, flexPackage: Option[FlexPackage], items: List[EnumItem], documentation: Option[String] = None) extends Statement with Type {
    def forFlex: String = "int"
    def forCSharp: String = name
  }

  case class Definition(name: String, definitionType: DefinitionType) {
    def forCSharp = definitionType.forCSharp + " " + name
  }

  case class FlexPackage(name: String, namespace: NamespaceType.Value)
  {
    val flexSubPackage = name

    val flexSubDirectory = name.split('.').mkString("/")
  }

  class DefinitionType(vType: () => Type, val genericType: Option[String]) {
    def variableType = vType()
    def forFlex: String = (variableType, genericType) match {
        case (_, Some(value)) => "ArrayCollection"
        case (x: Type, None) => x.forFlex
      }

    def forCSharp: String = genericType match {
      case Some(generic) => generic + "<" + variableType.forCSharp + ">"
      case _ => variableType.forCSharp
    }
  }

  case class Command(name: String, service: Service) extends Statement

  case class Inject(name: String, artifactType: String)

  case class Method(name: String, returnType: DefinitionType, parameters: List[Definition], documentation: Option[String] = None) {
    def cSharpSignature = returnType.forCSharp + " " + name + "(" + parameters.map(p => p.forCSharp).mkString(", ") + ")"
  }

  class Documentation(val text: String) {

  }

  object NamespaceType extends Enumeration {
    val Common = Value("Common")
    val Consumer = Value("Consumer")
    val Utility = Value("Utility")
    val Installer = Value("Installer")
  }
}