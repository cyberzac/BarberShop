package se.cygni.barbershop

import akka.actor.{ActorRef, Actor}
import collection.immutable.Queue
class Sign extends Actor {

  protected def receive = signReceive(Queue[ActorRef]())

  def signReceive(freeBarbers: Queue[ActorRef]): Receive = {
    case StartSleeping => {
      if (self.sender.isDefined) {}
      become(signReceive(freeBarbers enqueue self.sender.get))
    }
    case WakeUp => {
      if (freeBarbers.isEmpty) {
        self.reply(Wait)
      } else {
        val (barber: ActorRef, tail) = freeBarbers.dequeue
        log.info("Try %s", barber)
        barber forward WakeUp
        become(signReceive(tail))
      }
    }
  }

}