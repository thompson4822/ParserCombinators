package msl.dsl

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.BDDMockito.{given => givenThat}
import org.junit.Assert._
import org.scalatest.{GivenWhenThen, BeforeAndAfterEach, FeatureSpec}
//import Types._

class MethodSpec extends FeatureSpec with GivenWhenThen with MockitoSugar
with BeforeAndAfterEach with ShouldMatchers {

  feature("The parser should be able to correctly recognize methods") {
    info("As a parser")
    info("I want to be able to correctly recognize methods")
    info("So that I can create a representation of that method for code generation")

    scenario("should recognize a method without a service") {
      given("input representing a method (without a service)")
      val input = "List<DeviceDto> GetDevicesForCustomer(CustomerDto customer)"

      when("the input is parsed")
      then("a valid method will be created, but will not have a service association")
      /*
      val m = new MslParser
      val expectedReturnType = DefinitionType("DeviceDto", Some("List"))
      val expectedParameters = List(
        Definition("customer", DefinitionType("CustomerDto", None))
      )
      m.parseAll(m.method, input) match {
        case m.Success(result, _) => result should equal (Method("GetDevicesForCustomer", expectedReturnType, expectedParameters))
        case other => fail("Produced unexpected result: " + other.toString)
      }
      */
      pending
    }

    scenario("should recognize a method with a service") {
      given("input representing a method (with a service)")
      val input = "List<DeviceDto> GetDevicesForCustomer(CustomerDto customer)"

      when("the input is parsed")
      then("a valid method will be created, and will be associated with the correct service")
      /*
      val m = new MslParser
      val expectedReturnType = DefinitionType("DeviceDto", Some("List"))
      val expectedParameters = List(
        Definition("customer", DefinitionType("CustomerDto", None))
      )
      m.parseAll(m.method, input) match {
        case m.Success(result, _) => result should equal (Method("GetDevicesForCustomer", expectedReturnType, expectedParameters))
        case other => fail("Produced unexpected result: " + other.toString)
      }
      */
      pending
    }



  }

}