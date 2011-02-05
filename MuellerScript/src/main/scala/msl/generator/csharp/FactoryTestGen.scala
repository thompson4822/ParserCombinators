package msl.generator.csharp

import msl.Context
import msl.dsl.Types.Factory
import msl.generator._
import msl.generator.StringExtensions._
/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 5:51 AM
 * To change this template use File | Settings | File Templates.
 */

class FactoryTestGen(factory: Factory) extends Generator {
  val namespace = Context.netFactoryTest

  lazy val filepath = List(Context.netPath, Context.netFactoryTest).mkString("/")

  lazy val filename = factory.name + "Tests_Gen.cs"

  val deps = factory.dependencies

  val mockDefinitions =
    deps.map(d => "        private Mock<I" + d.definitionType.forCSharp + "> mock" + d.name.capitalize + ";").mkString("\n")

  val mockInitializations =
    deps.map(d => "            mock" + d.name.capitalize + " = new Mock<I" + d.definitionType.forCSharp + ">();").mkString("\n")

  val mockAssignments =
    deps.map(d => "            " + factory.name.unCapitalize + "." + d.name.capitalize + " = mock" + d.name.capitalize + ".Object;").mkString("\n")

  override def toString = generationNotice + """
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
using Mueller.Han.Business.Test.Interfaces;

namespace Mueller.Han.Business.Test
{
    [TestClass]
    public partial class """ + factory.name + """Tests : I""" + factory.name + """Tests
    {
        private """ + factory.name + " " + factory.name.unCapitalize +""";
""" + mockDefinitions + """

        [TestInitialize()]
        public void MyTestInitialize()
        {
            """ + factory.name.unCapitalize + """ = new """ + factory.name + """();
""" + mockInitializations + """
""" + mockAssignments + """
        }
    }
}
  """
}