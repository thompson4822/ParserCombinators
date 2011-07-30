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

class FactoryInterfaceGen(factory: Factory) extends Generator with CommonNet{
  lazy val namespace = List(Context.netFactory, "Interfaces").mkString(".")

  lazy val filePath = List(Context.netPath, Context.netFactory, "Interfaces").mkString("/")

  lazy val filename = className + "_Gen.cs"

  lazy val className = "I" + factory.name

  lazy val projectFileMapping = (Context.netFactory -> List("Interfaces", filename).mkString("\\"))

  val methodSignatures = factory.methods.map(m => List(methodDocumentation(m), "        " + m.cSharpSignature + ";").mkString(nl)).distinct.mkString(nl+nl)

  override def toString = generationNotice + """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Mango.Dto;
using Mango.Utility;
using Mango.Utility.Enumerations;
using Mango.Dao.Domain;

namespace """ + namespace + """
{
""" + interfaceDocumentation(factory.name, docs = factory.documentation) + """
    public partial interface """ + className + """
    {
""" + methodSignatures + """
    }
}
  """
}