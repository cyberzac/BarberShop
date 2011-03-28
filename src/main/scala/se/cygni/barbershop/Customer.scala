package se.cygni.barbershop

import akka.actor.{ActorRef, Actor}

case class Customer(sign: ActorRef, chairs: ActorRef, door: ActorRef) extends Actor {


  override def preStart = {
    sign ! WakeUp
  }

  protected def receive = {
    case CutDone(time) => {
      log.info("Cut in %d, leaving", time)
      door ! Leaving
    }
    case Wait => chairs ! IsSeatAvailable
    case TakeASeat => log.info("sitting down")
    case NoSeatsAvailable => log.info("Standing in line")
    case msg => log.error("Unknown message %s", msg)
  }
}