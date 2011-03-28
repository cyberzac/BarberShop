package se.cygni.barbershop

import scala.math.random
import akka.actor.{ActorRef, Actor}

case class Barber(name:String, sign:ActorRef, chairs: ActorRef) extends Actor {

  override def preStart = {
    log.info("%s came to work", name)
    sign ! Sleeping
  }


  override def postStop = {
    log.info("%s going home", name)
  }

  protected def receive = sleeping

    def unknownMessage(unknown: Any): Unit = {
      log.warn("unknown message %s", unknown)
    }

  val sleeping: Receive = {
    case WakeUp => cut()
    case m => unknownMessage(m)
  }

  val working: Receive = {
    case WakeUp => log.info("Ignoring WakeUp")
    case m => unknownMessage(m)
  }

  def cut(): Unit = {
    log.info("Cutting")
    become(working)
    val time = cutTime
    Thread.sleep(time)
    log.info("cutted in %d", time)
    self.reply(CutDone(time))
    chairs ! Next
  }

  def cutTime: Long = {
    val maxCutTime = 600
     val minCutTime = 300

    minCutTime + (random * (maxCutTime-minCutTime)).intValue
  }

}

