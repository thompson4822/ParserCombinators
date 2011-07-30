package msl.generator.csharp

import msl.dsl.Types.Dao
import msl.generator.Generator
import msl._
/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/3/11
 * Time: 5:47 AM
 * To change this template use File | Settings | File Templates.
 */

class DaoGen(dao: Dao) extends Generator {
  lazy val namespace = List(Context.netDao, "NHibernate").mkString(".")

  lazy val filePath = List(Context.netPath, namespace).mkString("/")

  lazy val filename = dao.name + "_Gen.cs"

  val regex = """([a-zA-Z][a-zA-Z0-9_]*)Dao""".r

  def entityName = {
    val regex(result) = dao.name
    result
  }

  override def toString = generationNotice +
    """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Mango.Dao.Domain;
using NHibernate;
using NHibernate.Criterion;
using Mango.Utility;
using Mango.Utility.Enumerations;

namespace """ + namespace + """
{
    /// <summary>
    ///
    /// </summary>
    public partial class """ + dao.name + " : GenericDao<" + entityName + ", long>, I" + dao.name + """
    {

    }
}    """

}
