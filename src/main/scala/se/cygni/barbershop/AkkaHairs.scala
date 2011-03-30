package se.cygni.barbershop

import akka.actor.Actor._
import scala.math.random


object AkkaHairs {

  def main(args: Array[String]) {
    val numberOfCustomers = 20
    val numberOfChairs = 10
    val maxLine = 10
    val barberNames = List("Edward", "Jean-Paul", "Gulletussan")

    val sign = actorOf[Sign]
    val line = actorOf(new Line(maxLine))
    val chairs = actorOf(new Chairs(numberOfChairs, line))
    val door = actorOf(new Door(numberOfCustomers))
    val shop = Barbershop(sign = sign, chairs = chairs, waitingLine = line, door = door)
    shop.start
    val barbers = barberNames map {name => actorOf(new Barber(name, sign = sign, chairs = chairs)).start}

    1.to(numberOfCustomers) foreach {
      id =>
      val wait:Long = delay
      log.debug("Waiting %d", wait)
      Thread.sleep(delay)
      actorOf(new Customer(id.toString, shop)).start
    }

    def delay: Long = {
      val max = 100
      val min = 0
      min + (random * (max - min)).intValue
    }
  }


}
