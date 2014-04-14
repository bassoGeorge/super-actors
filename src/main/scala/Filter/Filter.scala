package Filter

import akka.actor.{ActorRef, Actor}
import scala.collection.mutable.ListBuffer

/**
 * Created by basso on 14/4/14.
 * A filter is an actor which takes a message of type Message and processes it to return a type Message
 * Notes:
 *  1. A single filter actor can be registered with multiple services as the filter itself only
 *    replies back to the sender
 *  2. Allows enabling and disabling the filter, enabled by default. It maintains a list of systems where
 *    it has been disabled.
 */
object Filter {
  case object Disable   // for disabling a the filter
  case object Enable
}
import Filter._

trait Filter extends Actor {
  def processMessage(msg: Message): Message   // To be implemented by concrete actor

  val disabledSystems = ListBuffer[ActorRef]()    // A list of systems where this filter is disabled

  def receive = {
    case Disable => disabledSystems += sender
    case Enable => disabledSystems -= sender
    case msg: Message =>
      if (disabledSystems.contains(sender)) sender ! msg    // Reply back the same message if disabled
      else sender ! processMessage(msg)   // Reply with processed message if enabled
  }
}
