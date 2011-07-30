package msl.generator.csharp

import msl.generator.Generator
import collection.immutable.List
import msl.dsl.Types.{Type, DefinitionType, Method}

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 5/20/11
 * Time: 8:22 AM
 * To change this template use File | Settings | File Templates.
 */


trait TestMethodDiscriminator extends Generator {
  def methods: List[Method]

  private def adjustedName(method: Method): String = {
    val methodsNamedTheSame = methods.filter(_.name == method.name)
    (methodsNamedTheSame.length, methodsNamedTheSame.indexOf(method)) match {
      case (x, y) if(x > 1 && y > 0) => method.name + y
      case _ => method.name
    }
  }

  private val uniqueMethods: Seq[Method] = {
    def argumentTypes(method: Method): List[(Type, Option[String])] =
      method.parameters.map(p => (p.definitionType.variableType, p.definitionType.genericType))
    def isMethodSignatureUnique(methodsToCompare: List[Method], method: Method): Boolean =
      methodsToCompare.count(m => m.name == method.name && argumentTypes(m) == argumentTypes(method)) == 0
    methods.foldLeft(List[Method]()){
      (list, method) => if(isMethodSignatureUnique(list, method)) method :: list else list
    }
  }

  val methodInterfaces =
    uniqueMethods.map(m => "        void Test" + adjustedName(m) + "();").mkString(nl)

  val methodDefinitions = {
    // Because method names can be the same (but have different signatures), we may need to discriminate
    // test methods
    uniqueMethods.map(m =>
      """
        [TestMethod]
        public void Test""" + adjustedName(m) + """()
        {
            throw new NotImplementedException();
        }
      """).mkString
  }
}

