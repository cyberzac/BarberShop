package se.cygni.barbershop

import akka.actor.ActorRef

/**
 * Messages to the tracker
 */
sealed trait TrackerMessages

/**
 * Barber is sleeping
 */
case object TrackSleeping extends TrackerMessages

/**
 * Barber is cutting
 */
case class TrackCutting(customer:ActorRef) extends TrackerMessages

/**
 * Customer is leaving
 */
case class TrackLeaving(stats:Option[CustomerStats]) extends TrackerMessages

/**
 * Customer is sitting down
 */
case class TrackSat(chair:Int)  extends TrackerMessages

/**
 * Customer left a chair
 */
case class TrackLeftChair(chair:Int) extends TrackerMessages

/**
 * Customer is getting in line
 */
case object TrackEnteredLine  extends TrackerMessages

/**
 * Customer left line
 */
case object TrackLeftLine extends TrackerMessages

/**
 * Barber is sleeping
 * @param time, when started sleeping
 */
case object StartSleeping

/**
 * Request for a barber
 */
case object RequestBarber


/**
 * A customer want to be cut
 */
case object CutMe

/**
 * There are no waiting customers
 */
case object NoCustomersWaiting

/**
 * Customer in waiting line claims a seat
 */
case object ClaimChair

/**
 * Barber wants the next waiting customer
 */
case object NextCustomer

/**
 * Is there a free chair?
 */
case object IsSeatAvailable

/**
 * No barbers free please wait
 */
case object Wait

/**
 * Barber begins to cut
 */
case object Cutting

/**
 * Hair cut is done
 */
case object  CutDone

/**
 * Take a seat in the waiting room
 */
case class TakeChair(chair:Int)

/**
 * Leave a chair
 */
case class LeftChair(chair:Int)

/**
 * There is a free chair
 */
case object FreeChair

/**
 * There are no free chairs
 */
case object NoSeatsAvailable

/**
 * Customer is allowed into the waiting line
 */
case class  WaitInLine(pos:Int)

/**
 *   Waiting line is full
 */
case object LineFull

/**
 * Customer left the line
 */
case object LeftLine

/**
 * Goto the specified barber
 */
case class GotoBarber(barber:ActorRef)

/**
 * Customer tries to get in the line
 */
case object GetInLine

