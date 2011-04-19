package msl
package generator
package csharp

import dsl.Types.{Dto, Factory}

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/18/11
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */

class FactoryGen(factory: Factory) extends Generator with CommonNet{
  lazy val namespace = Context.netFactory

  lazy val filePath = List(Context.netPath, Context.netFactory).mkString("/")

  lazy val filename = factory.name + "_Gen.cs"

  lazy val projectFileMapping = (namespace -> filename)

  val deps = factory.dependencies

  val dependencyDeclarations = deps.map(d => "        private I" + d.forCSharp + ";").mkString(nl)

  val dependencySetters = deps.map(d =>
    """
        public I""" + d.definitionType.forCSharp + " " + d.name.capitalize + """
        {
            set { """ + d.name + """ = value; }
        }
    """
  ).mkString(nl)

  def autoPocoDefinitions = {
    val uniqueDtos = factory.methods.map(_.returnType.variableType).collect{case x: Dto => x}.distinct
    uniqueDtos.map(dto => "            x.AddFromAssemblyContainingType<" + dto.name + ">();").mkString(nl)
  }

  override def toString = generationNotice +
    """
using Mueller.Han.Business.Interfaces;
using Mueller.Han.Dao;
using Mueller.Han.Dto;
using Spring.Transaction.Interceptor;
using System;
using System.Collections.Generic;
using System.Linq;
using Mueller.Han.Utility;
using Mueller.Han.Utility.Enumerations;
using Mueller.Han.Dao.Domain;
using AutoPoco;
using AutoPoco.Engine;

namespace """ + namespace + """
{
    /// <summary>
    ///
    /// </summary>
    public partial class """ + factory.name + " : " + "I" + factory.name + """
    {
        //public static IGenerationSession Session = Factory.CreateSession();
        public IGenerationSession Session;

        private static IGenerationSessionFactory Factory = AutoPocoContainer.Configure(x =>
        {
            x.Conventions(c =>
            {
                c.UseDefaultConventions();
            });
        });

        public """ + factory.name + """()
        {
            Session = Factory.CreateSession();
        }

""" + dependencyDeclarations + """
""" + dependencySetters + """
    }
}
    """
}