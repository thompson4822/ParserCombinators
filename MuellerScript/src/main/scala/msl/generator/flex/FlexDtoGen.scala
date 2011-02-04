package msl.generator.flex

import msl.generator.Generator
import msl._
import dsl.Types._

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/3/11
 * Time: 5:13 AM
 * To change this template use File | Settings | File Templates.
 */

class FlexDtoGen(dto: Dto, flexPackage: FlexPackage) extends Generator with CommonFlex {
  val namespace = List(Context.flexPackage(flexPackage), "dtos").mkString(".")
  //val namespace = List(Context.flexBasePackage, Context.flexPackage, "dtos").mkString(".")

  lazy val filepath = List(Context.flexPath(flexPackage), "dtos").mkString("/")

  lazy val filename = dto.name + ".as"

  def dtoImports = dto.definitions.map{ _.definitionType.variableType match {
    case d: Dto =>
      val elementDto: Dto = Context.elements.get(d.name).get.asInstanceOf[Dto]
      val dtoNamespace = List(Context.flexPackage(elementDto.flexPackage.get), "dtos").mkString(".")
      if(dtoNamespace != namespace)
        """    import """ + dtoNamespace + "." + d.name + """;
""" else ""
    case _ => ""
  }
  }.mkString

  def definitions = dto.definitions.map {
    d =>
"""        public var """ + d.name + ":" + d.definitionType.forFlex + """;
"""
  }.mkString

  override def toString =
    """
package """ + namespace + """ {
    import mx.collections.ArrayCollection;
""" + dtoImports(dto.definitions) + """
    [Bindable]
    [RemoteClass (alias=""" + "\"" + List(Context.netDto, dto.name).mkString(".") + "\"" + """)]
    public class """ + dto.name + """
    {
""" + definitions + """
    }
}
    """
}