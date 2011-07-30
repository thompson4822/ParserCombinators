package xml

import scala.xml.{NamespaceBinding, PrettyPrinter, XML}

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 2/13/11
 * Time: 9:42 AM
 * To change this template use File | Settings | File Templates.
 */

object Parse {
  def main(args: Array[String]) {
    val xml = <born><to><be><alive></alive></be></to></born>
    val printer = new PrettyPrinter(256, 2)
    println(printer.format(xml))
  }
}