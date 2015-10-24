/**
 * Created by Łukasz Biały on 10/23/15.
 *
 * This file is part of package
 * ReactiveObserver library.
 *
 */

import eu.keios.ReactiveObserver._
import org.scalatest.FunSpec

class ProtocolParserSpec extends FunSpec {

  val observeJson = """{"type":"observe","what":"login_status","to":["Elixir.Reactive.Session"]}"""
  val unObserveJson = """{"type":"unobserve","what":"login_status","to":["Elixir.Reactive.Session"]}"""
  val getJson = """{"type":"get","what":"login_status","to":["Elixir.Reactive.Session"],"requestId":1}"""
  val requestJson = """{"type":"request","requestId":2,"to":["Elixir.Blog.Post"],"method":"comment","args":["Yo"]}"""
  val eventJson = """{"type":"event","to":["Elixir.Blog.Post"],"method":"comment","args":["Yo"]}"""

  val brokenObserveJson = """{"type":"observe","what":["login_status"],"to":[1]}"""
  val brokenUnObserveJson = """{"type":"unobserve","what":["login_status"],"to":["Elixir.Reactive.Session"]}"""
  val brokenGetJson = """{"type":"get","what":"login_status","to":["Elixir.Reactive.Session"],"requestId":"23"}"""
  val brokenRequestJson = """{"type":"request","requestId":2,"to":["Elixir.Blog.Post"],"method":"comment","args":{"k":"v"}}"""
  val brokenEventJson = """{"type":"event","to":["Elixir.Blog.Post"],"method":["comment"],"args":["Yo"]}"""

  val missingTypeJson = """{"invalid":"invalid"}"""
  val invalidProtocolJson = """[{"key":"value"},{"key":"value"}]"""

  describe("ProtocolParser") {
    it("should parse observe messages") {
        val result = ProtocolParser.parse(observeJson)
        if (result.isLeft) throw new Exception("Unexpected failure: " + result.left.get.reason)

        if (!result.right.get.isInstanceOf[Observe]) throw new Exception("Result is of wrong type: " + result.right.get.getClass)
    }

    it("should parse unobserve messages") {
      val result = ProtocolParser.parse(unObserveJson)
      if (result.isLeft) throw new Exception("Unexpected failure: " + result.left.get.reason)

      if (!result.right.get.isInstanceOf[UnObserve]) throw new Exception("Result is of wrong type: " + result.right.get.getClass)
    }

    it("should parse get messages") {
      val result = ProtocolParser.parse(getJson)
      if (result.isLeft) throw new Exception("Unexpected failure: " + result.left.get.reason)

      if (!result.right.get.isInstanceOf[Get]) throw new Exception("Result is of wrong type: " + result.right.get.getClass)
    }

    it("should parse request messages") {
      val result = ProtocolParser.parse(requestJson)
      if (result.isLeft) throw new Exception("Unexpected failure: " + result.left.get.reason)

      if (!result.right.get.isInstanceOf[Request]) throw new Exception("Result is of wrong type: " + result.right.get.getClass)
    }

    it("should parse event messages") {
      val result = ProtocolParser.parse(eventJson)
      if (result.isLeft) throw new Exception("Unexpected failure: " + result.left.get.reason)

      if (!result.right.get.isInstanceOf[Event]) throw new Exception("Result is of wrong type: " + result.right.get.getClass)
    }

    it("should fail on broken and invalid messages") {
      val invalidJsons = brokenObserveJson :: brokenUnObserveJson :: brokenGetJson :: brokenRequestJson :: brokenEventJson :: missingTypeJson :: invalidProtocolJson :: Nil

      invalidJsons.foreach { json =>
        val result = ProtocolParser.parse(json)
        if (result.isRight) throw new Exception("Unexpected success, broken json resulted in: " + result.right.get.getClass)
      }
    }
  }
}