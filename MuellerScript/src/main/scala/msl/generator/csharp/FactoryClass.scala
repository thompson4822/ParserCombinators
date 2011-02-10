/*
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/29/11
 * Time: 6:47 AM
 */
package msl.generator.csharp

import msl.generator.Generator
import msl._
import dsl.Types._

class FactoryClass(factory: Factory) extends Generator with CommonNet{
  lazy val namespace = Context.netFactory

  lazy val filepath = List(Context.netPath, Context.netFactory).mkString("/")

  lazy val filename = factory.name + ".cs"

  lazy val projectFileMapping = (namespace -> filename)

  override def overwrite = false

  def methodDefinition(m: Method) =
    """
        public """ + m.cSharpSignature + """
        {
            throw new NotImplementedException();
        }
    """


  override def toString = """
using Mueller.Han.Business.Interfaces;
using Mueller.Han.Dao;
using Mueller.Han.Dto;
using Spring.Transaction.Interceptor;
using System;
using System.Collections.Generic;
using Mueller.Han.Utility;
using Mueller.Han.Utility.Enumerations;

namespace """ + namespace + """
{
    public partial class """ + factory.name + """
    {
""" + factory.methods.map(methodDefinition).mkString + """
    }
}
  """

}