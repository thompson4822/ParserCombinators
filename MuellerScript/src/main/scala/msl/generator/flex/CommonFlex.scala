package msl.generator.flex

import msl.dsl.Types.{Dto, Definition}
import msl._
import generator.Generator

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/3/11
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */

trait CommonFlex {
  self: Generator =>
  def dtoImports(definitions: List[Definition]) = definitions.map {
    _.definitionType.variableType match {
      case d: Dto =>
        val elementDto: Dto = Context.elements.get(d.name).get.asInstanceOf[Dto]
        val dtoNamespace = List(Context.flexPackage(elementDto.flexPackage.get), "dtos").mkString(".")
        if(dtoNamespace != namespace)
          """    import """ + dtoNamespace + "." + d.name + """;
  """ else ""
      case _ => ""
    }
  }.mkString

}