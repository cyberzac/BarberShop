package se.cygni.barbershop

import akka.actor.ActorRef

/**
 * Request for a barber
 */
case object WakeUp

/**
 * Barber begins to cut
 */
case object Cutting

/**
 * Hair cut is done
 * @param time, how long time the haircut took
 */
case object  CutDone

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
case object TakeSeat

/**
 * There are no free chairs
 */
case object NoSeatsAvailable

/**
 * There are no waiting customers
 */
case object NoCustomersWaiting

/**
 * Customer is leaving
 */
case object Leaving

/**
 * A custmer want to be cut
 */
case object CutMe

/**
 * Goto the specified barber
 */
case class GotoBarber(barber:ActorRef)

/**
 * Customer tries to get in the line
 */
case object TryGetInLine

/**
 * Customer is allowed into the waiting line
 */
case object WaitInLine

/**
 *   Waiting line is full
 */
case object WaitinglineFull

/**
 * Customer in waiting line claims a seat
 */
case object ClaimSeat


