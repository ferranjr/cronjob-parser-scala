package com.cronparser.parser.models

import com.cronparser.parser.error.ParsingError
import com.cronparser.parser.utils.HasRangeLimit
import org.scalatest.EitherValues
import org.scalatest.Inspectors.forAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CronPartsSpec
  extends AnyFlatSpec
    with Matchers
    with EitherValues {

  "Minute" should "not be possible to create outside the valid minutes in an hour" in {
    forAll(List(-1, 60, 100)) { value =>

      val error = HasRangeLimit[Minute].exactAt(value).left.value
      error shouldBe ParsingError.InvalidValuesForCronPart(List(value))
    }
  }

  it should "be possible to create if is a valid minute number" in {
    forAll(0 to 59) { value =>
      val result = HasRangeLimit[Minute].exactAt(value).value
      result shouldBe Minute(List(value))
    }
  }

  it should "be possible to create if is a valid list of minutes" in {
    val minutes = Range(0, 59, 10).toList
    val result = HasRangeLimit[Minute].atTimes(minutes).value
    result shouldBe Minute(minutes)
  }

  it should "not be possible to create if is a list containing invalid minutes" in {
    val minutes = List(-1, 10)
    val error = HasRangeLimit[Minute].atTimes(minutes).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(minutes)
  }

  it should "be possible to create a `every` X minutes if the N is in minutes range" in {
    val every = 20
    val result = HasRangeLimit[Minute].every(every).value
    result shouldBe Minute(List(0, 20, 40))
  }

  it should "not be possible to create a `every` X minutes if the N is NOT in minutes range" in {
    val every = 60
    val error = HasRangeLimit[Minute].every(every).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(List(every))
  }

  "Hour" should "not be possible to create outside the valid hours in a day" in {
    forAll(List(-1, 60, 100)) { value =>
      val error = HasRangeLimit[Hour].exactAt(value).left.value
      error shouldBe ParsingError.InvalidValuesForCronPart(List(value))
    }
  }

  it should "be possible to create if is a valid hour number" in {
    forAll(0 to 23) { value =>
      val result = HasRangeLimit[Hour].exactAt(value).value
      result shouldBe Hour(List(value))
    }
  }

  it should "be possible to create if is a valid list of hours" in {
    val hours = Range(0, 23, 2).toList
    val result = HasRangeLimit[Hour].atTimes(hours).value
    result shouldBe Hour(hours)
  }

  it should "not be possible to create if is a list containing invalid hours" in {
    val hours = List(-1, 10)
    val error = HasRangeLimit[Hour].atTimes(hours).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(hours)
  }

  it should "be possible to create a `every` X hours if the N is in hours range" in {
    val every = 6
    val result = HasRangeLimit[Hour].every(every).value
    result shouldBe Hour(List(0, 6, 12, 18))
  }

  it should "not be possible to create a `every` X hours if the N is NOT in hours range" in {
    val every = 24
    val error = HasRangeLimit[Hour].every(every).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(List(every))
  }

  "DayOfTheMonth" should "not be possible to create outside the valid dayOfTheMonths in a day" in {
    forAll(List(-1, 60, 100)) { value =>
      val error = HasRangeLimit[DayOfMonth].exactAt(value).left.value
      error shouldBe ParsingError.InvalidValuesForCronPart(List(value))
    }
  }

  it should "be possible to create if is a valid dayOfTheMonth number" in {
    forAll(1 to 31) { value =>
      val result = HasRangeLimit[DayOfMonth].exactAt(value).value
      result shouldBe DayOfMonth(List(value))
    }
  }

  it should "be possible to create if is a valid list of dayOfTheMonths" in {
    val dayOfTheMonths = Range(1, 31, 2).toList
    val result = HasRangeLimit[DayOfMonth].atTimes(dayOfTheMonths).value
    result shouldBe DayOfMonth(List(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29))
  }

  it should "not be possible to create if is a list containing invalid dayOfTheMonths" in {
    val dayOfTheMonths = List(-1, 10)
    val error = HasRangeLimit[DayOfMonth].atTimes(dayOfTheMonths).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(dayOfTheMonths)
  }

  it should "be possible to create a `every` X dayOfTheMonths if the N is in dayOfTheMonths range" in {
    val every = 6
    val result = HasRangeLimit[DayOfMonth].every(every).value
    result shouldBe DayOfMonth(List(1, 7, 13, 19, 25, 31))
  }

  it should "not be possible to create a `every` X dayOfTheMonths if the N is NOT in dayOfTheMonths range" in {
    val every = 32
    val error = HasRangeLimit[DayOfMonth].every(every).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(List(every))
  }


  "Month" should "not be possible to create outside the valid months in a day" in {
    forAll(List(-1, 60, 100)) { value =>
      val error = HasRangeLimit[Month].exactAt(value).left.value
      error shouldBe ParsingError.InvalidValuesForCronPart(List(value))
    }
  }

  it should "be possible to create if is a valid month number" in {
    forAll(1 to 12) { value =>
      val result = HasRangeLimit[Month].exactAt(value).value
      result shouldBe Month(List(value))
    }
  }

  it should "be possible to create if is a valid list of months" in {
    val months = Range(1, 12, 2).toList
    val result = HasRangeLimit[Month].atTimes(months).value
    result shouldBe Month(List(1, 3, 5, 7, 9, 11))
  }

  it should "not be possible to create if is a list containing invalid months" in {
    val months = List(-1, 10)
    val error = HasRangeLimit[Month].atTimes(months).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(months)
  }

  it should "be possible to create a `every` X months if the N is in months range" in {
    val every = 3
    val result = HasRangeLimit[Month].every(every).value
    result shouldBe Month(List(1, 4, 7, 10))
  }

  it should "not be possible to create a `every` X months if the N is NOT in months range" in {
    val every = 13
    val error = HasRangeLimit[Month].every(every).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(List(every))
  }


  "DayOfTheWeek" should "not be possible to create outside the valid dayOfTheWeeks in a day" in {
    forAll(List(-1, 60, 100)) { value =>
      val error = HasRangeLimit[DayOfWeek].exactAt(value).left.value
      error shouldBe ParsingError.InvalidValuesForCronPart(List(value))
    }
  }

  it should "be possible to create if is a valid dayOfTheWeek number" in {
    forAll(0 to 6) { value =>
      val result = HasRangeLimit[DayOfWeek].exactAt(value).value
      result shouldBe DayOfWeek(List(value))
    }
  }

  it should "be possible to create if is a valid list of dayOfTheWeeks" in {
    val dayOfTheWeeks = Range(0, 6, 2).toList
    val result = HasRangeLimit[DayOfWeek].atTimes(dayOfTheWeeks).value
    result shouldBe DayOfWeek(List(0, 2, 4))
  }

  it should "not be possible to create if is a list containing invalid dayOfTheWeeks" in {
    val dayOfTheWeeks = List(-1, 10)
    val error = HasRangeLimit[DayOfWeek].atTimes(dayOfTheWeeks).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(dayOfTheWeeks)
  }

  it should "be possible to create a `every` X dayOfTheWeeks if the N is in dayOfTheWeeks range" in {
    val every = 3
    val result = HasRangeLimit[DayOfWeek].every(every).value
    result shouldBe DayOfWeek(List(0, 3, 6))
  }

  it should "not be possible to create a `every` X dayOfTheWeeks if the N is NOT in dayOfTheWeeks range" in {
    val every = 13
    val error = HasRangeLimit[DayOfWeek].every(every).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(List(every))
  }
}

