package se.cygni.barbershop

import akka.actor.{ActorRef, Actor}
import collection.immutable.Queue

class Sign extends Actor {

  protected def receive = signReceive(Queue[ActorRef]())

  def signReceive(freeBarbers: Queue[ActorRef]): Receive = {
    case StartSleeping => {
      if (self.sender.isDefined) {}
      val queue = freeBarbers enqueue self.sender.get
      log.info("sleeping: %s", barberNames(queue))
      become(signReceive(queue))
    }
    case WakeUp => {
      if (freeBarbers.isEmpty) {
        log.info("%s is told to wait", self.sender.get.getId)
        self.reply(Wait)
      } else {
        val (barber: ActorRef, tail) = freeBarbers.dequeue
        log.info("%s to  %s (sleeping %s)", self.sender.get.getId, barber.getId, barberNames(tail))
        barber forward WakeUp
        become(signReceive(tail))
      }
    }
  }

  def barberNames(sleeping:Queue[ActorRef]):String ={
    if (sleeping.isEmpty) {
      "no barbers"
    }  else {
      sleeping map (_.getId) reduceLeft( _+", "+_)
    }
  }

}