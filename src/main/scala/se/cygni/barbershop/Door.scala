package se.cygni.barbershop

import akka.actor.Actor


case class Door(totalCustomers:Int) extends Actor {
  protected def receive = doorReceive(0)

  def doorReceive(seenCustomers:Int): Receive = {
    case Leaving => {
      val customerRef = self.sender.get
      customerRef.stop
      val total = seenCustomers + 1
      log.info("%s left, total customers %d", customerRef.getId, total)
      become(doorReceive(total))
      if (total == totalCustomers) {
        log.info("Last customer left, closing shop")
        Actor.registry.shutdownAll
      }
    }
  }
}
