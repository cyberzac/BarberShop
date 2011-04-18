package se.cygni.barbershop

import org.specs.Specification
import akka.actor.Actor._
import akka.util.duration._
import akka.util.TestKit

class CustomerSpec extends Specification with TestKit with TestStubs {


  "A customer" should {

    val timeout = 200 millis
    val customer = actorOf(new Customer("dut", barbershop)).start

    doBefore {
      // A customer always sends a RequestBarber message when started
      line.expectMsg(timeout, RequestBarber)
    }

    doAfterSpec {
      customer.stop
    }


    "Leave after beeing cut" in {
      customer ! CutDone
      tracker.expectMsgClass(timeout, classOf[TrackLeaving])
    }

    "Sit down and wait if receiving a TakeChair message" in {
      customer ! TakeChair(1)
      expectNoMsg(timeout)
    }

    "Stand in line if receiving a WaitInLine message" in {
      customer ! WaitInLine
      expectNoMsg(timeout)
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