package se.cygni.barbershop

import collection.immutable.Queue
import akka.actor.{ActorRef, Actor}

case class Chairs(count: Int) extends Actor with PostStart {

  self.id = "chairs"

  case class Entry(customer: ActorRef, chair: Int)

  protected def receive = chairsReceive(Queue[Entry](), Range(0, count).foldLeft(List[Int]())(_ :+ _))

  def chairsReceive(taken: Queue[Entry], free: List[Int]): Receive = {

    case requestBarber@RequestBarber(customer) => {
      if (taken.isEmpty) {
        log.debug("All chairs are empty, try the sign for a barber %s", customer.id)
        sign ! requestBarber
      } else {
        queueCustomer(customer, taken, free)
      }
    }

    case Wait(customer) => {
      queueCustomer(customer, taken, free)
    }

    case NextCustomer => {
      if (taken.isEmpty) {
        self.reply(NoCustomersWaiting)
        log.debug("All chairs are empty")
      } else {
        val (Entry(customer, chair), tail) = taken.dequeue
        log.debug("sending %s to %s", customer.id, self.sender.get.id)
        customer ! GotoBarber(self.sender.get)
        tracker ! TrackLeftChair(chair)
        log.debug("offer a chairs to customers standing in line")
        line ! NextCustomer
        become(chairsReceive(tail, chair :: free))
      }
    }
  }

  def queueCustomer(customer: ActorRef, taken: Queue[Entry], free: List[Int]): Unit = {
    if (free.isEmpty) {
      log.debug("All chairs are taken, try the line %s", customer.id)
      line ! Wait(customer)
    } else {
      val (chair :: tail) = free
      val newTaken = taken enqueue Entry(customer, chair)
      log.debug("told %s to sit down in chair %d", customer.id, chair)
      customer ! TakeChair(chair)
      become(chairsReceive(newTaken, tail))
    }
  }

}
