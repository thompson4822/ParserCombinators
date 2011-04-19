/*
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/29/11
 * Time: 7:24 AM
 */
package msl.generator.csharp
import msl._
import generator.Generator
import msl.dsl.Types._

class FactoryTestClass(factory: Factory) extends Generator with CommonNet{
  lazy val namespace = Context.netFactoryTest

  lazy val filePath = List(Context.netPath, Context.netFactoryTest).mkString("/")

  lazy val filename = factory.name + "Tests.cs"

  lazy val projectFileMapping = (namespace -> filename)

  override def overwrite = false

  val methodDefinitions = {
    // Because method names can be the same, we may need to discriminate
    def adjustedName(method: Method): String = {
      val methodsNamedTheSame = factory.methods.filter(_.name == method.name)
      (methodsNamedTheSame.length, methodsNamedTheSame.indexOf(method)) match {
        case (x, y) if(x > 1 && y > 0) => method.name + y
        case _ => method.name
      }
    }
    factory.methods.map(m =>
      """
        [TestMethod]
        public void Test""" + adjustedName(m) + """()
        {
            throw new NotImplementedException();
        }
      """).mkString
  }

  override def toString = """
using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Mueller.Han.Dao;
using Moq;
using Mueller.Han.Dto;
using Mueller.Han.Dao.Domain;
using NHibernate.Criterion;
using System.Linq.Expressions;
using Mueller.Han.Utility;
using Mueller.Han.Utility.Enumerations;

namespace Mueller.Han.Business.Test
{
    /// <summary>
    ///
    /// </summary>
    public partial class """ + factory.name + """Tests
    {

""" + methodDefinitions + """

    }
}
  """
}