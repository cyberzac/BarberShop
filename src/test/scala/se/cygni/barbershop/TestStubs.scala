package se.cygni.barbershop

import akka.util.TestKit


trait TestStubs {

  class Stub extends TestKit {
    val ref = testActor
  }

  val  sign = new Stub
  val  chairs  = new Stub
  val door = new Stub
  val waitingLine = new Stub
  val barbershopStub = Barbershop(sign = sign.ref, chairs = chairs.ref, door = door.ref, waitingLine = waitingLine.ref)

}