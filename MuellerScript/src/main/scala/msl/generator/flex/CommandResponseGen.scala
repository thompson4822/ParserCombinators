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

class CommandResponseGen(method: Method, flexPackage: FlexPackage) extends Generator {
  val namespace = List(Context.flexPackage(flexPackage), "events").mkString(".")

  lazy val filepath = List(Context.flexPath(flexPackage), "events").mkString("/")
  lazy val filename = method.name + "Response.as"

  val flexType = method.returnType.forFlex

  val parameterFields: String =
    if(method.returnType.forCSharp != "void")
      "    private var _result:" + flexType + ";\n"
    else ""

  val parameterGetters: String =
    if(method.returnType.forCSharp != "void")
      "    public function get result():" + flexType + "{ return _result; }\n"
    else ""

  val constructorParameters: String =
    if(method.returnType.forCSharp != "void")
      "resultParam:" + flexType
    else ""

  val constructorBody: String =
    if(method.returnType.forCSharp != "void")
      "      _result = resultParam;\n"
    else ""

  override def toString = """
package """ + namespace + """
{
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