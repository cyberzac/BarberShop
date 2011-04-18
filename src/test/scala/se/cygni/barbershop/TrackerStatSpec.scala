package se.cygni.barbershop

import org.specs.Specification
import akka.util.TestKit

class TrackerStatSpec extends Specification with TestKit {

  "A TrackerState" should {
    val state = TrackerState(chairs = 5, maxLine = 5)

    "set a barber as sleeping" in {
      val barber = BarberRef(testActor)
      state.sleeping(barber).barbers.get(barber).get must_== " z "
    }

    "set a barber as cutting " in {
      val barber = BarberRef(testActor)
      val customer = CustomerRef(testActor)
      state.cutting(barber, customer).barbers.get(barber).get must_== testActor.getId
    }

    "increment the seenCustomers and rejected  when customerLeft(None) is called" in {
      val newState = state.customerLeft(None)
      newState.stats.total must_== 1
      newState.stats.rejected must_== 1
    }

    "increment only totalCustomers and rejected  when customerLeft(Some) is called" in {
      val newState = state.customerLeft(Some(CustomerStats()))
      newState.stats.total must_== 1
      newState.stats.rejected must_== 0
    }

    "set a chair a occupied with sitting" in {
      val chair = 3
      val customer = CustomerRef(testActor)
      state.sat(customer = customer, chair = chair).chairs(chair) must_== customer.id
    }

    "clear a chair with leftChair" in {
      val chair = 3
      val customer = CustomerRef(testActor)
      state.sat(customer = customer, chair = chair).leftChair(chair).chairs(chair) must_== "."
    }

    "enterLine queues a customer id" in {
      val customer = CustomerRef(testActor)
      state.enterLine(customer).line(0) must_== customer.id
    }

    "leftLine dequeues" in {
      val customer = CustomerRef(testActor)
      state.enterLine(customer).leftLine.line.isEmpty must_== true
    }

    "foldFormat to format nice string" in {
      state.foldFormat(Map(("arne", "Arne"), ("b", "B")).values) must_== "Arn   B "
      state.foldFormat(Map(("arne", "Arne"), ("b", "B")).keys) must_== "arn   b "
    }

  }
}