package Mediator

import akka.actor.{Terminated, ActorRef, Actor}

/**
 * Created by basso on 13/4/14.
 * The Mediator actor implements the mediator pattern
 * It can become a central point of communication between many actors
 */

class Mediator extends Actor {
  import collection.mutable.{Set => mSet, Map => mMap}
  val fTable = mMap[Class[_], mSet[ActorRef]]()
  val notifySet = mSet[ActorRef]()
  val globalMsg = mSet[Class[_]]()

  def receive = {
    case RegisterBroadcastMessage(msg) => globalMsg += msg
    case RegisterForNotification(act) =>
      notifySet += act
      context watch act

    case RegisterForReceive(act, mt) =>
      fTable += ((mt, fTable.getOrElse(mt, mSet[ActorRef]())+act ))
      context watch act

    case Unregister(act) =>
      context unwatch act
      fTable.foreach { case(m, al) =>
        al -= act
      }
      notifySet -= act

    case Terminated(act) => self ! Unregister(act)

    case msg =>
      if (globalMsg.exists{ _.isAssignableFrom(msg.getClass)}) {
        notifySet.foreach{_!msg}
        fTable.values.reduce(_++_).foreach{_!msg}
      }
      else
        fTable.foreach { case(m, al) =>
          if (m.isAssignableFrom(msg.getClass))
            al.foreach{_ ! msg}
        }
  }
}
