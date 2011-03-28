package se.cygni.barbershop

import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification

class BarberSpec extends Specification with TestKit {


  "A barber" should {

    val barber = actorOf(new Barber("Edward", sign = testActor, chairs= testActor)).start

    doBefore {
      // A barber always send a Sleeping messages when started
      expectMsg(100 millis, Sleeping)
    }

    doAfter {
      barber.stop
    }

    "When sleeping, respond with cutDone and a Next message after a WakeUp" in {
      var messages = List[String]()
      within(800 millis) {
        barber ! WakeUp
        receiveWhile(700 millis) {
          case CutDone(time) => messages = "cutdone" :: messages
          case Next => messages = "next" :: messages
        }
        messages  must_==  (List("next", "cutdone")) // Reverse since we prepend messages
      }
    }

  }
}