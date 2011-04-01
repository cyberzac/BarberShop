package se.cygni.barbershop

import akka.actor.ActorRef

case class Barbershop(sign: ActorRef, chairs: ActorRef, line: ActorRef, tracker: ActorRef) {
  val parts = List(sign, chairs, line, tracker)
  def start: Unit = parts foreach (_.start)
  def stop:Unit = parts foreach (_.stop)
}