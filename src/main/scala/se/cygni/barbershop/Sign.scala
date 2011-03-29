package se.cygni.barbershop

import akka.actor.{ActorRef, Actor}
import collection.immutable.Queue

class Sign extends Actor {

  protected def receive = signReceive(Queue[ActorRef]())

  def signReceive(freeBarbers: Queue[ActorRef]): Receive = {
    case StartSleeping => {
      if (self.sender.isDefined) {}
      val queue = freeBarbers enqueue self.sender.get
      log.info("%d sleeping barbers", queue.size)
      become(signReceive(queue))
    }
    case WakeUp => {
      if (freeBarbers.isEmpty) {
        self.reply(Wait)
      } else {
        val (barber: ActorRef, tail) = freeBarbers.dequeue
        log.info("Try %s", barber)
        barber forward WakeUp
        log.info("%d sleeping barbers", tail.size)
        become(signReceive(tail))
      }
    }
  }

}