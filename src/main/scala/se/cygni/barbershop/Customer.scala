package se.cygni.barbershop

import akka.actor.Actor

case class Customer(barbershop: Barbershop) extends Actor {

  override def preStart = {
    barbershop.sign ! WakeUp
  }

  protected def receive = {
    case CutDone(time) => {
      log.info("Cut in %d, leaving", time)
      barbershop.door ! Leaving
    }
    case Wait => barbershop.chairs ! IsSeatAvailable
    case TakeSeat => log.info("Sitting down")
    case WaitInLine => log.info("Waiting in line")
    case NoSeatsAvailable => barbershop.waitingLine ! TryGetInLine
    case WaitinglineFull => barbershop.door ! Leaving
    case ClaimSeat => barbershop.chairs ! ClaimSeat
    case GotoBarber(barber) => barber ! CutMe
    case msg => log.error("Unknown message %s", msg)
  }
}