package se.cygni.barbershop

import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification


class ChairsSpec extends Specification with TestKit {

  "The chairs" should {

      val chairs = actorOf(new Chairs(2)).start

    "Reply with a TakeASeat message if there are seats available" in {
      within(100 millis) {
        chairs ! IsSeatAvailable
        expectMsg(TakeASeat)
      }
    }

    "Reply with a NoSeatsAvailable message if there is no seat available " in {
      within(100 millis) {
        chairs ! IsSeatAvailable
        chairs ! IsSeatAvailable
        chairs ! IsSeatAvailable
        expectMsgAllOf(TakeASeat, TakeASeat, NoSeatsAvailable)
      }
    }

    "Reply with a NoCustomersWaiting message on a NextCustomer message if all seats are empty" in {
      within(100 millis) {
        chairs ! NextCustomer
        expectMsg(NoCustomersWaiting)
      }


    }



  }
}