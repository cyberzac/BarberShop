package se.cygni.barbershop

import akka.actor.ActorRef

case class Barbershop(sign: ActorRef, chairs: ActorRef, waitingLine: ActorRef, door: ActorRef) {
  val parts = List(sign, chairs, waitingLine, door)
  def start: Unit = parts foreach (_.start)
  def stop:Unit = parts foreach (_.stop)
}