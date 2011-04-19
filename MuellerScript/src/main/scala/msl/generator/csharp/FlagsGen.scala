package msl.generator.csharp

import msl.dsl.Types.Flags
import msl.generator.Generator
import msl.Context

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/13/11
 * Time: 7:32 AM
 * To change this template use File | Settings | File Templates.
 */

class FlagsGen(flags: Flags) extends Generator with CommonNet {
  lazy val namespace = List(Context.netUtility, "Enumerations").mkString(".")

  lazy val filePath = List(Context.netPath, Context.netUtility, "Enumerations").mkString("/")

  lazy val filename = flags.name + ".cs"

  lazy val projectFileMapping =  (Context.netUtility -> List("Enumerations", filename).mkString("\\"))

  override def toString = """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace """ + namespace + """
{
    /// <summary>
    ///
    /// </summary>
    [Flags]
    public enum """ + flags.name + """
    {
""" + flags.items.map(item => "        " + item.name + " = " + item.value).mkString("," + nl) + """
    }
}
    """
}