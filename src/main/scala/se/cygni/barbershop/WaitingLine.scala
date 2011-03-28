package se.cygni.barbershop

import collection.immutable.Queue
import akka.actor.{Actor, ActorRef}


case class WaitingLine(numberOfSpots:Int)  extends Actor {

  protected def receive = lineReceive(Queue[ActorRef]())

  def lineReceive(takenSpots: Queue[ActorRef]): Receive =  null
  /*
  {
    case IsSpotAvailable => {
      if (takenSpots.size < numberOfChairs) {
        log.info("Take spot %d ", takenSpots.size)
        self.reply(TakeASpot)
        become(lineReceive(takenSpots enqueue self.sender.get))
      } else {
        log.info("Sorry, no spots available")
        self.reply(NoSpotsAvailable)
      }
    }
    case TakeASeat =>if (takenSpots.isEmpty) {
      self.reply(NoCustomersWaiting)
    } else {
      val (customer, tail) = takenSpots.dequeue

      become(lineReceive(tail))
    }
    case msg => log.error("Unknown message: %s", msg)
  }
  */
}