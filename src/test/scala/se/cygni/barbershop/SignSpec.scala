package se.cygni.barbershop

import org.specs.Specification
import akka.util.TestKit
import akka.util.duration._
import akka.actor.Actor._


class SignSpec extends Specification with TestKit with TestStubs {

  val dut = actorOf[Sign].start

  "The sign" should {

    doBefore {
      dut ! barbershop
    }

    doAfter {
      dut stop
    }

    val timeout = 200 millis

    "Replay with a Wait message on a RequestBarber message  if there are no sleeping barbers" in {
      chairs.sendMessage(dut, RequestBarber(customer1.ref))
      chairs.expectMsg(timeout, Wait(customer1.ref))
    }

    "Forward the RequestBarber message to first sleeping barber" in {
      barber1.sendMessage(dut, Sleeping) // A barber is free
      val requestBarber = RequestBarber(customer1.ref)
      chairs.sendMessage(dut, requestBarber)
      barber1.expectMsg(timeout, requestBarber)
    }

    "Remove the first barber from the sign after dispatching a RequestBarber" in {
      within(500 millis) {
        barber1.sendMessage(dut, Sleeping) // A barber is free
        val requestBarber = RequestBarber(customer1.ref)
        chairs.sendMessage(dut, requestBarber)
        barber1.expectMsg(timeout, requestBarber)
        chairs.sendMessage(dut, RequestBarber(customer2.ref))
        chairs.expectMsg(timeout, Wait(customer2.ref))
      }
    }

  }


}