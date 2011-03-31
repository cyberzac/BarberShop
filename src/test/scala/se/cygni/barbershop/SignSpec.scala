package se.cygni.barbershop

import org.specs.Specification
import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._


class SignSpec extends Specification with TestKit {

  val sign = actorOf[Sign].start

  "The sign" should {
    doAfter {
      sign stop
    }

    val duration = 100 millis

    "Replay with a Wait message on a Wakeup message  if there are no sleeping barbers" in {
      within(duration) {
        sign ! EnteredShop
        expectMsg(Wait)
      }
    }

    "Forward the EnteredShop as a CutMe message call to first sleeping barber" in {
      within(500 millis) {
        sign ! StartSleeping // A barber is free
        sign ! EnteredShop
        expectMsg(CutMe)
      }
    }

    "Remove the first barber from the sign after dispatching a EnteredShop" in {
      within(500 millis) {
        sign ! StartSleeping // A barber is free
        sign ! EnteredShop
        expectMsg(CutMe)
      }
      within(duration) {
        sign ! EnteredShop  // No free barbers
        expectMsg(Wait)
      }
    }


  }


}