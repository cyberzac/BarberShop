package se.cygni.barbershop

import akka.actor.{ActorRef, Actor}

case class Barber(name: String, cutTime: () => Long) extends Actor with PostStart {

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
  }

  def cut(customer: ActorRef): Any = {
    log.debug("%s is cutting %s", name, customer.getId)
    customer ! Cutting
    tracker ! TrackCutting(customer)
    val time = cutTime()
    Thread.sleep(time)
    log.debug("%s cut %s in %d ms", name, customer.getId, time)
    customer ! CutDone
    tracker ! TrackCutDone
    startSleeping
  }

}

