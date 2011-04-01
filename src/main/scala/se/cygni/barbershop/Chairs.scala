package se.cygni.barbershop

import collection.immutable.Queue
import akka.actor.{ActorRef, Actor}

case class Chairs(numberOfChairs: Int, line: ActorRef, tracker:ActorRef) extends Actor {

  case class Entry(customer:ActorRef, chair:Int)

  val chairs = Range(0, numberOfChairs).reverse.foldLeft(List[Int]()) ((l, e) => e :: l)

  protected def receive = chairsReceive(0, Queue[Entry](), chairs)

  def stateAsString(claimed: Int, queue: Queue[Entry]): String = "sitting %d, claimed %d".format(queue.size, claimed)

  def chairsReceive(claimed: Int, taken: Queue[Entry], free:List[Int]): Receive = {
    case IsSeatAvailable => {
      if (free.size - claimed > 0) {
        val (chair :: newFree) = free
        self.reply(TakeChair(chair))
        val newTaken = taken enqueue Entry(self.sender.get, chair)
        log.info("told %s to sit down in chair %d (%s)", self.sender.get.getId, chair, stateAsString(claimed, newTaken))
        become(chairsReceive(claimed, newTaken, newFree))
      } else {
        log.info("no seats available for %s (%s)", self.sender.get.getId, stateAsString(claimed, taken))
        self.reply(NoSeatsAvailable)
      }
    }
    case NextCustomer => if (taken.isEmpty) {
      self.reply(NoCustomersWaiting)
      log.info("no customers is sitting (%s)", stateAsString(claimed, taken))
    } else {
      val (Entry(customer, chair), newTaken) = taken.dequeue
      val newFree = chair :: free
      log.info("sending %s to %s (%s)", customer.getId, self.sender.get.getId, stateAsString(claimed, newTaken))
      customer ! GotoBarber(self.sender.get)
      tracker ! TrackLeftChair(chair)
      log.info("check if there are customers standing in line")
      line ! FreeChair
      become(chairsReceive(claimed+1, newTaken, newFree))
    }
    case ClaimChair => if (claimed == 0) {
      log.error("Spurios ClaimChair message ")
    } else {
      val (chair :: newFree) = free
      val newTaken = taken enqueue Entry(self.sender.get, chair)
      val customer = self.sender.get
      customer ! TakeChair(chair)
      log.info("told customer %s to sit down in claimed chair (%s)", customer.getId, stateAsString(claimed, newTaken))
      become(chairsReceive(claimed -1, newTaken, newFree))
    }
    case NoCustomersWaiting => if (claimed == 0) {
      log.error("Spurios ClaimChair message ")
    } else {
      val c = claimed - 1
      log.info("no customers standing (%s)", stateAsString(c, taken))
      become(chairsReceive(claimed-1, taken, free))
    }

    case msg => log.error("Unknown message: %s", msg)
  }
}
