/**
 * Created by Łukasz Biały on 10/23/15.
 *
 * This file is part of package
 * ReactiveObserver library.
 *
 */

package eu.keios.ReactiveObserver

import org.json4s._

case class Address(code: String, parameters: List[String])

case class InvalidMessage(reason: String)

sealed trait IncomingMessage

case class Observe(to: Address, what: String) extends IncomingMessage
case class UnObserve(to: Address, what: String) extends IncomingMessage
case class Get(to: Address, what: String, requestId: BigInt) extends IncomingMessage
case class Event(to: Address, method: String, args: List[JValue]) extends IncomingMessage
case class Request(to: Address, method: String, requestId: BigInt, args: List[JValue]) extends IncomingMessage

sealed trait OutgoingMessage

case class Notify(from: Address, what: String, signal: String, args: List[JValue]) extends OutgoingMessage
case class Response(responseId: BigInt, response: JValue) extends OutgoingMessage