package se.cygni.barbershop

import org.specs.Specification
import akka.util.TestKit
import collection.immutable.Queue


class LoungeStateSpec extends Specification with TestKit with TestStubs {

  "The LoungeState" should {

    "Provide a chairs map mapping from chairs to Option[ActorRef]" in {
      val oc1 = OccupiedChair(customer1.ref, 1)
      val oc2 = OccupiedChair(customer2.ref, 2)
      val free = List(0, 3, 4)
      val state = LoungeState(Queue(oc2, oc1), free)
      state.chairs must_== Map(0->None, 1->Some(customer1.ref), 2->Some(customer2.ref), 3->None, 4 -> None)
    }
  }
}