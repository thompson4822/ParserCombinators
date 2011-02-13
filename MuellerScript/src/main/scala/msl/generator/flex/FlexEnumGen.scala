package msl.generator.flex

import msl.generator.Generator
import msl.dsl.Types._
import msl.Context

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/11/11
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */

class FlexEnumGen(enum: Enum, flexPackage: FlexPackage) extends Generator with CommonFlexEnum {
  val namespace = List(Context.flexPackage(flexPackage), "enums").mkString(".")
  //val namespace = List(Context.flexBasePackage, Context.flexPackage, "dtos").mkString(".")

  lazy val filepath = List(Context.flexPath(flexPackage), "enums").mkString("/")

  lazy val filename = enum.name + ".as"

  def getByValue(name: String) = """
        public static function getByValue(value:Object):""" + name + """
        {
            return VALUE_MAP[value] as """ + name + """;
        }
    """

  override def toString = fileContent(enum.name, enum.items)

}
