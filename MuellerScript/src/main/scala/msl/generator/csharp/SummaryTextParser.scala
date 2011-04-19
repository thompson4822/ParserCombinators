package msl.generator.csharp

import util.matching.Regex.Match
import msl.dsl.Types.Definition

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 4/18/11
 * Time: 6:10 AM
 * To change this template use File | Settings | File Templates.
 */

class SummaryTextParser(text: String) {

  lazy val paragraphs: List[String] = text.split(nl + nl).map(p => p.split(nl).map(_.trim).mkString(" ")).toList

  private val nl = System.getProperty("line.separator")

  def withArguments(args: List[Definition]): List[String] = {
    val argument = """\{(\d+)\}""".r
    def replaceArgument(m: Match): String =  {
      val index = m.group(1).toInt
      val parameter: Definition = args(index.min(args.length - 1))
      val parameterType: String = parameter.definitionType.variableType.forCSharp
      <paramref name={parameter.name}/> toString
    }
    val result = paragraphs.map(p => argument.replaceAllIn(p, replaceArgument _))
    println(result)
    result
  }

}