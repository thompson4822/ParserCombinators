package msl.generator.csharp

import msl.dsl.Types._
import msl.generator._
import msl._
/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/3/11
 * Time: 6:07 AM
 * To change this template use File | Settings | File Templates.
 */

class DaoInterfaceGen(dao: Dao) extends Generator with CommonNet {
  lazy val namespace = Context.netDao

  lazy val filePath = List(Context.netPath, namespace).mkString("/")

  lazy val filename = "I" + dao.name + "_Gen.cs"

  lazy val projectFileMapping = (namespace -> filename)

  val regex = """([a-zA-Z][a-zA-Z0-9_]*)Dao""".r

  def entityName = {
    val regex(result) = dao.name
    result
  }

  def documentation = (dao.documentation match {
    case None => List(dao.name)
    case Some(x) =>
      val summary = new SummaryTextParser(x)
      summary.paragraphs
  }).map(str => "    ///  <para>" + str + "</para>").mkString(nl)

  def methodSignatures = dao.methods.map(m => List(methodDocumentation(m), "        " + m.cSharpSignature + ";").mkString(nl)).mkString(nl + nl)

  override def toString = generationNotice +
    """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Mango.Dao.Domain;
using Mango.Utility;
using Mango.Utility.Enumerations;

namespace """ + namespace + """
{
""" + interfaceDocumentation(dao.name, docs = dao.documentation) + """
    public partial interface I""" + dao.name + " : IGenericDao<" + entityName + """, long>
    {
""" + methodSignatures + """
    }
}    """

}
