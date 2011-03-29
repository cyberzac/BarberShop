package se.cygni.barbershop

import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification

class WaitinglineSpec extends Specification with TestKit with TestStubs {

  "The Waitingline" should {

    val line = actorOf(new Waitingline(2)).start

    doAfter {
      line stop
    }

    val duration = 200 millis

    "Reply with a WaitInLine message when there are room" in {
      within(duration) {
        line ! TryGetInLine
        expectMsg(WaitInLine)
      }
    }

    "Reply with a WaitingLineFull message when  is no room " in {
      within(duration) {
        line ! TryGetInLine // Becomes first in line
        expectMsg(WaitInLine)
        line ! TryGetInLine // Becomes second
        expectMsg(WaitInLine)
        line ! TryGetInLine // Line is full
        expectMsg(WaitinglineFull)
      }
    }

    "Reply with a NoCustomersWaiting message on a TakeSeat message if no one is in line" in {
      within(duration) {
        line ! TakeSeat
        expectMsg(NoCustomersWaiting)
      }
    }


    "When a TakeSeat message is received, send the first customer to claim the seat" in {
      within(500 millis) {
        line ! TryGetInLine
        expectMsg(WaitInLine)
        line ! TakeSeat
        expectMsg(ClaimSeat)
        line ! TakeSeat
        expectMsg(NoCustomersWaiting)
      }
    }
  }
}