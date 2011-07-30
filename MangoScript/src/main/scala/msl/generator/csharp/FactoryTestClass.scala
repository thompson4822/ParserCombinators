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
import javax.swing.UIDefaults.LazyInputMap

class FactoryTestClass(val factory: Factory) extends Generator with TestMethodDiscriminator with CommonNet {

  lazy val namespace = Context.netFactoryTest

  lazy val filePath = List(Context.netPath, Context.netFactoryTest).mkString("/")

  lazy val filename = factory.name + "Tests.cs"

  lazy val projectFileMapping = (namespace -> filename)

  def methods: List[Method] = factory.methods

  override def overwrite = false

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