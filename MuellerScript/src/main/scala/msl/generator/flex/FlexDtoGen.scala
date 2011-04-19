package msl.generator.flex

import msl.generator.Generator
import msl._
import dsl.Types._

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/3/11
 * Time: 5:13 AM
 * To change this template use File | Settings | File Templates.
 */

class FlexDtoGen(dto: Dto, flexPackage: FlexPackage) extends Generator with CommonFlex {
  val namespace = List(Context.flexPackage(flexPackage), "dtos").mkString(".")

  lazy val filePath = List(Context.flexPath(flexPackage), "dtos").mkString("/")

  lazy val filename = dto.name + ".as"

  def definitions = dto.definitions.map {
    d =>
"""        public var """ + d.name + ":" + d.definitionType.forFlex + """;
"""
  }.mkString

  override def toString = generationNotice +
    """
package """ + namespace + """
{
""" + dtoImports(dto.definitions) + """

    [RemoteClass (alias=""" + "\"" + List(Context.netDto, dto.name).mkString(".") + "\"" + """)]
    public class """ + dto.name + """
    {
""" + definitions + """
    }
}
    """
}