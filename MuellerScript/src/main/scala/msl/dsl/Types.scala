package msl.dsl

import collection.mutable.HashMap

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

  case class Dao(name: String, methods: List[Method]) extends Statement with Type {
    def forFlex: String = ""
    def forCSharp: String = ""
  }

  case class Dto(name: String, flexPackage: Option[FlexPackage], definitions: List[Definition]) extends Statement with Type {
    def forFlex: String = name
    def forCSharp: String = ""
  }

  case class Primitive(name: String) extends Statement with Type {
    def forFlex: String = name match {
      case "int" => "int"
      case "float" => "Number"
      case "long" => "Number"
      case "string" => "String"
      case "DateTime" => "Date"
      case "bool" => "Boolean"
      case _ => "Object"
    }

    def forCSharp: String = {
      ""
    }
  }

  case class Mapping(name: String) extends Statement

  case class Factory(name: String, dependencies: List[Definition], methods: List[Method]) extends Statement with Type {
    def forFlex: String = ""
    def forCSharp: String = ""
  }

  case class Service(name: String, flexPackage: Option[FlexPackage], methods: List[Method]) extends Statement

  case class Enum(name: String, flexPackage: Option[FlexPackage], items: List[String]) extends Statement with Type {
    def forFlex: String = "int"
    def forCSharp: String = ""
  }

  case class Flags(name: String, flexPackage: Option[FlexPackage], items: List[String]) extends Statement with Type {
    def forFlex: String = "int"
    def forCSharp: String = ""
  }

  case class Definition(name: String, definitionType: DefinitionType) {
    def forCSharp = definitionType.forCSharp + " " + name
  }

  case class FlexPackage(name: String, namespace: NamespaceType.Value)

  // TODO: Revisit when you address Enums and Flags
  class DefinitionType(vType: () => Type, genericType: Option[String]) {
    def variableType = vType()
    def forFlex: String = (variableType, genericType) match {
        case (_, Some(value)) => "ArrayCollection"
        case (x: Type, None) => x.forFlex
      }

    def forCSharp: String = genericType match {
      case Some(generic) => generic + "<" + variableType.name + ">"
      case _ => variableType.name
    }
  }

  case class Command(name: String, service: Service) extends Statement

  case class Inject(name: String, artifactType: String)

  case class Method(name: String, returnType: DefinitionType, parameters: List[Definition]) {
    def cSharpSignature = returnType.forCSharp + " " + name + "(" + parameters.map(p => p.forCSharp).mkString(", ") + ")"
  }

  object NamespaceType extends Enumeration {
    val Common = Value("Common")
    val Consumer = Value("Consumer")
    val Utility = Value("Utility")
  }
}