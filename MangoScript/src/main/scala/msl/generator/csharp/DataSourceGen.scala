package msl.generator.csharp

import msl.generator.Generator
import msl.Context
import msl.dsl.Types.{DtoDefinition, Dto}

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 3/12/11
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 */

class DataSourceGen(dto: Dto) extends Generator with CommonNet {

  // TODO: The following definitions are ALL WRONG, I just copied them from DtoGen to pacify the compiler for the moment.
  lazy val namespace = List(Context.netDto, "DataSource").mkString(".")

  lazy val filePath = List(Context.netPath, Context.netDto, "DataSource").mkString("/")

  lazy val filename = dto.name + "Source_Gen.cs"

  lazy val projectFileMapping = (Context.netDto -> List("DataSource", filename).mkString("\\"))

  // The complication in the following reflects the fact that if the return type is a Dto, we actually want to make use
  // of the generated DtoSource, unless it is overridden with some other data source.
  def dataSourceMappings = {
    def sourceName(definition: DtoDefinition): Option[String] = {
      (definition.definitionType.genericType, definition.definitionType.variableType, definition.dataSource) match {
        case (Some(_), _, _) => None
        case (_, _, Some(dataSource)) => Some(dataSource.name)
        case (_, varType: Dto, _) => Some(varType.name + "Source")
        case _ => None
      }
    }
    dto.definitions.filter(sourceName(_) != None).
    map {
      definition =>
        val arguments = definition.dataSource.map(_.arguments).getOrElse("()")
        "               .Setup(c => c." + definition.name + ").Use<" + sourceName(definition).get + ">" + arguments
    }.
    mkString(nl)
  }

  override def toString = """
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Mango.Dto;
using AutoPoco.Engine;
using AutoPoco;
using AutoPoco.DataSources;

namespace """ + namespace + """
{
    public class """ + dto.name + """Source : DatasourceBase<""" + dto.name + """>
    {
        static IGenerationSessionFactory factory = AutoPocoContainer.Configure(x =>
        {
            x.Conventions(c =>
            {
                c.UseDefaultConventions();
            });
            x.AddFromAssemblyContainingType<""" + dto.name + """>();
            x.Include<""" + dto.name + """>()
""" + dataSourceMappings + """
               ;
        });

        static IGenerationSession Session = factory.CreateSession();

        public override """ + dto.name + """ Next(IGenerationSession session)
        {
            return Session.Single<""" + dto.name + """>().Get();
        }
    }
}
  """
}