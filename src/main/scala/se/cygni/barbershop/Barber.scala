package se.cygni.barbershop

import scala.math.random
import akka.actor.{ActorRef, Actor}

case class Barber(name: String, sign: ActorRef, chairs: ActorRef) extends Actor {

  self.id=name

  override def preStart = {
    log.info("%s came to work", name)
    startSleeping
  }

  override def postStop = {
    log.info("%s going home", name)
  }

  private def startSleeping = {
    log.info("%s going to sleep", name)
    sign ! StartSleeping
  }


  def unknownMessage(unknown: Any): Unit = {
    log.warn("unknown message %s", unknown)
  }

   protected def receive = {
      case WakeUp => cut()
      case CutMe => cut()
      case NoCustomersWaiting => startSleeping
      case m => unknownMessage(m)
  }

  def cut(): Unit = {
    val customer = self.sender.get.getId
    log.info("%s is cutting %s", name, customer)
    val time = cutTime
    Thread.sleep(time)
    log.info("%s cut %s in %d ms", name, customer, time)
    self.reply(CutDone(time))
    log.info("%s calls for next customer", name)
    chairs ! NextCustomer
  }

  def cutTime: Long = {
    val maxCutTime = 600
    val minCutTime = 300
    minCutTime + (random * (maxCutTime - minCutTime)).intValue
  }

}

