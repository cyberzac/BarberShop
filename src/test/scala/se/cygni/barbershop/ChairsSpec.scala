package se.cygni.barbershop

import akka.util.duration._
import akka.actor.Actor._
import org.specs.Specification
import akka.util.TestKit

class ChairsSpec extends Specification with TestKit with TestStubs {

  "The chairs" should {

    val chairs = actorOf(new Chairs(2)).start

    doBefore {
      chairs ! barbershop
    }
    doAfter {
      chairs stop
    }

    val timeout = 200 millis


    "Forward a RequestBaraber message all chairs are empty" in {
      line.sendMessage(chairs, RequestBarber(customer1.ref))
      sign.expectMsg(timeout, RequestBarber(customer1.ref))
    }

    "Send a TakeChair messages to the original customer on a Wait message" in {
      sign.sendMessage(chairs, Wait(customer1.ref))
      customer1.expectMsg(timeout, TakeChair(0))
    }

    "Send a TakeChair message on a RequestBarber message  if there are free chairs"  in {
      sign.sendMessage(chairs, Wait(customer1.ref))
      customer1.expectMsg(timeout, TakeChair(0))
      line.sendMessage(chairs, RequestBarber(customer2.ref))
      customer2.expectMsg(TakeChair(1))
    }

    "Send a Wait message to the line on  a Wait messages if all chairs are taken" in {
      sign.sendMessage(chairs, Wait(customer1.ref))
      customer1.expectMsg(timeout, TakeChair(0))
      sign.sendMessage(chairs, Wait(customer2.ref))
      customer2.expectMsg(timeout, TakeChair(1))
      line.sendMessage(chairs, RequestBarber(customer3.ref))
      line.expectMsg(timeout, Wait(customer3.ref))
    }

    "Reply with a NoCustomersWaiting message on a NextCustomer message if all seats are empty" in {
      barber1.sendMessage(chairs, NextCustomer)
      barber1.expectMsg(timeout, NoCustomersWaiting)
    }

    "When a barber sends a NextCustomer, send a GotoBarber to the first waiting customer, TrackLeftChair to the Tracker and a NextCustomer to the waiting line" in {
      sign.sendMessage(chairs, Wait(customer1.ref)) // Queue one customer
      customer1.expectMsg(timeout, TakeChair(0))
      barber1.sendMessage(chairs, NextCustomer)
      customer1.expectMsg(timeout, GotoBarber(barber1.ref))
      tracker.expectMsg(timeout, TrackLeftChair(0))
      line.expectMsg(timeout, NextCustomer)
    }
  }
}
