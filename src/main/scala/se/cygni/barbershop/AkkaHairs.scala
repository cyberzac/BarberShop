package se.cygni.barbershop

import akka.actor.Actor._
import scala.math.random


object AkkaHairs {

  val numberOfCustomers = 200
     val numberOfChairs = 10
     val maxLine = 10
     val maxCutTime = 150
     val minCutTime = 10
     val barberNames = List("Edward", "Jean-Paul", "Gulletussan")

  def main(args: Array[String]) {

    val sign = actorOf[Sign]
    val tracker = actorOf(new Tracker(numberOfCustomers, numberOfChairs, maxLine))
    val lounge = actorOf(new Lounge(numberOfChairs))
    val line = actorOf(new Line(maxLine))
    val barbers = barberNames map {name => actorOf(new Barber(name, cutTime _))}
    val shop = Barbershop(sign = sign, lounge = lounge, line = line, tracker = tracker, barbers = barbers)
    shop start

    1.to(numberOfCustomers) foreach {
      id =>
        val wait: Long = poisson((maxCutTime-minCutTime)/(3*2))
        log.debug("Waiting %d", wait)
        Thread.sleep(wait)
        actorOf(new Customer(id.toString, shop)).start
    }
    Thread.sleep(1000)


    // def delay = poisson(100)

    def uniform: Long = {
      val max = 100
      val min = 0
      min + (random * (max - min)).intValue
    }
  }

  def poisson(lamda: Int): Int = {
    val L = math.exp(-lamda)
    var k = 0
    var p = 1D
    while (p > L) {
      k += 1
      p *= random
    }
    k - 1
  }

  def cutTime: Long = {
    minCutTime + (random * (maxCutTime - minCutTime)).intValue
  }

}
