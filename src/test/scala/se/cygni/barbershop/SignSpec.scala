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

    val timeout = 200 millis

    "Replay with a Wait message on a RequestBarber message  if there are no sleeping barbers" in {
      sign ! RequestBarber
      expectMsg(timeout, Wait)
    }

    "Forward the RequestBarber as a CutMe message call to first sleeping barber" in {
      sign ! StartSleeping // A barber is free
      sign ! RequestBarber
      expectMsg(500 millis, CutMe)
    }

    "Remove the first barber from the sign after dispatching a RequestBarber" in {
      within(500 millis) {
        sign ! StartSleeping // A barber is free
        sign ! RequestBarber
        expectMsg(CutMe)
        sign ! RequestBarber // No free barbers
        expectMsg(Wait)
      }
    }


  }


}