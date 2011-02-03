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

class FlexDtoGen(dto: Dto) extends Generator {
  val namespace = List(Context.flexBasePackage, Context.flexPackage, "dtos").mkString(".")

  lazy val filepath = List(Context.flexPath, "dtos").mkString("/")

  lazy val filename = dto.name + ".as"

  override def toString =
    """
    """
}