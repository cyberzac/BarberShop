package se.cygni.barbershop

import akka.actor.{ActorRef, Actor}
import collection.immutable.Queue

class Sign extends Actor with PostStart {

  protected def receive = signReceive(Queue[ActorRef]())

  def signReceive(freeBarbers: Queue[ActorRef]): Receive = {

    case Sleeping => {
      if (self.sender.isDefined) {}
      log.debug("sleeping: %s", barberNames(freeBarbers))
      become(signReceive(freeBarbers enqueue self.sender.get))
    }
    case rb@RequestBarber(customer) => {
      if (freeBarbers.isEmpty) {
        log.debug("%s is told to wait", customer.id)
        self.reply(Wait(customer))
      } else {
        val (barber: ActorRef, tail) = freeBarbers.dequeue
        become(signReceive(tail))
        log.debug("sending %s to  %s (sleeping %s)", customer.id, barber.getId, barberNames(freeBarbers))
        barber ! rb
      }
    }
  }

  def barberNames(sleeping: Queue[ActorRef]): String = {
    if (sleeping.isEmpty) {
      "no barbers"
    } else {
      sleeping map (_.getId) reduceLeft (_ + ", " + _)
    }
  }

}