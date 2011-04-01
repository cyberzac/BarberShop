package se.cygni.barbershop

import akka.actor.Actor._
import scala.math.random


object AkkaHairs {

  def main(args: Array[String]) {
    val numberOfCustomers = 50
    val numberOfChairs = 4
    val maxLine = 5
    val barberNames = List("Edward", "Jean-Paul", "Gulletussan")

    val sign = actorOf[Sign]
    val tracker = actorOf(new Tracker(numberOfCustomers, numberOfChairs, maxLine))
    val line = actorOf(new Line(maxLine, tracker))
    val chairs = actorOf(new Chairs(numberOfChairs, line, tracker))
    val shop = Barbershop(sign = sign, chairs = chairs, line = line, tracker = tracker)
    shop.start
    val barbers = barberNames map {name => actorOf(new Barber(name, sign = sign, chairs = chairs, tracker = tracker)).start}

    1.to(numberOfCustomers) foreach {
      id =>
      val wait:Long = poisson(50)
      log.debug("Waiting %d", wait)
      Thread.sleep(wait)
      actorOf(new Customer(id.toString, shop)).start
    }

   // def delay = poisson(100)

    def uniform: Long = {
      val max = 100
      val min = 0
      min + (random * (max - min)).intValue
    }
  }

  def poisson(lamda:Int): Int = {
    val L = math.exp(-lamda)
    var k = 0
    var p = 1D
    while(p > L) {
      k += 1
      p *= random
    }
    k -1
  }

}
