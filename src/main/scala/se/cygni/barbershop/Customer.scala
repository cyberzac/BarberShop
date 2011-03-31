package se.cygni.barbershop

import akka.actor.Actor

case class Customer(id:String, barbershop: Barbershop) extends Actor {

  self.id = id

  override def preStart = {
    log.info("%s entered shop", id)
    barbershop.sign ! EnteredShop
  }

  protected def receive = customerReceive(CustomerStats())

def customerReceive(stats:CustomerStats): Receive = {
  case Cutting =>
    become(customerReceive(stats.cut))
    case CutDone => {
      log.info("%s leaving, %s", id, stats.done)
      barbershop.door ! Leaving
    }
    case Wait => barbershop.chairs ! IsSeatAvailable
    case TakeSeat => {
      log.info("%s sitting down", id)
      become(customerReceive(stats.sit))
    }
    case WaitInLine => {
      log.info("%s waiting in line", id)
      become(customerReceive(stats.stand))
    }
    case NoSeatsAvailable => barbershop.waitingLine ! TryGetInLine
    case WaitinglineFull => {
      log.info("%s no place to stand, I'm leaving", id)
      barbershop.door ! Leaving
    }
    case ClaimSeat =>{
      barbershop.chairs ! ClaimSeat
      become(customerReceive(stats.sit))
    }
    case GotoBarber(barber) => barber ! CutMe
  }
}

object CustomerStats {
  def apply() = new CustomerStats(0L, 0L, 0L, 0L)
}

case class CustomerStats(standAt:Long, satAt:Long, cutAt:Long, doneAt:Long) {
  def stand:CustomerStats = copy(standAt = System.currentTimeMillis)
  def sit:CustomerStats =  copy(satAt = System.currentTimeMillis)
  def cut:CustomerStats = copy(cutAt = System.currentTimeMillis)
  def done:CustomerStats = copy(doneAt = System.currentTimeMillis)

  val timeStanding = if (standAt == 0) 0 else (if (satAt == 0) cutAt else satAt) - standAt
  val timeSitting = if (satAt == 0) 0 else cutAt - satAt
  val timeCut = if (cutAt == 0) 0 else doneAt - cutAt
  val timeTotal = timeStanding + timeSitting +timeCut

  override def toString = "standing=%d sitting=%d, cutting=%d, total=%d".format(timeStanding, timeSitting, timeCut, timeTotal)
}




