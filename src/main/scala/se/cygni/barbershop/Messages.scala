package se.cygni.barbershop

/**
 * Request for a barber
 */
  case object WakeUp

/**
 * Hair cut is done
 * @param time, how long time the haircut took
 */
  case class CutDone(time:Long)

/**
 * Barber is sleeping
 * @param time, when started sleeping
 */
  case object StartSleeping

/**
 * No barbers free please wait
 */
case object Wait

/**
 * Barber wants the next waiting customer
 */
case object NextCustomer

/**
 * Is there a free chair?
 */
case object IsSeatAvailable

/**
 * Take a seat in the waiting room
 */
case object TakeASeat

/**
 * There are no free chairs
 */
case object NoSeatsAvailable

/**
 * There are no waiting customers
 */
case object NoCustomersWaiting



