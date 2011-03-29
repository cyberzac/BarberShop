package se.cygni.barbershop

import collection.immutable.Queue
import akka.actor.{Actor, ActorRef}


case class WaitingLine(numberOfPositions:Int)  extends Actor {

  protected def receive = lineReceive(Queue[ActorRef]())

  def lineReceive(takenPositions: Queue[ActorRef]): Receive = {
    case IsPositionAvailable => {
      if (takenPositions.size < numberOfPositions) {
        log.info("Wait in line, position %d ", takenPositions.size)
        self.reply(WaitInLine)
        become(lineReceive(takenPositions enqueue self.sender.get))
      } else {
        log.info("Sorry, waitingline is full")
        self.reply(WaitingLineFull)
      }
    }
    case TakeSeat =>if (takenPositions.isEmpty) {
      self.reply(NoCustomersWaiting)
    } else {
      val (customer, tail) = takenPositions.dequeue
      become(lineReceive(tail))
    }
    case msg => log.error("Unknown message: %s", msg)
  }
}