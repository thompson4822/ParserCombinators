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

class FactoryClass(factory: Factory) extends Generator {
  val namespace = Context.netFactory

  lazy val filepath = List(Context.netPath, Context.netFactory).mkString("/")

  lazy val filename = factory.name + ".cs"

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

namespace """ + namespace + """
{
    public partial class """ + factory.name + """
    {
""" + factory.methods.map(methodDefinition).mkString + """
    }
}
  """

}