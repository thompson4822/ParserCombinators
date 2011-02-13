package msl.generator.csharp

import msl.generator.Generator
import msl.Context
import msl.dsl.Types._

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/13/11
 * Time: 7:32 AM
 * To change this template use File | Settings | File Templates.
 */

class EnumGen(enum: Enum) extends Generator with CommonNet {
  lazy val namespace = List(Context.netUtility, "Enumerations").mkString(".")

  lazy val filepath = List(Context.netPath, Context.netUtility, "Enumerations").mkString("/")

  lazy val filename = enum.name + ".cs"

  lazy val projectFileMapping =  (Context.netUtility -> List("Enumerations", filename).mkString("\\"))

  override def toString = """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace """ + namespace + """
{
    public enum """ + enum.name + """
    {
""" + enum.items.map(item => "        " + item.name + " = " + item.value).mkString("," + nl) + """
    }
}
    """
}