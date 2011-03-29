package se.cygni.barbershop

import akka.actor.{ActorRef, Actor}

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
    case TakeSeat => log.info("sitting down")
    case NoSeatsAvailable => log.info("Standing in line")
    case msg => log.error("Unknown message %s", msg)
  }
}