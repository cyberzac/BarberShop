package se.cygni.barbershop

import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification

class LineSpec extends Specification with TestKit with TestStubs {

  "The Line" should {

    val line = actorOf(new Line(2, tracker.ref)).start

    doAfter {
      line stop
    }

    val timeout = 200 millis

    "Reply with a WaitInLine message when there are room" in {
      within(timeout) {
        line ! GetInLine
        expectMsg(WaitInLine)
      }
    }

    "Reply with a WaitingLineFull message when  is no room " in {
      within(timeout) {
        line ! GetInLine // Becomes first in line
        expectMsg(WaitInLine)
        line ! GetInLine // Becomes second
        expectMsg(WaitInLine)
        line ! GetInLine // Line is full
        expectMsg(LineFull)
      }
    }

    "Reply with a NoCustomersWaiting message on a TakeChair message if no one is in line" in {
      within(timeout) {
        line ! FreeChair
        expectMsg(NoCustomersWaiting)
      }
    }


    "When a FreeChair message is received, send the first customer to claim the chair and a LeftLine to the tracker" in {
      within(500 millis) {
        line ! GetInLine
        expectMsg(WaitInLine)
        line ! FreeChair
        expectMsg(ClaimChair)
        tracker.expectMsg(timeout, TrackLeftLine)
        line ! FreeChair
        expectMsg(NoCustomersWaiting)
      }
    }
  }
}