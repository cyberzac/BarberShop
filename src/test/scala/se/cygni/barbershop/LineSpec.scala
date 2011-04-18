package se.cygni.barbershop

import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification

class LineSpec extends Specification with TestKit with TestStubs {

  "The Line" should {

    val line = actorOf(new Line(2)).start

    doBefore {
      line ! barbershop
    }

    doAfter {
      line stop
    }

    val timeout = 200 millis


    "Forward a RequestBaraber message if line is empty" in {
      line ! RequestBarber
      lounge.expectMsg(timeout, RequestBarber(testActor))
    }

    "Reply with a WaitInLine message when there are room" in {
      line ! Wait(customer1.ref)
      expectNoMsg(timeout)
      customer1.expectMsg(timeout, WaitInLine)
    }

    "Reply with a WaitInLine directly when line is not empty" in {
      line ! Wait(customer1.ref) //Stands in line, line not empty
      line ! RequestBarber
      expectMsg(timeout, WaitInLine)
    }

    "Reply with a LineFull message when  is no room " in {
      line ! Wait(customer1.ref) // Becomes first in line
      line ! RequestBarber // Becomes second
      expectMsg(timeout, WaitInLine)
      line ! RequestBarber // Line is full
      expectMsg(timeout, LineFull)
    }

    "Reply with a RequestBaraber(customer) on a NextCustomer if line is non empty" in {
      line ! Wait(customer1.ref) // Becomes first in line
      line ! NextCustomer
      lounge.expectMsg(timeout, RequestBarber(customer1.ref))
    }
  }
}