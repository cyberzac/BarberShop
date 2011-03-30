package se.cygni.barbershop

import collection.immutable.Queue
import akka.actor.{ActorRef, Actor}

case class Chairs(numberOfChairs: Int, waitingLine: ActorRef) extends Actor {

  protected def receive = chairsReceive(0, Queue[ActorRef]())

  def state(claimed: Int, queue: Queue[ActorRef]): String = "sitting %d, claimed %d".format(queue.size, claimed)

  def chairsReceive(claimedSeatCount: Int, takenChairs: Queue[ActorRef]): Receive = {
    case IsSeatAvailable => {
      if (takenChairs.size < (numberOfChairs - claimedSeatCount)) {
        self.reply(TakeSeat)
        val queue = takenChairs enqueue self.sender.get
        log.info("told %s to sit down (%s)", self.sender.get.getId, state(claimedSeatCount, queue))
        become(chairsReceive(claimedSeatCount, queue))
      } else {
        log.info("no seats available for %s (%s)", self.sender.get.getId, state(claimedSeatCount, takenChairs))
        self.reply(NoSeatsAvailable)
      }
    }
    case NextCustomer => if (takenChairs.isEmpty) {
      self.reply(NoCustomersWaiting)
      log.info("no customers is sitting (%s)", state(claimedSeatCount, takenChairs))
    } else {
      val (customer, tail) = takenChairs.dequeue
      log.info("sending %s to %s (%s)", customer.getId, self.sender.get.getId, state(claimedSeatCount, tail))
      customer ! GotoBarber(self.sender.get)
      log.info("check if there are customers standing in line")
      waitingLine ! TakeSeat
      become(chairsReceive(claimedSeatCount+1, tail))
    }
    case ClaimSeat => if (claimedSeatCount == 0) {
      log.error("Spurios ClaimSeat message ")
    } else {
      val claimed = claimedSeatCount - 1
      val queue = takenChairs enqueue self.sender.get
      val customer = self.sender.get
      log.info("told customer %s to sit down in claimed seat (%s)", customer.getId, state(claimed, queue))
      become(chairsReceive(claimed, queue))
    }
    case NoCustomersWaiting => if (claimedSeatCount == 0) {
      log.error("Spurios ClaimSeat message ")
    } else {
      val claimed = claimedSeatCount - 1
      log.info("no customers standing (%s)", state(claimed, takenChairs))
      become(chairsReceive(claimed, takenChairs))
    }

    case msg => log.error("Unknown message: %s", msg)
  }

}
