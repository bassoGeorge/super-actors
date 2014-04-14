package Filter

import akka.actor.{Props, ActorRefFactory, Actor, ActorRef}
import akka.util.Timeout
import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import akka.pattern.ask
import scala.concurrent.duration._

/**
 * Created by basso on 14/4/14.
 * This is an wrapper class for any actor which needs pre-processing or post-processing filters
 */
object FilteredService {
  case class AddPreProcessor(filter: ActorRef)
  case class AddPostProcessor(filter: ActorRef)

  case class DisablePreFilter(filter: ActorRef)
  case class EnablePreFilter(filter: ActorRef)
  case class DisablePostFilter(filter: ActorRef)
  case class EnablePostFilter(filter: ActorRef)

  def createService(target: ActorRef, name: String, filterTimeout: Timeout = Timeout(1 second))
                   (preList: Seq[ActorRef] = Nil, postList: Seq[ActorRef] = Nil)
                   (implicit system: ActorRefFactory): ActorRef = {
    val res = system.actorOf(Props(classOf[FilteredService], target, filterTimeout), name)
    preList.foreach{ res ! AddPreProcessor(_) }
    postList.foreach{ res ! AddPostProcessor(_) }
    res
  }
}
import FilteredService._

/**
 * Notes:
 *  1. for using the AddPreProcessor and AddPostProcessor, please use actor classes that mix in the
 *    Filter trait given in this package
 *  2. This system is able to selectively enable and disable filters using ActorRef matching to find the filters
 *  3. Any messages that need to be passed to the service should mix in the Message trait and all your filters
 *    should handle the expected message types
 * @param target -> The actual service actor that needs the filters
 * @param timeout -> The timeout for a filter, i.e max time before exceptions are raised
 */

class FilteredService(val target: ActorRef, implicit val timeout: Timeout)
  extends Actor {

  case class FilterRec(var enabled: Boolean, filter: ActorRef)
  val preProcessors = ListBuffer[FilterRec]()
  val postProcessors= ListBuffer[FilterRec]()

  def processMessage(msg: Message): Message = {
    ((preProcessors :+ FilterRec(enabled = true, target)) ++ postProcessors).foldLeft[Message](msg) { case (m, d @ FilterRec(enabled, act)) =>
      if (enabled) Await.result(act ? m, timeout.duration).asInstanceOf[Message]
      else m }

  }

  def control (processorList: ListBuffer[FilterRec])(filter: ActorRef, enabled: Boolean) {
    processorList.find { case FilterRec(_,x) => x == filter } match {
      case Some(fr) => fr.enabled = enabled
      case None => Unit
    }
  }

  def receive = {
    case AddPreProcessor(act) => preProcessors += FilterRec(enabled = true, act)
    case AddPostProcessor(act) => postProcessors += FilterRec(enabled = true, act)

    case DisablePreFilter(act) => control(preProcessors)(act, enabled = false)
    case EnablePreFilter(act) => control(preProcessors)(act, enabled = true)
    case DisablePostFilter(act) => control(postProcessors)(act, enabled = false)
    case EnablePostFilter(act) => control(postProcessors)(act, enabled = true)

    case msg: Message => sender ! processMessage(msg)
  }
}
