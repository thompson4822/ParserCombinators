package msl.generator.csharp

import msl.generator.StringExtensions._
import msl.generator._
import msl.Context
import msl.dsl.Types.{Method, Service}

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 5:49 AM
 * To change this template use File | Settings | File Templates.
 */

class ServiceGen(service: Service) extends Generator with CommonNet{
  lazy val namespace = Context.netService

  lazy val filepath = List(Context.netPath, Context.netService).mkString("/")
  lazy val filename = service.name + "_Gen.cs"

  lazy val projectFileMapping = (namespace -> filename)

  val factoryInterfaceName = service.name.splitId.reverse match {
    case "Service" :: rest => "I" + rest.reverse.mkString + "Factory"
    case _ => "I" + service.name + "Factory"
  }

  private def actionMethod(m: Method) =
    """
        public """ + m.cSharpSignature + """
        {
            string description = serviceName + """ + "\"" + m.name.splitId.mkString(" ") + "\"" + """;
            FactoryCall(description, f => f.""" + m.name + """(""" + m.parameters.map(_.name).mkString(", ") + """));
            LogFactoryComplete(description);
        }
    """

  private def functionMethod(m: Method) =
    """
        public """ + m.cSharpSignature + """
        {
            string description = serviceName + """ + "\"" + m.name.splitId.mkString(" ") + "\"" + """;
            """ + m.returnType.forCSharp + """ result = FactoryCall(description, f => f.""" + m.name + """(""" + m.parameters.map(_.name).mkString(", ") + """));
            LogFactoryComplete(description);
            return result;
        }
    """

  val methods = service.methods.map { m =>
    if(m.returnType.forCSharp == "void")
      actionMethod(m)
    else
      functionMethod(m)
  }.mkString

  override def toString = generationNotice + """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Mueller.Han.Dto;
using Mueller.Han.Business;
using FluorineFx;
using Mueller.Han.Business.Interfaces;
using """ + namespace + """.Interfaces;
using log4net;
using log4net.Config;
using Mueller.Han.Service.Common;
using Mueller.Han.Utility;
using Mueller.Han.Utility.Enumerations;

namespace """ + namespace + """
{
    [RemotingService(""" + "\"" + service.name.unCapitalize + "\"" + """)]
    public partial class """ + service.name + """ : BaseService<""" + factoryInterfaceName + ", " + service.name + """>, I""" + service.name + """
    {
        #region Private members
        private const string serviceName = "(""" + service.name.unCapitalize + """) - ";
        #endregion Private members

        #region Services
""" + methods + """

        #endregion Services
    }
}
  """
}