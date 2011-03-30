package se.cygni.barbershop

import org.specs.Specification
import akka.actor.Actor._
import akka.util.duration._
import akka.util.TestKit

class CustomerSpec extends Specification with TestKit with TestStubs {


  "A customer" should {

    val duration = 200 millis
    val customer = actorOf(new Customer("dut", barbershopStub)).start

    doBefore {
      // A customer always sends a WakeUp message when started
      sign.expectMsg(duration, WakeUp)
    }

    doAfterSpec {
      customer.stop
    }


    "Leave after beeing cut" in {
      customer ! CutDone(300)
      door.expectMsg(duration, Leaving)
    }

    object barber extends Stub
    "Sends a CutMe message to the barber if receiving a GotoBarber message" in {
      customer ! GotoBarber(barber.ref)
      barber.expectMsg(duration, CutMe)
    }

    "Try to get a chair if beeing told to Wait " in {
      customer ! Wait
      chairs.expectMsg(duration, IsSeatAvailable)
    }

    "Sit down and wait if receiving a TakeSeat message" in {
      customer ! TakeSeat
      expectNoMsg(duration)
    }

    "Stand in line if receiving a StandInLine message" in {
      customer ! WaitInLine
      expectNoMsg(duration)
    }

    "Try standing in line if being told that there are no seats available" in {
      customer ! NoSeatsAvailable
      waitingLine.expectMsg(duration, TryGetInLine)
    }

    "Claim a seat if receiving a Claimseat message" in {
      customer ! ClaimSeat
      chairs.expectMsg(duration, ClaimSeat)
    }

    "Leave if waiting live is full" in {
      customer ! WaitinglineFull
      door.expectMsg(duration, Leaving)
    }

  }

}