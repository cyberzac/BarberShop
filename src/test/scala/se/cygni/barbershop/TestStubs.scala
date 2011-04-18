package se.cygni.barbershop

import akka.util.TestKit
import akka.actor.Actor.log
import akka.actor.ActorRef


trait TestStubs {

  class Stub(name: String) extends TestKit {
    testActor.id = name
    log.debug("Created %s, id %s", name, testActor)
    val ref = testActor

    def sendMessage(receiver: ActorRef, message: Any) = {
      receiver ! message
    }
  }

  val sign = new Stub("sign")
  val lounge = new Stub("chairs")
  val tracker = new Stub("tracker")
  val line = new Stub("line")
  val customer1 = new Stub("customer1")
  val customer2 = new Stub("customer2")
  val customer3 = new Stub("customer3")
  val barber1 = new Stub("barber1")
  val barber2 = new Stub("barber2")
  val barber3 = new Stub("barber3")
  val barbers = List(barber1.ref, barber2.ref, barber3.ref)
  val barbershop = Barbershop(sign = sign.ref, lounge = lounge.ref, tracker = tracker.ref, line = line.ref, barbers = barbers)
}