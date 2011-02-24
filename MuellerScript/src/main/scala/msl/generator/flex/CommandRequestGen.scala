package msl.generator.flex

import msl.generator.Generator
import msl.Context
import msl.dsl.Types.{FlexPackage, Method, Command}

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 5:46 AM
 * To change this template use File | Settings | File Templates.
 */

class CommandRequestGen(method: Method, flexPackage: FlexPackage) extends Generator with CommonFlex {
  val namespace = List(Context.flexPackage(flexPackage), "events").mkString(".")

  lazy val filepath = List(Context.flexPath(flexPackage), "events").mkString("/")

  lazy val filename = method.name + "Request.as"

  val parameters = method.parameters

  override def overwrite = false

  val parameterFields: String =
    parameters.map(d => "        private var _" + d.name + ":" + d.definitionType.forFlex + ";").mkString(nl)

  val parameterGetters: String =
    parameters.map(d => "        public function get " + d.name + "():" + d.definitionType.forFlex + "{ return _" + d.name + "; }").mkString(nl)

  val constructorParameters: String = parameters.map(d => d.name + "Param:" + d.definitionType.forFlex).mkString(", ")

  val constructorBody: String = parameters.map(d => "            _" + d.name + " = " + d.name + "Param;").mkString(nl)

  override def toString = """
package """ + namespace + """
{
""" + dtoImports(method.parameters) + """

    public class """ + method.name + """Request
    {
""" + parameterFields + nl + nl + parameterGetters + nl + """
        public function """ + method.name + """Request(""" + constructorParameters + """)
        {
""" + constructorBody + """
        }
    }
}
"""
}