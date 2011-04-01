package se.cygni.barbershop

import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification

class BarberSpec extends Specification with TestKit with TestStubs {


  "A barber" should {

    val barber = actorOf(new Barber("Edward", sign = barbershopStub.sign, chairs = barbershopStub.chairs, barbershopStub.tracker)).start

    doBefore {
      // A barber always send a StartSleeping messages when started
      sign.expectMsg(100 millis, StartSleeping)
    }

    doAfter {
      barber stop
    }

    "Respond with Cutting, CutDone and a NextCustomer toi the Chairs after a CutMe" in {
      barber ! CutMe
      expectMsgAllOf(700 millis, Cutting, CutDone)
      chairs.expectMsg(100 millis, NextCustomer)
    }

    "Respond with a StartSleeping on a NoCustomersWaiting message" in {
      barber ! NoCustomersWaiting
      sign.expectMsg(100 millis, StartSleeping)
      tracker.expectMsg(100 millis, TrackSleeping)
    }

  }
}