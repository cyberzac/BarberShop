package se.cygni.barbershop

import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification
import akka.util.{duration, TestKit}

class ChairsSpec extends Specification with TestKit with TestStubs {

  "The chairs" should {

    val chairs = actorOf(new Chairs(2, line.ref, tracker.ref)).start

    object barberStub extends Stub {
      def sendNextCustomer = chairs ! NextCustomer
    }

    doAfter {
      chairs stop
    }

    val timeout = 200 millis

    "Reply with a TakeChair message when there are seats available" in {
      chairs ! IsSeatAvailable
      expectMsg(timeout, TakeChair(0))
    }

    "Reply with a NoSeatsAvailable message when  no seat is available " in {
      chairs ! IsSeatAvailable
      expectMsg(timeout, TakeChair(0))
      chairs ! IsSeatAvailable
      expectMsg(timeout, TakeChair(1))
      chairs ! IsSeatAvailable
      expectMsg(NoSeatsAvailable)
    }

    "Reply with a NoCustomersWaiting message on a NextCustomer message if all seats are empty" in {
      chairs ! NextCustomer
      expectMsg(timeout, NoCustomersWaiting)
    }

    "When a barber sends a NextCustomer, send a GotoBarber to the first waiting customer, TrackLeftChair to the Tracker and a FreeChair to the waiting line" in {
      within(500 millis) {
        chairs ! IsSeatAvailable
        barberStub.sendNextCustomer
        expectMsgAllOf(TakeChair(0), GotoBarber(barberStub.ref))
        tracker.expectMsg(100 millis, TrackLeftChair(0))
        line.expectMsg(FreeChair)
      }
    }

    "After sending a TakeChair to the waiting line, reply NoSeatsAvailable, until a ClaimChair is received" in {
      within(500 millis) {
        chairs ! IsSeatAvailable
        expectMsg(TakeChair(0))
        chairs ! IsSeatAvailable
        expectMsg(TakeChair(1))
        barberStub.sendNextCustomer // Barber requests a customer, first customer goes to barber, chair offered to waiting line
        expectMsg(GotoBarber(barberStub.ref))
        line.expectMsg(FreeChair)
        chairs ! IsSeatAvailable // customer 3 is rejected
        expectMsg(NoSeatsAvailable)
        chairs ! ClaimChair // customer  from waiting line takes claimed seat
        expectMsg(TakeChair(0))
        chairs ! IsSeatAvailable // customer 45 is rejected
        expectMsg(NoSeatsAvailable)
      }
    }

    "Ensure that a NoCustomersWaiting response from the waiting line ends the wait for a ClaimChair" in {
      within(500 millis) {
        chairs ! IsSeatAvailable // Customer 1 takes first seat
        expectMsg(TakeChair(0))
        chairs ! IsSeatAvailable // Customer 2 takes second seat
        expectMsg(TakeChair(1))
        barberStub.sendNextCustomer // Barber requests a customer, first customer goes to barber, seat offered to waiting line
        expectMsg(GotoBarber(barberStub.ref))
        line.expectMsg(FreeChair)
        chairs ! IsSeatAvailable // customer 3 is rejected
        expectMsg(NoSeatsAvailable)
        chairs ! NoCustomersWaiting // no one is waiting in the line
        chairs ! IsSeatAvailable // customer 4 is accepted
        expectMsg(TakeChair(0))
      }

    }
  }
}