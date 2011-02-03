package msl.dsl

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.BDDMockito.{given => givenThat}
import org.junit.Assert._
import org.scalatest.{GivenWhenThen, BeforeAndAfterEach, FeatureSpec}
import Types._

class EnumSpec extends FeatureSpec with GivenWhenThen with MockitoSugar
with BeforeAndAfterEach with ShouldMatchers {

  feature("Enumeration can be parsed") {
    info("As a parser")
    info("I want to be able to recognize enumerations")
    info("So that I can generate enum boilerplate")

    scenario("should recognize a non-empty enumeration") {
      given("a string representing an enumeration with one or more elements")
      val input = "enum MyEnum { First, Second, Third }"

      when("parsed")
      then("an Enum type should be produced with the correct name and content")
      /*
      val m = new MslParser
      m.parseAll(m.enum, input) match {
        case m.Success(result, _) => result should equal (Enum("MyEnum", List("First", "Second", "Third") ))
        case other => fail("Produced unexpected result: " + other.toString)
      }
      */
      pending
    }

    scenario("should recognize an empty enumeration") {
      given("a string representing an enumeration without elements")
      val input = "enum MyEnum { }"

      when("parsed")
      then("an Enum type should be created with the correct name and content")
      /*
      val m = new MslParser
      m.parseAll(m.enum, input) match {
        case m.Success(result, _) => result should equal (Enum("MyEnum", Nil ))
        case other => fail("Produced unexpected result: " + other.toString)

      }
      */
      pending
    }



  }
  
}