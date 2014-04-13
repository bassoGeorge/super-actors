import akka.actor.ActorRef

/**
 * Created by basso on 13/4/14.
 */
package object Mediator {
  sealed trait Registration

    // Register an actor to receive messages of types 'messageType' (& any of there subtypes)
  case class RegisterForReceive(actor: ActorRef, messageType: Class[_]) extends Registration

    // The actor is only registered for global notifications
  case class RegisterForNotification(actor: ActorRef) extends Registration

    // Register the given types for broadcast
  case class RegisterBroadcastMessage(msgs: Class[_]) extends Registration
  case class Unregister(act: ActorRef) extends Registration
}
