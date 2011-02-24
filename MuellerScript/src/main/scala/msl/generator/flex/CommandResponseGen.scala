package msl.generator.flex

import msl.generator.Generator
import msl.Context
import msl.dsl.Types.{FlexPackage, Method, Command, Definition}

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 5:46 AM
 * To change this template use File | Settings | File Templates.
 */

class CommandResponseGen(method: Method, flexPackage: FlexPackage) extends Generator with CommonFlex {
  val namespace = List(Context.flexPackage(flexPackage), "events").mkString(".")

  lazy val filepath = List(Context.flexPath(flexPackage), "events").mkString("/")
  lazy val filename = method.name + "Response.as"

  val flexType = method.returnType.forFlex

  override def overwrite = false

  val parameterFields: String =
    if(method.returnType.forCSharp != "void")
      "        private var _result:" + flexType + ";" + nl
    else ""

  val parameterGetters: String =
    if(method.returnType.forCSharp != "void")
      "        public function get result():" + flexType + "{ return _result; }" + nl
    else ""

  val constructorParameters: String =
    if(method.returnType.forCSharp != "void")
      "resultParam:" + flexType
    else ""

  val constructorBody: String =
    if(method.returnType.forCSharp != "void")
      "            _result = resultParam;" + nl
    else ""

  override def toString = """
package """ + namespace + """
{
""" + dtoImports(List(Definition("", method.returnType))) + """

    public class """ + method.name + """Response
    {
""" + parameterFields + "\n" + parameterGetters + """
        public function """ + method.name + """Response(""" + constructorParameters + """)
        {
""" + constructorBody + """
        }
    }
}
"""
}