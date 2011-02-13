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

class FlagsGen(flags: Flags) extends Generator {
  lazy val namespace = List(Context.netUtility, "Enumerations").mkString(".")

  lazy val filepath = List(Context.netPath, Context.netUtility, "Enumerations").mkString("/")

  lazy val filename = flags.name + ".cs"

  lazy val projectFileMapping =  (Context.netUtility -> List("Enumerations", filename).mkString("\\"))

  override def toString = """
Under Construction.  Check back later.
    """
}