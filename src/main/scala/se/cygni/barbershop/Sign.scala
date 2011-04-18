package se.cygni.barbershop

import akka.actor.{ActorRef, Actor}
import collection.immutable.Queue

class Sign extends Actor with PostStart {


  protected def receive = signReceive(Queue[ActorRef]())

  def signReceive(sleeping: Queue[ActorRef]): Receive = {

    case Sleeping => {
      val newSleeping = sleeping enqueue self.sender.get
      log.debug("%s going to sleep, calling for next customer, (sleeping: %s)", self.sender.get.id, barberNames(newSleeping))
      lounge ! NextCustomer
      become(signReceive(newSleeping))
    }
    case rb@RequestBarber(customer) => {
      if (sleeping.isEmpty) {
        log.debug("%s is told to wait", customer.id)
        self.reply(Wait(customer))
      } else {
        val (barber: ActorRef, tail) = sleeping.dequeue
        become(signReceive(tail))
        log.debug("sending %s to  %s (sleeping %s)", customer.id, barber.getId, barberNames(tail))
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