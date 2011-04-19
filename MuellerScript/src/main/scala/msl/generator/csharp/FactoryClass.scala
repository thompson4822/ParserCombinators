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

  lazy val filePath = List(Context.netPath, Context.netFactory).mkString("/")

  lazy val filename = factory.name + ".cs"

  lazy val projectFileMapping = (namespace -> filename)

  override def overwrite = false

  def methodDefinition(m: Method) = {
    val body: String = (m.returnType.variableType, m.returnType.genericType) match {
      case (dto: Dto, Some(_)) => "return Enumerable.Range(0, 20).Select(x => (new " + dto.name + "Source()).Next(Session)).ToList();"
      case (dto: Dto, _) => "return (new " + dto.name + "Source()).Next(Session);"
      case _ => "throw new NotImplementedException();"
    }
    """"
        /// <summary>
        ///
        /// </summary>
        public """ + m.cSharpSignature + "{ " + body + """ }
    """
  }


  override def toString = """
using Mueller.Han.Business.Interfaces;
using Mueller.Han.Dao;
using Mueller.Han.Dto;
using Mueller.Han.Dto.DataSource;
using Spring.Transaction.Interceptor;
using System;
using System.Collections.Generic;
using System.Linq;
using Mueller.Han.Dao.Domain;
using Mueller.Han.Utility;
using Mueller.Han.Utility.Enumerations;

namespace """ + namespace + """
{
    /// <summary>
    ///
    /// </summary>
    public partial class """ + factory.name + """
    {
""" + factory.methods.map(methodDefinition).distinct.mkString(nl) + """
    }
}
  """

}