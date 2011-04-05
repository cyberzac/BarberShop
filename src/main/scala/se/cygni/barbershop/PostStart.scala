package se.cygni.barbershop

import akka.actor.{ActorRef, Actor}

/**
 * Will set addresses for, line, sign and  chairs
 */
trait PostStart {
  actor: Actor =>

  var line: ActorRef = null
  var sign: ActorRef = null
  var chairs: ActorRef = null
  var tracker: ActorRef = null


  /**
   * Called when all actors in the barbershop are started
   */
  def postStart: Unit = {
    log.debug("Default postStarted")
  }

  override def preStart {
    actor.become {
      case barbershop: Barbershop => try { {
        line = barbershop.line
        sign = barbershop.sign
        chairs = barbershop.chairs
        tracker = barbershop.tracker
        postStart
      }
      } finally {unbecome}
    }
  }

}