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

class FactoryTestClass(factory: Factory) extends Generator {
  val namespace = Context.netFactoryTest

  lazy val filepath = List(Context.netPath, Context.netFactoryTest).mkString("/")

  lazy val filename = factory.name + "Tests.cs"

  override def overwrite = false

  def methodDefinition(m: Method) =
    """
        [TestMethod]
        public void Test""" + m.name + """()
        {
            throw new NotImplementedException();
        }
    """

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

namespace Mueller.Han.Business.Test
{
    public partial class """ + factory.name + """Tests
    {

""" + factory.methods.map(methodDefinition).mkString + """

    }
}
  """
}