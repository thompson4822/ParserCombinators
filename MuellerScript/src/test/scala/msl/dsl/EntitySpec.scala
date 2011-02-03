package msl.dsl

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.BDDMockito.{given => givenThat}
import org.junit.Assert._
import org.scalatest.{GivenWhenThen, BeforeAndAfterEach, FeatureSpec}
import Types._

class EntitySpec extends FeatureSpec with GivenWhenThen with MockitoSugar
with BeforeAndAfterEach with ShouldMatchers {

  feature("Entity elements in scripts should be correctly interpreted") {
    info("As a parser")
    info("I want to be able to interpret entity elements")
    info("So that I can generate the appropriate code")

    scenario("should interpret an entity with no elements") {
      given("a fragment of input representing an entity without elements")
      val input = "entity MyEntity { }"

      when("the input is parsed")
      then("an appropriate entity type should be created")
      /*
      val m = new MslParser
      m.parseAll(m.entity, input) match {
        case m.Success(result, _) => result should equal (Entity("MyEntity", Nil ))
        case other => fail("Produced unexpected result: " + other.toString)

      }
      */
    }

    scenario("should interpret an entity with elements") {
      given("a fragment of input representing an entity with at least one element")
      val input = "entity MyEntity { int First char Second long Third }"

      when("the input is parsed")
      then("an appropriate entity type should be created")
      /*
      val expectedDefinitions = List(
        Definition("First", DefinitionType("int", None)),
        Definition("Second", DefinitionType("char", None)),
        Definition("Third", DefinitionType("long", None))
      )
      val m = new MslParser
      m.parseAll(m.entity, input) match {
        case m.Success(result, _) => result should equal (Entity("MyEntity", expectedDefinitions))
        case other => fail("Produced unexpected result: " + other.toString)

      }
      */
      pending
    }

    scenario("should interpret an entity with generic elements") {
      given("a fragment of input representing an entity with at least one generic element")
      val input = "entity MyEntity { List<Alpha> First }"

      when("the input is parsed")
      then("an appropriate entity type should be created")
      /*
      val expectedDefinitions = List(
        Definition("First", DefinitionType("Alpha", Some("List")))
      )
      val m = new MslParser
      m.parseAll(m.entity, input) match {
        case m.Success(result, _) => result should equal (Entity("MyEntity", expectedDefinitions))
        case other => fail("Produced unexpected result: " + other.toString)

      }
      */
      pending
    }


  }

}