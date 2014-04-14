import akka.util.Timeout
import Filter._
import akka.actor._
import ActorDSL._
import scala.concurrent.duration._

case class MsgOut(s: String) extends Message
case class MsgIn(s: String) extends Message

class TestFilter extends Filter {
  def processMessage(msg: Message) = msg match {
    case MsgIn(s) => MsgIn("Pre__"+s)
    case MsgOut(s) => MsgOut(s+"__Post")
  }
}

class FilterTest extends IntegrationTest {

  val testFilter = system.actorOf(Props[TestFilter], "testFilter")
  //val testFilter = new TestFilter

  val targetDummy = actor(new Act{
    become{
      case MsgIn(s) => sender ! MsgOut(s)
    }
  })

  /*val filtered = system.actorOf(Props(classOf[FilteredService],
    targetDummy, Timeout(3 second)), "filtered")*/
  val filtered = FilteredService.createService(targetDummy, "filtered")()

  val testMsg = MsgIn("Hello")
  import FilteredService._

  "Filtered System" should "Reply original message when no filters are registered" in {
    filtered ! testMsg
    expectMsg(MsgOut("Hello"))
  }

  it should "Reply Pre processed message on registering preProcessor" in {
    filtered ! AddPreProcessor(testFilter)
    filtered ! testMsg
    expectMsg(MsgOut("Pre__Hello"))
  }

  it should "Reply Pre and post processed message on registering a postProcessor too" in {
    filtered ! AddPostProcessor(testFilter)
    filtered ! testMsg
    expectMsg(MsgOut("Pre__Hello__Post"))
  }

  it should "Reply postProcessed message on disabling pre-process filter" in {
    filtered ! DisablePreFilter(testFilter)
    filtered ! testMsg
    expectMsg(MsgOut("Hello__Post"))
  }

  it should "Reply preProcessed message on disabling post-process filter" in {
    filtered ! EnablePreFilter(testFilter)
    filtered ! DisablePostFilter(testFilter)
    filtered ! testMsg
    expectMsg(MsgOut("Pre__Hello"))
  }

  it should "Reply original message when both filters are disabled" in {
    filtered ! DisablePreFilter(testFilter)
    filtered ! testMsg
    expectMsg(MsgOut("Hello"))
  }
}
