/**
 * Created by Łukasz Biały on 10/23/15.
 *
 * This file is part of package
 * ReactiveObserver library.
 *
 */

package eu.keios.ReactiveObserver

object ProtocolParser {

  import org.json4s._
  import org.json4s.jackson.JsonMethods.{parse => parseJson}

  implicit val formats = DefaultFormats

  def parse(msg: String): Either[InvalidMessage, IncomingMessage] = {
    val json = parseJson(msg)

    if (hasValidStructure(json)) {
      val maybeType = findType(json)

      makeMessage(maybeType, json)
    } else {
      Left(InvalidMessage("Message does not conform to ReactiveObserver protocol."))
    }
  }

  def makeMessage(maybeType: Option[String], json: JValue): Either[InvalidMessage, IncomingMessage] = {
    maybeType match {
      case Some("observe") => makeObserveMessage(json)
      case Some("unobserve") => makeUnObserveMessage(json)
      case Some("get") => makeGetMessage(json)
      case Some("request") => makeRequestMessage(json)
      case Some("event") => makeEventMessage(json)

      case Some(_) => Left(InvalidMessage("Unknown type."))
      case None => Left(InvalidMessage("Missing message type."))
    }
  }

  def makeEventMessage(json: JValue): Either[InvalidMessage, IncomingMessage] = {
    try {
      val toList = (json \ "to").extract[List[String]]
      val method = (json \ "method").extract[String]
      val args = (json \ "args").extract[List[JValue]]

      val to = new Address(toList.head, toList.tail)

      Right(new Event(to, method, args))
    } catch {
      case ex: Exception => Left(InvalidMessage(s"Invalid observe message: ${ex.getMessage}}"))
    }
  }

  def makeRequestMessage(json: JValue): Either[InvalidMessage, IncomingMessage] = {
    try {
      val toList = (json \ "to").extract[List[String]]
      val method = (json \ "method").extract[String]
      val args = (json \ "args").extract[List[JValue]]
      val requestId = (json \ "requestId").extract[BigInt]

      val to = new Address(toList.head, toList.tail)

      Right(new Request(to, method, requestId, args))
    } catch {
      case ex: Exception => Left(InvalidMessage(s"Invalid observe message: ${ex.getMessage}}"))
    }
  }

  def makeGetMessage(json: JValue): Either[InvalidMessage, IncomingMessage] = {
    try {
      val toList = (json \ "to").extract[List[String]]
      val what = (json \ "what").extract[String]
      val requestId = (json \ "requestId").extract[BigInt]

      val to = new Address(toList.head, toList.tail)

      Right(new Get(to, what, requestId))
    } catch {
      case ex: Exception => Left(InvalidMessage(s"Invalid observe message: ${ex.getMessage}}"))
    }
  }

  def makeUnObserveMessage(json: JValue): Either[InvalidMessage, IncomingMessage] = {
    try {
      val toList = (json \ "to").extract[List[String]]
      val what = (json \ "what").extract[String]

      val to = new Address(toList.head, toList.tail)

      Right(new UnObserve(to, what))
    } catch {
      case ex: Exception => Left(InvalidMessage(s"Invalid unobserve message: ${ex.getMessage}}"))
    }
  }

  def makeObserveMessage(json: JValue): Either[InvalidMessage, IncomingMessage] = {
    try {
      val toList = (json \ "to").extract[List[String]]
      val what = (json \ "what").extract[String]

      val to = new Address(toList.head, toList.tail)

      Right(new Observe(to, what))
    } catch {
      case ex: Exception => Left(InvalidMessage(s"Invalid observe message: ${ex.getMessage}}"))
    }
  }

  def hasValidStructure(json: JValue) = json match {
    case JObject(_ :: _) => true
    case _ => false
  }

  def makeAddress(unparsedAddress: Option[JValue]): Option[Address] = unparsedAddress match {
    case Some(JArray(wrappedCode :: rest)) => Some(new Address(wrappedCode.extract[String], rest.map {
      _.extract[String]
    }))
    case _ => None
  }
  
  def extractType: PartialFunction[(String, JValue), JValue] = {
    case ("type", msgType) => msgType
  }

  def findType(json: JValue) = {
    val wrappedType = json match {
      case JObject(list: List[(String, JValue)]) => list.collectFirst(extractType)
      case _ => None
    }

    wrappedType match {
      case Some(JString(messageType)) => Some(messageType)
      case Some(_) => None
      case None => None
    }
  }


}
