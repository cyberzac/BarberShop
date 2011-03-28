package se.cygni.barbershop

import akka.actor.ActorRef


case class Barbershop(sign:ActorRef, chairs:ActorRef, waitingLine:ActorRef, door:ActorRef) {

}