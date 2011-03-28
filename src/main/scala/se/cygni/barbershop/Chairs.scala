package se.cygni.barbershop

import collection.immutable.Queue
import akka.actor.{ActorRef, Actor}

case class Chairs(numberOfChairs: Int) extends Actor {

  protected def receive = chairsReceive(Queue[ActorRef]())

  def chairsReceive(takenChairs: Queue[ActorRef]): Receive = {
    case IsSeatAvailable => {
      if (takenChairs.size < numberOfChairs) {
        log.info("Take seat %d ", takenChairs.size)
        self.reply(TakeASeat)
        become(chairsReceive(takenChairs enqueue self.sender.get))
      } else {
        log.info("Sorry, no seats available")
        self.reply(NoSeatsAvailable)
      }
    }
    case NextCustomer =>if (takenChairs.isEmpty) {
      self.reply(NoCustomersWaiting)
    } else {
      val (customer, tail) = takenChairs.dequeue
      customer ! GotoBarber(self.sender.get)
      become(chairsReceive(tail))
    }
    case msg => log.error("Unknown message: %s", msg)
  }
}
