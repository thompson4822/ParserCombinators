package msl.generator.csharp

import msl.generator.Generator
import msl.dsl.Types._
import msl.Context

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/8/11
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */

class DaoClassMaker(dao: Dao) extends Generator with CommonNet{
  lazy val namespace = List(Context.netDao, "NHibernate").mkString(".")

  lazy val filePath = List(Context.netPath, namespace).mkString("/")

  lazy val filename = dao.name + ".cs"

  lazy val projectFileMapping = (namespace -> filename)

  override def overwrite = false

  def methodDefinition(m: Method) =
    """
        public """ + m.cSharpSignature + """
        {
            throw new NotImplementedException();
        }
    """

  override def toString =
    """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Mango.Dao.Domain;
using Mango.Utility;
using Mango.Utility.Enumerations;

namespace Mango.Dao.NHibernate
{
    /// <summary>
    ///
    /// </summary>
    public partial class """ + dao.name + """
    {
""" + dao.methods.map(methodDefinition).mkString + """
    }
}
    """

}