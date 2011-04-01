package se.cygni.barbershop

import org.specs.Specification
import akka.actor.Actor._
import akka.util.duration._
import akka.util.TestKit

class CustomerSpec extends Specification with TestKit with TestStubs {


  "A customer" should {

    val timeout = 200 millis
    val customer = actorOf(new Customer("dut", barbershopStub)).start

    doBefore {
      // A customer always sends a RequestBarber message when started
      sign.expectMsg(timeout, RequestBarber)
    }

    doAfterSpec {
      customer.stop
    }


    "Leave after beeing cut" in {
      customer ! CutDone
      tracker.expectMsgClass(timeout, classOf[TrackLeaving])
    }

    "Sends a CutMe message to the barber when receiving a GotoBarber message" in {
      object barber extends Stub
      customer ! TakeChair(1)
      customer ! GotoBarber(barber.ref)
      barber.expectMsg(timeout, CutMe)
    }

    "Query Chairs for a chair  if beeing told to Wait " in {
      customer ! Wait
      chairs.expectMsg(timeout, IsSeatAvailable)
    }

    "Sit down and wait if receiving a TakeChair message" in {
      customer ! TakeChair(1)
      expectNoMsg(timeout)
    }

    "Stand in line if receiving a WaitInLine message" in {
      customer ! WaitInLine
      expectNoMsg(timeout)
    }

    "Try standing in line if being told that there are no seats available" in {
      customer ! NoSeatsAvailable
      line.expectMsg(timeout, GetInLine)
    }

    "Claim a chair when receiving a ClaimChair message" in {
      customer ! ClaimChair
      chairs.expectMsg(timeout, ClaimChair)
    }

    "Leave if waiting live is full" in {
      customer ! LineFull
      tracker.expectMsg(timeout, TrackLeaving(None))
    }

    "Send a TrackerEnteredLine on a WaitInLine message" in {
      customer ! WaitInLine
      tracker.expectMsg(TrackEnteredLine)
    }

  }

}