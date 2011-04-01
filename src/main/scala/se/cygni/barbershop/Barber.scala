package se.cygni.barbershop

import scala.math.random
import akka.actor.{ActorRef, Actor}

case class Barber(name: String, sign: ActorRef, chairs: ActorRef, tracker:ActorRef) extends Actor {

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
    tracker ! TrackSleeping
  }


   protected def receive = {
      case CutMe => cut()
      case NoCustomersWaiting => startSleeping
  }

  def cut(): Unit = {
    val customer = self.sender.get
    log.info("%s is cutting %s", name, customer.getId)
    customer ! Cutting
    tracker ! TrackCutting(customer)
    val time = cutTime
    Thread.sleep(time)
    log.info("%s cut %s in %d ms", name, customer.getId, time)
    self.reply(CutDone)
    tracker ! TrackCutDone
    log.info("%s calls for next customer", name)
    chairs ! NextCustomer
  }

  def cutTime: Long = {
    val maxCutTime = 600
    val minCutTime = 300
    minCutTime + (random * (maxCutTime - minCutTime)).intValue
  }

}

