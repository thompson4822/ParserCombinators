package msl.generator.csharp

import msl._
import msl.generator._
import msl.dsl.Types.Factory

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 5:50 AM
 * To change this template use File | Settings | File Templates.
 */

class FactoryInterfaceGen(factory: Factory) extends Generator {
  val namespace = List(Context.netFactory, "Interfaces").mkString(".")

  lazy val filepath = List(Context.netPath, Context.netFactory, "Interfaces").mkString("/")

  lazy val filename = className + "_Gen.cs"

  lazy val className = "I" + factory.name

  val methodSignatures = factory.methods.map(m => "        " + m.cSharpSignature + ";").mkString("\n")

  override def toString = """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Mueller.Han.Dto;
using Mueller.Han.Utility;

namespace """ + namespace + """
{
    public partial interface """ + className + """
    {
""" + methodSignatures + """
    }
}
  """
}