package se.cygni.barbershop

import org.specs.Specification

class CustomerStatSpec extends Specification {

  "A CustomerStat" should {

    "return standing time 0 if standAt is zero " in {
      CustomerStats().timeStanding must_== 0
      CustomerStats(0,1,2,3).timeStanding must_== 0
    }

    "return standing time as diff between standAt and satAt " in {
      CustomerStats(1, 5, 0, 0).timeStanding must_== 4
      CustomerStats(1, 6, 7, 8).timeStanding must_== 5
    }

    "return standing time as diff between standAt and cutAt if satAt is zero " in {
      CustomerStats(1, 0, 5, 0).timeStanding must_== 4
      CustomerStats(1, 0, 6, 7).timeStanding must_== 5
    }

    "return sitting time 0 if satAt is zero" in {
      CustomerStats().timeSitting must_== 0
      CustomerStats(0,0, 1,3).timeSitting must_== 0
    }

    "return sitting time as diff between cutAt and satAt " in {
      CustomerStats(1, 5, 6, 0).timeSitting must_== 1
      CustomerStats(0, 4, 9, 15).timeSitting must_== 5
    }

     "return cut time 0 if cutAt is zero" in {
      CustomerStats().timeSitting must_== 0
    }

    "return cut time as diff between doneAt and cutAt " in {
      CustomerStats(1, 5, 6, 30).timeCut must_== 24
      CustomerStats(0, 4, 9, 15).timeCut must_== 6
    }

    "compute the total time as the sum of stand, sit and cut" in {
      CustomerStats().timeTotal must_== 0
      CustomerStats(1,3,7,19).timeTotal must_== 18
      CustomerStats(0,3,7,19).timeTotal must_== 16
      CustomerStats(0,0,7,19).timeTotal must_== 12
    }
  }


}