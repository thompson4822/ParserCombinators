import util.parsing.combinator.JavaTokenParsers

abstract class Value
case class MemberValue(name: String, value: Value) extends Value
case class ArrayValue(values: List[Value]) extends Value
case class ObjectValue(members: List[MemberValue]) extends Value
case object TrueValue extends Value
case object FalseValue extends Value
case class NumericValue(value: Float) extends Value
case class StringValue(value: String) extends Value
case object NullValue extends Value

/*
Parser Grammar For JSON

value   ::= obj | arr | stringLiteral | floatingPointNumber |
            "null" | "true" | "false"
obj     ::= "{" members "}"
arr     ::= "[" values "]"
members ::= member {"," member}
member  ::= stringLiteral ":" value
values  ::= value {"," value}

*/
class JSONParser extends JavaTokenParsers {

  def value: Parser[Value]   =
    obj | arr |
    stringLiteral ^^ { case str => StringValue(str) } |
    floatingPointNumber ^^ { case number => NumericValue(number.toFloat) } |
    "null" ^^^ { NullValue }|
    "true" ^^^ { TrueValue } |
    "false" ^^^ { FalseValue }

  def obj     = "{" ~> members <~ "}" ^^ { ObjectValue(_) }
  def arr     = "[" ~> values <~ "]" ^^ { ArrayValue(_) }
  def members = repsep(member, ",")
  def member  = stringLiteral ~ (":" ~> value) ^^ { case name ~ value => MemberValue(name, value) }
  def values  = repsep(value,  ",")

}

object Main {
  def main(args: Array[String]) {
    val jsonText = """
{
  "address book": {
    "name": "John Smith",
    "address": {
      "street": "10 Market Street",
      "city" : "San Francisco, CA",
      "zip" : 94111
    },
    "phone numbers": [
      "408 3384238",
      "408 1116892"
    ]
  }
}
    """

    val p = new JSONParser
    p.parseAll(p.value, jsonText) match {
      case p.Success(result, _) => println(result)
      case other => error("Produced unexpected result: " + other.toString)
    }
  }
}