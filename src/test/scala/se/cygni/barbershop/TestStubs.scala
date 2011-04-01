package se.cygni.barbershop

import akka.util.TestKit


trait TestStubs {

  class Stub extends TestKit {
    val ref = testActor
  }

  val  sign = new Stub
  val  chairs  = new Stub
  val tracker = new Stub
  val line = new Stub
  val barbershopStub = Barbershop(sign = sign.ref, chairs = chairs.ref, tracker = tracker.ref, line = line.ref)

}