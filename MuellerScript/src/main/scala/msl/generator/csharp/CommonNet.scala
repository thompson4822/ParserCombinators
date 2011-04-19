package msl.generator.csharp

import msl.Context
import msl.generator.Generator
import msl.dsl.Types.{DefinitionType, Method}

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/9/11
 * Time: 7:14 AM
 * To change this template use File | Settings | File Templates.
 */

import msl.generator.StringExtensions._

trait CommonNet {
  self: Generator =>
  def projectFileMapping: (String, String)

  def interfaceDocumentation(className: String, docs: Option[String]): String =
    classDocumentation("I" + className, docs)

  def classDocumentation(className: String, docs: Option[String]): String = {
    val summaryParagraphs = (docs match {
      case None => List(<c>{className}</c>.toString())
      case Some(x) =>
        val summary = new SummaryTextParser(x)
        summary.paragraphs
    }).map(str => "    ///   " + <para>{scala.xml.Unparsed(str)}</para>.toString()).mkString(nl)
    List("    /// <summary>", summaryParagraphs, "    /// </summary>").mkString(nl)
  }

  private def methodSummaryDocs(method: Method): String = {
    val summaryParagraphs = (method.documentation match {
      case None => List(<c>{method.name}</c>.toString)
      case Some(x) =>
        val summary = new SummaryTextParser(x)
        summary.withArguments(method.parameters)
    }).map(str => "        ///   " + <para>{scala.xml.Unparsed(str)}</para>.toString).mkString(nl)
    List("        /// <summary>", summaryParagraphs, "        /// </summary>").mkString(nl)
  }

  private def typeReference(definitionType: DefinitionType): String = (definitionType.genericType, definitionType.variableType.name) match {
      case(Some(x), y) => "A collection of " + (<see cref={y}>{y.splitId.mkString(" ")}</see> toString)
      case(None, "void") => ""
      case(_, x) => "A " + (<see cref={x}>{x.splitId.mkString(" ")}</see> toString)
  }

  private def methodParameterDocs(method: Method): String = {
    method.parameters.map(parameter => "        /// " + <param name={parameter.name}>{scala.xml.Unparsed(typeReference(parameter.definitionType))}</param> toString ).mkString(nl)
  }

  private def methodReturnDocs(method: Method): String = {
    "        /// " + (<returns>{scala.xml.Unparsed(typeReference(method.returnType))}</returns> toString)
  }

  def methodDocumentation(method: Method): String = {
    List(methodSummaryDocs(method), methodParameterDocs(method), methodReturnDocs(method)).mkString(nl)
  }

  if(Context.projectMapping.contains(projectFileMapping._1))
    Context.projectMapping(projectFileMapping._1) ::= projectFileMapping._2
  else
    Context.projectMapping.put(projectFileMapping._1, List(projectFileMapping._2))
}