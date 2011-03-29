package se.cygni.barbershop

import org.specs.Specification
import akka.actor.Actor._
import akka.util.duration._
import akka.util.TestKit
class CustomerSpec extends Specification with TestKit with TestStubs {


  "A customer" should {

    val duration = 200 millis
    val customer = actorOf(new Customer(barbershopStub)).start

    doBefore {
      // A customer always sends a WakeUp message when started
      sign.expectMsg(duration, WakeUp)
    }

    doAfterSpec {
      customer.stop
    }

    /*
    "Send a WakeUp message when receiving the Init message" in {
      within(duration) {
        customer ! Init
        expectMsg(WakeUp)
      }
    }
    */

    "Leave after beeing cut" in {
      within(duration) {
        customer ! CutDone(300)
        door.expectMsg(Leaving)
      }
    }

    "Try to get a chair if beeing told to Wait " in {
      within(duration) {
        customer ! Wait
        chairs.expectMsg(IsSeatAvailable)
      }
    }

    "Sit down and wait if receiving a TakeSeat message" in {
      within(duration) {
        customer ! TakeSeat
        expectNoMsg
      }
    }

    "Stand in line if being told that there are no seats available" in {
      within(duration) {
        customer ! NoSeatsAvailable
        expectNoMsg
      }
    }

  }

}