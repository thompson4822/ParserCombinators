package msl.dsl

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.BDDMockito.{given => givenThat}
import org.junit.Assert._
import org.scalatest.{GivenWhenThen, BeforeAndAfterEach, FeatureSpec}
import msl.dsl.Types._

class InjectSpec extends FeatureSpec with GivenWhenThen with MockitoSugar
with BeforeAndAfterEach with ShouldMatchers {

  feature("A dependency for a factory can be defined") {
    info("As a Factory")
    info("I want to be able to be decorated with dependencies")
    info("So that I can generate more robust factory test code")

    scenario("should parse a dependency as a definition") {
      given("input defining a dependency")
      val input = "inject ILotusBlossum lotusBlossum"

      when("the input is parsed")
      then("the outcome should be a definition type")
      /*
      val m = new MslParser
      val expectedOutcome = Definition("lotusBlossum", DefinitionType("ILotusBlossum", None))
      m.parseAll(m.inject, input) match {
        case m.Success(result, _) => result should equal (expectedOutcome)
        case other => fail("Produced unexpected result: " + other.toString)
      }
      */
      pending
    }


  }

}