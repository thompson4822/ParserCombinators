package msl.generator.csharp

import msl.generator.Generator
import msl._
import dsl.Types._

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/3/11
 * Time: 5:12 AM
 * To change this template use File | Settings | File Templates.
 */

class DtoGen(dto: Dto) extends Generator with CommonNet{
  lazy val namespace = Context.netDto

  lazy val filepath = List(Context.netPath, namespace).mkString("/")

  lazy val filename = dto.name + "_Gen.cs"

  lazy val projectFileMapping = (namespace -> filename)

  def properties = dto.definitions.map {
    d =>
    "        public " + d.definitionType.forCSharp + " " + d.name.capitalize + " { get; set; } "
  }.mkString(nl)

  override def toString = generationNotice +
    """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Mueller.Han.Utility;
using Mueller.Han.Utility.Enumerations;

namespace """ + namespace + """
{
    public class """ + dto.name + """
    {
""" + properties + """
        #region NULL object pattern
        public static readonly """ + dto.name + """ NULL = new Null""" + dto.name + """();
        private class Null""" + dto.name + " : " + dto.name + """ { }
        #endregion NULL object pattern
    }
}
    """

}