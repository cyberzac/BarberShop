package se.cygni.barbershop

import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification
import akka.actor.Actor


class ChairsSpec extends Specification with TestKit with TestStubs {

  "The chairs" should {

      val chairs = actorOf(new Chairs(2)).start
      val barber = new Stub

    doAfter {
      chairs stop
    }

    "Reply with a TakeASeat message if there are seats available" in {
      within(100 millis) {
        chairs ! IsSeatAvailable
        expectMsg(TakeASeat)
      }
    }

    "Reply with a NoSeatsAvailable message if there is no seat available " in {
      within(100 millis) {
        chairs ! IsSeatAvailable
        chairs ! IsSeatAvailable
        chairs ! IsSeatAvailable
        expectMsgAllOf(TakeASeat, TakeASeat, NoSeatsAvailable)
      }
    }

    "Reply with a NoCustomersWaiting message on a NextCustomer message if all seats are empty" in {
      within(100 millis) {
        chairs ! NextCustomer
        expectMsg(NoCustomersWaiting)
      }
    }

  "Tell the first customer in line to go the the barber sending a NextCustomer with a GotoBarber message" in {
    object  barber extends Stub {
      def sendNextCustomer = chairs ! NextCustomer
    }
    within(500 millis) {
      chairs ! IsSeatAvailable
      barber.sendNextCustomer
      expectMsgAllOf(TakeASeat, GotoBarber(barber.ref))
    }
  }



  }
}