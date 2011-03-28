package se.cygni.barbershop

import org.specs.Specification
import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._


class SignSpec extends Specification with TestKit {

  val sign = actorOf[Sign].start

  "The sign" should {

    "Replay with a Wait message on a Wakeup message  if there are no sleeping barbers" in {
      within(100 millis) {
        sign ! WakeUp
        expectMsg(Wait)
      }
    }

    "Forward the WakeUp call to first sleeping barber" in {
      within(500 millis) {
        sign ! StartSleeping // A barber is free
        sign ! WakeUp
        expectMsg(WakeUp)
      }
    }

    "Remove the first barber from the sign after dispatching a WakeUp" in {
      within(500 millis) {
        sign ! StartSleeping // A barber is free
        sign ! WakeUp
        expectMsg(WakeUp)
      }
      within(100 millis) {
        sign ! WakeUp  // No free barbers
        expectMsg(Wait)
      }
    }


  }


}