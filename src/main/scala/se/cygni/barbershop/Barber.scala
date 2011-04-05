package se.cygni.barbershop

import scala.math.random
import akka.actor.{ActorRef, Actor}

case class Barber(name: String) extends Actor with PostStart {

  self.id = name

  override def postStart() = {
    log.debug("%s came to work", name)
    startSleeping
  }

  override def postStop = {
    log.debug("%s going home", name)
  }

  private def startSleeping = {
    log.debug("%s going to sleep", name)
    sign ! Sleeping
    tracker ! TrackSleeping
  }


  protected def receive = {

    case RequestBarber(customer) => cut(customer)
    case RequestBarber => cut(self.sender.get)
    case NoCustomersWaiting => startSleeping
  }

  def cut(customer: ActorRef): Any = {
    log.debug("%s is cutting %s", name, customer.getId)
    customer ! Cutting
    tracker ! TrackCutting(customer)
    val time = cutTime
    Thread.sleep(time)
    log.debug("%s cut %s in %d ms", name, customer.getId, time)
    customer ! CutDone
    tracker ! TrackCutDone
    log.debug("%s calls for next customer", name)
    chairs ! NextCustomer
  }

  def cutTime: Long = {
    val maxCutTime = 600
    val minCutTime = 300
    minCutTime + (random * (maxCutTime - minCutTime)).intValue
  }


}

