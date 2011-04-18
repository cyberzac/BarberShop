package se.cygni.barbershop

import org.specs.Specification


class StatisticsSpec extends Specification {

  "A TimeSum case class" should  {

    "extract the times from a CustomerStats" in {

      val timeSum = TimeSum( CustomerStats(1,2,4,7))
      (timeSum.cut, timeSum.sit, timeSum.stand)  must_== (3,2,1)
    }

    "define + function" in {
      val term1 = TimeSum(1,2,3)
      val term2 = TimeSum(10,10,10)
      term1 + term2 must_== TimeSum(11,12,13)
    }

    "define / function " in {
          TimeSum(20, 40, 60) / 20 must_== TimeSum(1, 2, 3)
    }


  }
}