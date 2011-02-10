package msl.generator.csharp

import msl.Context

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/9/11
 * Time: 7:14 AM
 * To change this template use File | Settings | File Templates.
 */

trait CommonNet {
  def projectFileMapping: Tuple2[String, String]

  if(Context.projectMapping.contains(projectFileMapping._1))
    Context.projectMapping(projectFileMapping._1) ::= projectFileMapping._2
  else
    Context.projectMapping.put(projectFileMapping._1, List(projectFileMapping._2))
}