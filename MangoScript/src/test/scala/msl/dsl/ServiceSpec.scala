package msl.dsl

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.BDDMockito.{given => givenThat}
import org.junit.Assert._
import org.scalatest.{GivenWhenThen, BeforeAndAfterEach, FeatureSpec}
//import Types._

class ServiceSpec extends FeatureSpec with GivenWhenThen with MockitoSugar
with BeforeAndAfterEach with ShouldMatchers {

  feature("Services can be parsed") {
    info("As a parser")
    info("I want to be able to handle service declarations")
    info("So that I can create the appropriate representation")

    scenario("should recognize member methods that return a generic value") {
      given("a service declaration with at least one method with a generic return type")
      val input = "CustomerDeviceService[Consumer] { List<CustomerDao> GetCustomers() }"

      when("parsed")
      then("the method signature should be correctly recognized")
      /*
      val m = new MslParser
      val expectedDefinitions = List(
        Method("GetCustomers", DefinitionType("CustomerDao", Some("List")), Nil)
      )
      m.parseAll(m.service, input) match {
        case m.Success(result, _) => result should equal (Service("CustomerDeviceService", NamespaceType.Consumer, expectedDefinitions ))
        case other => fail("Produced unexpected result: " + other.toString)
      }
      */
      pending
    }

    scenario("should recognize dependencies (injections)") {
      given("a service declaration with at least one dependency")
      when("parsed")
      then("the service should know that it has that dependency")
      pending
    }


  }
}