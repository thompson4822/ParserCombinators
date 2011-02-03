package msl.dsl

import collection.mutable.HashMap

object Types {
  sealed trait Statement {
    def name: String
  }

  case class Entity(name: String, entityElements: List[Definition]) extends Statement

  case class Dao(name: String, definitions: List[Definition]) extends Statement

  case class Dto(name: String, flexPackage: Option[FlexPackage], definitions: List[Definition]) extends Statement

  case class Mapping(name: String) extends Statement

  case class Factory(name: String, dependencies: List[Definition], methods: List[Method]) extends Statement

  case class Service(name: String, flexPackage: Option[FlexPackage], methods: List[Method]) extends Statement

  case class Enum(name: String, flexPackage: Option[FlexPackage], items: List[String]) extends Statement

  case class Flags(name: String, flexPackage: Option[FlexPackage], items: List[String]) extends Statement

  case class Definition(name: String, definitionType: DefinitionType) {
    def forCSharp = definitionType.forCSharp + " " + name
  }

  case class FlexPackage(name: String, namespace: NamespaceType.Value)

  case class DefinitionType(variableType: String, genericType: Option[String]) {
    def forFlex: String = (variableType, genericType) match {
        case (_, Some(value)) => "ArrayCollection"
        case ("int", None) => "int"
        case ("float", None) => "Number"
        case ("long", None) => "Number"
        case ("string", None) => "String"
        case ("DateTime", None) => "Date"
        case _ => "Object"
      }

    def forCSharp = genericType match {
      case Some(generic) => generic + "<" + variableType + ">"
      case _ => variableType
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