package se.cygni.barbershop

import collection.immutable.Queue
import akka.actor.{Actor, ActorRef}


case class Line(maxLine:Int, tracker:ActorRef)  extends Actor {

  protected def receive = lineReceive(Queue[ActorRef]())

  def lineReceive(inline: Queue[ActorRef]): Receive = {
    case GetInLine => {
      val customer = self.sender.get
      val message = if (inline.size < maxLine) {
        self.reply(WaitInLine)
        val newLine = inline enqueue customer
        become(lineReceive(newLine))
        "wait in line (%d)".format(newLine.size)
      } else {
        self.reply(LineFull)
        "sorry waitingline is full (%d)".format(inline.size)
      }
        log.info("told %s %s", customer.getId, message)

    }
    case FreeChair =>if (inline.isEmpty) {
      self.reply(NoCustomersWaiting)
      log.info("no customers standing", inline.size)
    } else {
      val (customer, tail) = inline.dequeue
      customer ! ClaimChair
      tracker ! TrackLeftLine
      become(lineReceive(tail))
      log.info("%s to claim a seat", customer.getId)
    }
    case msg => log.error("Unknown message: %s", msg)
  }
}