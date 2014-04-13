import akka.actor.{Props, ActorSystem}
import akka.testkit._
import Mediator._
import org.scalatest._
import org.scalatest.matchers._

object MediatorTest {
  val test = new MediatorTest()
  test.execute()
}

class MediatorTest extends IntegrationTest {
  trait TestMessage
  trait TestGlobal
  case class Msg(s: String)

  val mediator = system.actorOf(Props[Mediator], "testingMediator")

  "Mediator" should "Reply back message" in {
    mediator ! RegisterForReceive(testActor, classOf[TestMessage])
    mediator ! new Msg("Test message") with TestMessage
    expectMsg(Msg("Test message"))
  }

  it should "not Reply anything" in {
    mediator ! Msg("Test message")
    expectNoMsg()
  }

  it should "Broadcast global message" in {
    mediator ! RegisterBroadcastMessage(classOf[TestGlobal])
    mediator ! new Msg("Test message") with TestGlobal
    expectMsg(Msg("Test message"))
  }

  it should "not Reply after unRegistering" in {
    mediator ! Unregister(testActor)
    mediator ! new Msg("Test message") with TestMessage
    expectNoMsg()
  }

  it should "Reply with only global message and not personal ones" in {
    mediator ! RegisterForNotification(testActor)
    mediator ! new Msg("Global") with TestGlobal
    mediator ! new Msg("Personal") with TestMessage
    expectMsg(Msg("Global"))
  }
}