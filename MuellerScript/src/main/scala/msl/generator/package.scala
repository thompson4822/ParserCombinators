package msl.generator

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/17/11
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */

object StringExtensions {
  implicit def stringExtensions(string: String) = new StringExtensions(string)
}

class StringExtensions(string: String) {

  def unCapitalize: String = string match {
    case s if(s.length > 0) => { val(x, y) = s.splitAt(1); x.toLowerCase + y }
    case _ => string
  }

  def splitId: List[String] = {
    val parser = """([A-Za-z][a-z0-9]*)(.*)""".r
    def splitIdRec(remainder: String, parsed: List[String]): List[String] = remainder match {
      case "" => parsed.reverse
      case parser(first, rest) => splitIdRec(rest, first :: parsed)
    }
    splitIdRec(string, Nil)
  }
}