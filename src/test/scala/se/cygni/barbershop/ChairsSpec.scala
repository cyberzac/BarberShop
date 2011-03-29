package se.cygni.barbershop

import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification

class ChairsSpec extends Specification with TestKit with TestStubs {

  "The chairs" should {

    val chairs = actorOf(new Chairs(2, waitingLine.ref)).start

    object barberStub extends Stub {
      def sendNextCustomer = chairs ! NextCustomer
    }

    doAfter {
      chairs stop
    }

    val duration = 200 millis

    "Reply with a TakeSeat message when there are seats available" in {
      within(duration) {
        chairs ! IsSeatAvailable
        expectMsg(TakeSeat)
      }
    }

    "Reply with a NoSeatsAvailable message when  no seat is available " in {
      within(duration) {
        chairs ! IsSeatAvailable //Takes seat 1
        chairs ! IsSeatAvailable // Takes seat 2
        chairs ! IsSeatAvailable
        expectMsgAllOf(TakeSeat, TakeSeat, NoSeatsAvailable)
      }
    }

    "Reply with a NoCustomersWaiting message on a NextCustomer message if all seats are empty" in {
      within(duration) {
        chairs ! NextCustomer
        expectMsg(NoCustomersWaiting)
      }
    }

    "When a barber sends a NextCustomer, send a GotoBarber to the first waiting customer and a TakeSeat to the waiting line" in {
      within(500 millis) {
        chairs ! IsSeatAvailable
        barberStub.sendNextCustomer
        expectMsgAllOf(TakeSeat, GotoBarber(barberStub.ref))
        waitingLine.expectMsg(TakeSeat)
      }
    }

      "After sending a TakeSeat to the waiting line, reply NoSeatsAvailable, until a ClaimSeat is received" in {
        within(500 millis) {
          chairs ! IsSeatAvailable // Customer 1 takes first seat
          expectMsg(TakeSeat)
          chairs ! IsSeatAvailable // Customer 2 takes second seat
          expectMsg(TakeSeat)
          barberStub.sendNextCustomer // Barber requests a customer, first customer goes to barber, seat offered to waiting line
          expectMsg(GotoBarber(barberStub.ref))
          waitingLine.expectMsg(TakeSeat)
          chairs ! IsSeatAvailable // customer 3 is rejected
          expectMsg(NoSeatsAvailable)
          chairs ! ClaimSeat // customer  from waiting line takes claimed seat
          chairs ! IsSeatAvailable // customer 45 is rejected
          expectMsg(NoSeatsAvailable)
        }
      }

      "Ensure that a NoCustomersWaiting response from the waiting line ends the wait for a ClaimSeat" in {
        within(500 millis) {
          chairs ! IsSeatAvailable // Customer 1 takes first seat
          expectMsg(TakeSeat)
          chairs ! IsSeatAvailable // Customer 2 takes second seat
          expectMsg(TakeSeat)
          barberStub.sendNextCustomer // Barber requests a customer, first customer goes to barber, seat offered to waiting line
          expectMsg(GotoBarber(barberStub.ref))
          waitingLine.expectMsg(TakeSeat)
          chairs ! IsSeatAvailable // customer 3 is rejected
          expectMsg(NoSeatsAvailable)
          chairs ! NoCustomersWaiting // no one is waiting in the line
          chairs ! IsSeatAvailable // customer 4 is accepted
          expectMsg(TakeSeat)
        }

      }
  }
}