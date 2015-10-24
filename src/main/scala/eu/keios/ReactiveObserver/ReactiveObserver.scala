/**
 * Created by Łukasz Biały on 10/23/15.
 *
 * This file is part of package
 * ReactiveObserver library.
 *
 */

package eu.keios.ReactiveObserver

class ReactiveObserver(val router: ServiceRouter) {
//  def handle(messageString: String): String = {
//    val parsedMessage = ProtocolParser.parse(messageString)
//
//  }
}

object ReactiveObserver {
  def apply(router: ServiceRouter) = new ReactiveObserver(router)
}
