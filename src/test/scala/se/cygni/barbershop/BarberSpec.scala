package se.cygni.barbershop

import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification

class BarberSpec extends Specification with TestKit {


  "A barber" should {

    val barber = actorOf(new Barber("Edward", sign = testActor, chairs= testActor)).start

    doBefore {
      // A barber always send a StartSleeping messages when started
      expectMsg(100 millis, StartSleeping)
    }

    doAfter {
      barber stop
    }

    "When sleeping, respond with cutDone and a NextCustomer message after a WakeUp" in {
      var messages = List[String]()
      within(900 millis) {
        barber ! WakeUp
        receiveWhile(700 millis) {
          case CutDone(time) => messages = "cutdone" :: messages
          case NextCustomer => messages = "next" :: messages
        }
        messages.reverse  must_==  (List("cutdone", "next")) // Reverse since we prepend messages
      }
    }

    "When working go to sleep on a NoCustomersWaiting message" in {
      var messages = List[String]()
      within(800 millis) {
        barber ! WakeUp
        ignoreMsg {
          case CutDone(time) => true
          case NextCustomer =>true
        }
        barber ! NoCustomersWaiting
        expectMsg(StartSleeping)
      }
    }
  }
}