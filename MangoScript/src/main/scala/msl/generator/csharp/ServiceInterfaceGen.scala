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

  lazy val filePath = List(Context.netPath, Context.netService, "Interfaces").mkString("/")
  lazy val filename = className + "_Gen.cs"

  lazy val className = "I" + service.name

  lazy val projectFileMapping = (Context.netService -> List("Interfaces", filename).mkString("\\"))

  val methodSignatures = service.methods.map(m => List(methodDocumentation(m), "        " + m.cSharpSignature + ";").mkString(nl)).distinct.mkString(nl+nl)

  override def toString = generationNotice + """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Mango.Utility;
using Mango.Utility.Enumerations;
using Mango.Dto;
namespace """ + namespace + """
{
""" + interfaceDocumentation(service.name, docs = service.documentation) + """
    public partial interface """ + className + """
    {
""" + methodSignatures + """
    }
}
  """
}