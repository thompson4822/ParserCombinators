package msl
package generator
package csharp

import dsl.Types.Factory

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/18/11
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */

class FactoryGen(factory: Factory) extends Generator {
  val namespace = Context.netFactory

  lazy val filepath = List(Context.netPath, Context.netFactory).mkString("/")

  lazy val filename = factory.name + "_Gen.cs"

  val deps = factory.dependencies

  val dependencyDeclarations = deps.map(d => "        private " + d.forCSharp + ";").mkString("\n")

  val dependencySetters = deps.map(d =>
    """
        public """ + d.definitionType.forCSharp + " " + d.name.capitalize + """
        {
            set { """ + d.name + """ = value; }
        }
    """
  ).mkString("\n")

  override def toString =
    """
using Mueller.Han.Business.Interfaces;
using Mueller.Han.Dao;
using Mueller.Han.Dto;
using Spring.Transaction.Interceptor;
using System;
using System.Collections.Generic;

namespace """ + namespace + """
{
    public partial class """ + factory.name + " : " + "I" + factory.name + """
    {
""" + dependencyDeclarations + """
""" + dependencySetters + """
    }
}
    """
}