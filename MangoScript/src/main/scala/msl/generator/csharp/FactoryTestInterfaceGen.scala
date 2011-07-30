package msl
package generator
package csharp

import dsl.Types.{Method, Factory}

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 5:51 AM
 * To change this template use File | Settings | File Templates.
 */

class FactoryTestInterfaceGen(factory: Factory) extends Generator with TestMethodDiscriminator with CommonNet {
  lazy val namespace = List(Context.netFactoryTest, "Interfaces").mkString(".")

  lazy val filePath = List(Context.netPath, Context.netFactoryTest, "Interfaces").mkString("/")

  lazy val filename = className + "Tests_Gen.cs"

  lazy val className = "I" + factory.name

  lazy val projectFileMapping = (Context.netFactoryTest -> List("Interfaces", filename).mkString("\\"))

  def methods: List[Method] = factory.methods

  override def toString = generationNotice + """
using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Mango.Utility;
using Mango.Utility.Enumerations;

namespace """ + namespace + """
{
    public partial interface """ + className + """Tests
    {
""" + methodInterfaces + """
    }
}
  """
}