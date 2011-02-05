package msl
package generator
package csharp

import dsl.Types.Factory

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 5:51 AM
 * To change this template use File | Settings | File Templates.
 */

class FactoryTestInterfaceGen(factory: Factory) extends Generator {
  val namespace = List(Context.netFactoryTest, "Interfaces").mkString(".")

  lazy val filepath = List(Context.netPath, Context.netFactoryTest, "Interfaces").mkString("/")

  lazy val filename = className + "Tests_Gen.cs"

  lazy val className = "I" + factory.name

  val methodInterfaces = factory.methods.map(m => "        void Test" + m.name + "();").mkString("\n")

  override def toString = generationNotice + """
using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;

namespace """ + namespace + """
{
    public partial interface """ + className + """Tests
    {
""" + methodInterfaces + """
    }
}
  """
}