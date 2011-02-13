package msl.generator.flex

import msl.dsl.Types.EnumItem
import msl.generator.Generator

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/13/11
 * Time: 6:13 AM
 * To change this template use File | Settings | File Templates.
 */

trait CommonFlexEnum {
  self: Generator =>

  import msl.generator.StringExtensions._
  def constName(string: String): String = string.splitId.map(_.toUpperCase).mkString("_")

  def description(string: String): String = string.splitId.map(_.capitalize).mkString(" ")

  def staticSection(name: String, items: List[EnumItem]) = items.map { item =>
    "        public static const " + constName(item.name) + ":" + name + " = new " + name + "(" + item.value + ", \"" + description(item.name) + "\");"
  }.mkString(nl)

  def getByValue(name: String): String

  def fileContent(name: String, items: List[EnumItem]) = """
package """ + namespace + """
{
    public class """ + name + """
    {
""" + staticSection(name, items) + """

        private static var _VALUE_MAP:Object = null;

        private static function get VALUE_MAP():Object
        {
            if (_VALUE_MAP == null)
            {
                _VALUE_MAP = new Object();
                for each (var enum:""" + name + """ in ALL_VALUES)
                {
                    _VALUE_MAP[enum.value] = enum;
                }
            }
            return _VALUE_MAP;
        }

        public static const ALL_VALUES:Array = [""" + items.map(e => constName(e.name)).mkString(", ") + """];

""" + getByValue(name) + """

        public var value:int;
        public var label:String;

        public function """ + name + """(val:int, lbl:String)
        {
            value = val;
            label = lbl;
        }
    }
}
  """


}