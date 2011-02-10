package msl.generator.csharp

import msl.generator.Generator
import msl.dsl.Types.Service
import msl.Context

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 5:49 AM
 * To change this template use File | Settings | File Templates.
 */

class ServiceInterfaceGen(service: Service) extends Generator with CommonNet{
  lazy val namespace = List(Context.netService, "Interfaces").mkString(".")

  lazy val filepath = List(Context.netPath, Context.netService, "Interfaces").mkString("/")
  lazy val filename = className + "_Gen.cs"

  lazy val className = "I" + service.name

  lazy val projectFileMapping = (Context.netService -> List("Interfaces", filename).mkString("\\"))

  val methodSignatures = service.methods.map(m => "        " + m.cSharpSignature + ";").mkString("\n")

  override def toString = generationNotice + """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Mueller.Han.Utility;
using Mueller.Han.Utility.Enumerations;
using Mueller.Han.Dto;
namespace """ + namespace + """
{
    public partial interface """ + className + """
    {
""" + methodSignatures + """
    }
}
  """
}