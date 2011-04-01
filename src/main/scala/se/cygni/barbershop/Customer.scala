package se.cygni.barbershop

import akka.actor.Actor

case class Customer(id: String, barbershop: Barbershop) extends Actor {

  self.id = id

  override def preStart = {
    log.info("%s entered shop", id)
    barbershop.sign ! RequestBarber
  }

  protected def receive = customerReceive(CustomerStats())

  def customerReceive(stats: CustomerStats): Receive = {
    case Cutting =>
      become(customerReceive(stats.cut))
    case CutDone => {
      log.info("%s leaving, %s", id, stats.done)
      barbershop.tracker ! TrackLeaving(Some(stats))
    }
    case Wait => barbershop.chairs ! IsSeatAvailable
    case WaitInLine => {
      log.info("%s waiting in line", id)
      barbershop.tracker ! TrackEnteredLine
      become(customerReceive(stats.stand))
    }
    case NoSeatsAvailable => barbershop.line ! GetInLine
    case LineFull => {
      log.info("%s no place to stand, I'm leaving", id)
      barbershop.tracker ! TrackLeaving(None)
    }
    case TakeChair(chair) => {
      log.info("%s sitting down in chair %d", id, chair)
      barbershop.tracker ! TrackSat(chair)
      become(customerReceive(stats.sit(chair)))
    }
    case ClaimChair => barbershop.chairs ! ClaimChair
    case GotoBarber(barber) => {
      barber ! CutMe
    }
  }
}

object CustomerStats {
  def apply() = new CustomerStats(-1, 0L, 0L, 0L, 0L)
  def apply(standAt: Long, satAt: Long, cutAt: Long, doneAt: Long) = new CustomerStats(-1, standAt, satAt, cutAt, doneAt)
}

case class CustomerStats(chair: Int, standAt: Long, satAt: Long, cutAt: Long, doneAt: Long) {
  def stand: CustomerStats = copy(standAt = System.currentTimeMillis)

  def sit(c: Int): CustomerStats = copy(chair = c, satAt = System.currentTimeMillis)

  def cut: CustomerStats = copy(cutAt = System.currentTimeMillis)

  def done: CustomerStats = copy(doneAt = System.currentTimeMillis)

  val timeStanding = if (standAt == 0) 0 else (if (satAt == 0) cutAt else satAt) - standAt
  val timeSitting = if (satAt == 0) 0 else cutAt - satAt
  val timeCut = if (cutAt == 0) 0 else doneAt - cutAt
  val timeTotal = timeStanding + timeSitting + timeCut

  override def toString = "standing=%d sitting=%d, cutting=%d, total=%d".format(timeStanding, timeSitting, timeCut, timeTotal)
}




