package se.cygni.barbershop

import collection.immutable.Queue
import akka.actor.{ActorRef, Actor}

case class Chairs(numberOfChairs: Int, waitingLine:ActorRef) extends Actor {

  protected def receive =  chairsReceive(0, Queue[ActorRef]())

  def logStatus( claimed:Int, queue:Queue[ActorRef]): Unit = {
    log.info("Sitting %d, claimed %d", queue.size, claimed)
  }

  def chairsReceive(claimedSeatCount:Int, takenChairs:Queue[ActorRef]): Receive = {
    case IsSeatAvailable => {
      if (takenChairs.size < (numberOfChairs - claimedSeatCount)) {
        self.reply(TakeSeat)
        val queue = takenChairs enqueue  self.sender.get
        logStatus(claimedSeatCount, queue)
        become(chairsReceive(claimedSeatCount,queue))
      } else {
        log.info("Sorry, no seats available")
        self.reply(NoSeatsAvailable)
      }
    }
    case NextCustomer => if (takenChairs.isEmpty) {
      self.reply(NoCustomersWaiting)
    } else {
      val (customer, tail) = takenChairs.dequeue
      customer ! GotoBarber(self.sender.get)
      waitingLine ! TakeSeat
      val claimed = claimedSeatCount +1
      logStatus(claimed, tail)
      become(chairsReceive (claimed, tail))
    }
    case ClaimSeat =>  if (claimedSeatCount == 0 ) {
      log.error("Spurios ClaimSeat message ")
    } else {
      val claimed = claimedSeatCount - 1
      val queue = takenChairs enqueue self.sender.get
      logStatus(claimed, queue)
      become(chairsReceive(claimed, queue))
    }
    case NoCustomersWaiting =>  if (claimedSeatCount == 0 ) {
      log.error("Spurios ClaimSeat message ")
    } else {
      val claimed = claimedSeatCount - 1
      logStatus(claimed, takenChairs)
      become(chairsReceive(claimed, takenChairs))
    }

    case msg => log.error("Unknown message: %s", msg)
  }

}
