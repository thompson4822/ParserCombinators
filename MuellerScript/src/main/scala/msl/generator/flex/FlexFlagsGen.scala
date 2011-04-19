package msl.generator.flex

import msl.generator.Generator
import msl.Context
import msl.dsl.Types.{FlexPackage, Flags}

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/11/11
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */

class FlexFlagsGen(flags: Flags, flexPackage: FlexPackage) extends Generator with CommonFlexEnum {
  val namespace = List(Context.flexPackage(flexPackage), "enums").mkString(".")

  lazy val filePath = List(Context.flexPath(flexPackage), "enums").mkString("/")

  lazy val filename = flags.name + ".as"

  def getByValue(name: String) = """
        public static function getByValue(value:Object):ArrayValue
        {
            var result:ArrayValue = new ArrayValue();
            for each(var item:""" + name + """ in VALUE_MAP)
            {
                if(value & (item.value) != 0)
                {
                    result.push(item)
                }
            }
            return result;
        }
    """

  override def toString = fileContent(flags.name, flags.items)
}