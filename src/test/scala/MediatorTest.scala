import akka.actor._
import Mediator._
import akka.actor.ActorDSL._

class MediatorTest extends IntegrationTest {
  trait TestMessage
  trait TestGlobal
  case class Msg(s: String)

  val mediator = system.actorOf(Props[Mediator], "testingMediator")

  val dummyAct = actor (new Act{    // Dummy actor to forward messages to mediator
    become{                         // so that it becomes the sender, and we can receive all messages
      case msg => mediator ! msg
    }
  })

  val ourMessage = new Msg("Test message") with TestMessage
  val globalMessage = new Msg("Global") with TestGlobal

  "Mediator" should "forward us our message" in {
    mediator ! RegisterForReceive(testActor, classOf[TestMessage])
    dummyAct ! ourMessage
    expectMsg(Msg("Test message"))
  }

  it should "not send us back our message" in {
    mediator ! ourMessage
    expectNoMsg()
  }

  it should "not send us the message not intended for us" in {
    dummyAct ! Msg("Test message")
    expectNoMsg()
  }

  it should "Broadcast global message to us" in {
    mediator ! RegisterBroadcastMessage(classOf[TestGlobal])
    dummyAct ! globalMessage
    expectMsg(Msg("Global"))
  }

  it should "Not send us back the message we sent for broadcast" in {
    mediator ! globalMessage
    expectNoMsg()
  }

  it should "not send us any more messages after we have Un-registered ourselves" in {
    mediator ! Unregister(testActor)
    dummyAct ! ourMessage
    expectNoMsg()
  }

  it should "send us only broadcasts and no personal messages since we have only registered for notification" in {
    mediator ! RegisterForNotification(testActor)
    dummyAct ! ourMessage
    dummyAct ! globalMessage
    expectMsg(Msg("Global"))
  }
}