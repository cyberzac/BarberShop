package se.cygni.barbershop

import akka.actor.{PoisonPill, ActorRef}

case class Barbershop(sign: ActorRef, lounge: ActorRef, line: ActorRef, barbers: List[ActorRef], tracker: ActorRef) {
  val parts = List(sign, lounge, line, tracker) ::: barbers

  def start: Unit = {
    parts foreach (_.start)
    parts foreach (_ ! this)
  }

  def stop: Unit = parts foreach (_ ! PoisonPill)

}