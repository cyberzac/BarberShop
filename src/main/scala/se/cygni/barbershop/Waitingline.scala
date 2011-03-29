package se.cygni.barbershop

import collection.immutable.Queue
import akka.actor.{Actor, ActorRef}


case class Waitingline(maxLine:Int)  extends Actor {

  protected def receive = lineReceive(Queue[ActorRef]())

  def lineReceive(inline: Queue[ActorRef]): Receive = {
    case TryGetInLine => {
      if (inline.size < maxLine) {
        log.info("Wait in line at %d ", inline.size)
        self.reply(WaitInLine)
        become(lineReceive(inline enqueue self.sender.get))
      } else {
        log.info("Sorry, waitingline is full")
        self.reply(WaitinglineFull)
      }
    }
    case TakeSeat =>if (inline.isEmpty) {
      self.reply(NoCustomersWaiting)
    } else {
      val (customer, tail) = inline.dequeue
      customer ! ClaimSeat
      become(lineReceive(tail))
    }
    case msg => log.error("Unknown message: %s", msg)
  }
}