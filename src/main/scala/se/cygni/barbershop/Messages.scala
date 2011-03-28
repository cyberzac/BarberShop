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
  case object Sleeping

/**
 * No barbers free please wait
 */
case object Wait

/**
 * Barber wants the next waiting customer
 */
case object Next



