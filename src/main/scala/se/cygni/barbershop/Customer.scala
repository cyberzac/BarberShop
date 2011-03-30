package se.cygni.barbershop

import akka.actor.Actor

case class Customer(id:String, barbershop: Barbershop) extends Actor {

  self.id = id

  override def preStart = {
    log.info("%s entered shop", id)
    barbershop.sign ! WakeUp
  }

  protected def receive = {
    case CutDone(time) => {
      log.info("%s cut in %d, leaving", id, time)
      barbershop.door ! Leaving
    }
    case Wait => barbershop.chairs ! IsSeatAvailable
    case TakeSeat => log.info("%s sitting down", id)
    case WaitInLine => log.info("%s waiting in line", id)
    case NoSeatsAvailable => barbershop.waitingLine ! TryGetInLine
    case WaitinglineFull => {
      log.info("%s no place to stand, I'm leaving", id)
      barbershop.door ! Leaving
    }
    case ClaimSeat => barbershop.chairs ! ClaimSeat
    case GotoBarber(barber) => barber ! CutMe
    case msg => log.error("Unknown message %s", msg)
  }
}

