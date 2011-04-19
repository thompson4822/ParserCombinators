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

class FactoryTestInterfaceGen(factory: Factory) extends Generator with CommonNet {
  lazy val namespace = List(Context.netFactoryTest, "Interfaces").mkString(".")

  lazy val filePath = List(Context.netPath, Context.netFactoryTest, "Interfaces").mkString("/")

  lazy val filename = className + "Tests_Gen.cs"

  lazy val className = "I" + factory.name

  lazy val projectFileMapping = (Context.netFactoryTest -> List("Interfaces", filename).mkString("\\"))

  val methodInterfaces = {
    // Because method names can be the same, we may need to discriminate
    def adjustedName(method: Method): String = {
      val methodsNamedTheSame = factory.methods.filter(_.name == method.name)
      (methodsNamedTheSame.length, methodsNamedTheSame.indexOf(method)) match {
        case (x, y) if(x > 1 && y > 0) => method.name + y
        case _ => method.name
      }
    }
    factory.methods.map(m => "        void Test" + adjustedName(m) + "();").mkString(nl)
  }

  override def toString = generationNotice + """
using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Mueller.Han.Utility;
using Mueller.Han.Utility.Enumerations;

namespace """ + namespace + """
{
    public partial interface """ + className + """Tests
    {
""" + methodInterfaces + """
    }
}
  """
}