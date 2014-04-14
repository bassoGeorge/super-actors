package Filter

import akka.actor.Actor

/**
 * Created by basso on 14/4/14.
 * A filter is an actor which takes a message of type Message and processes it to return a type Message
 * Notes:
 *  1. A single filter actor can be registered with multiple services as the filter itself only
 *    replies back to the sender
 */

trait Filter extends Actor {
  def processMessage(msg: Message): Message   // To be implemented by concrete actor

  def receive = {
    case msg: Message => sender ! processMessage(msg)
  }
}
