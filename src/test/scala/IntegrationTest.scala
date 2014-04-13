import akka.actor.ActorSystem
import org.scalatest._
import akka.testkit._

abstract class IntegrationTest(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("test"))
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
}
