package se.cygni.barbershop

import akka.actor.{PoisonPill, ActorRef}

case class Barbershop(sign: ActorRef, chairs: ActorRef, line: ActorRef, barbers: List[ActorRef], tracker: ActorRef) {
  val parts = List(sign, chairs, line, tracker) ::: barbers

  def start: Unit = {
    parts foreach (_.start)
    parts foreach (_ ! this)
  }

  def stop: Unit = parts foreach (_ ! PoisonPill)

}