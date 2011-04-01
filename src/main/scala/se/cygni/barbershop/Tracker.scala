package se.cygni.barbershop

import akka.actor.{ActorRef, Actor}
import collection.immutable.Queue

case class Tracker(totalCustomers: Int, numberOfChairs: Int, maxLine: Int) extends Actor {
  protected def receive = trackerReceive(TrackerState(numberOfChairs, maxLine))

  def trackerReceive(state: TrackerState): Receive = {
    case TrackLeaving(stats) => {
      val customerRef = self.sender.get
      customerRef.stop
      val customerId = customerRef.getId
      val nextState = state.customerLeft(stats)
      become(nextState)
      if (nextState.seen == totalCustomers) {
        log.info("Last customer left, closing shop")
        Actor.registry.shutdownAll
      }
    }
    case TrackSleeping => become(state.sleeping(BarberRef(sender)))
    case TrackCutting(customer) => become(state.cutting(BarberRef(sender), CustomerRef(customer)))
    case TrackSat(chair) => become(state.sat(CustomerRef(sender), chair))
    case TrackLeftChair(chair) => become(state.leftChair(chair))
    case TrackEnteredLine => become(state.enterLine(CustomerRef(sender)))
    case TrackLeftLine => become(state.leftLine)
  }

  def sender = self.sender.get

  def become(state: TrackerState) = {
    log.info("%s", state)
    super.become(trackerReceive(state))
  }

}

case class BarberRef(ref: ActorRef)

case class CustomerRef(ref: ActorRef) {
  val id = ref.getId
}

case class Chair(id: Int)

object TrackerState {
  def apply(chairs: Int, maxLine:Int) = new TrackerState(maxLine, Map[BarberRef, String](), Vector.fill(chairs)("."), Queue[String](), 0, 0)
}

case class TrackerState(maxLine:Int, barbers: Map[BarberRef, String], chairs: Vector[String], line: Queue[String], seen: Int, rejected: Int) {

  def sleeping(barber: BarberRef): TrackerState = copy(barbers = (barbers.updated(barber, " z ")))

  def cutting(barber: BarberRef, customer: CustomerRef): TrackerState = copy(barbers = barbers.updated(barber, customer.id))

  def sat(customer: CustomerRef, chair: Int): TrackerState = copy(chairs = chairs.updated(chair, customer.id))

  def leftChair(chair: Int): TrackerState = copy(chairs = chairs.updated(chair, "."))

  def enterLine(customer: CustomerRef): TrackerState = copy(line = (line.enqueue(customer.id)))

  def leftLine: TrackerState = {
    val (customer, newLine) = line.dequeue
    copy(line = newLine)
  }

  def customerLeft(statsOption: Option[CustomerStats]): TrackerState = {
    if (statsOption.isEmpty) {
      copy(seen = seen + 1, rejected = rejected + 1)
    } else {
      copy(seen = seen + 1)
    }
  }

  def foldFormat(it: Iterable[String]): String = {
    it.foldLeft("")(_ + "%3.3s ".format(_))
  }

  override def toString = {

    "|" + foldFormat(barbers.values) +
      "|" + foldFormat(chairs) +
      "|" + foldFormat(line ++ Vector.fill(maxLine - line.size)(".")) +
      "|%3d|%3d|".format(seen, rejected)
  }


}