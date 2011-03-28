package se.cygni.barbershop

import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification

class BarberSpec extends Specification with TestKit with TestStubs{


  "A barber" should {

    val barber = actorOf(new Barber("Edward", sign = barbershopStub.sign, chairs= barbershopStub.chairs)).start

    doBefore {
      // A barber always send a StartSleeping messages when started
      sign.expectMsg(100 millis, StartSleeping)
    }

    doAfter {
      barber stop
    }

    "Respond with CutDone and a NextCustomer toi the Chairs after a WakeUp" in {
      within(900 millis) {
        barber ! WakeUp
        expectMsgClass(classOf[CutDone])
        chairs.expectMsg(NextCustomer)
      }
    }

    "Respond with a StartSleeping on a NoCustomersWaiting message" in {
      within(800 millis) {
        barber ! NoCustomersWaiting
        sign.expectMsg(StartSleeping)
      }
    }

    "Respond with a CutDone and NextCustomer to the Chairs on a  CutMe message" in {
     within(900 millis) {
        barber ! WakeUp
        expectMsgClass(classOf[CutDone])
        chairs.expectMsg(NextCustomer)
      }
    }
  }
}