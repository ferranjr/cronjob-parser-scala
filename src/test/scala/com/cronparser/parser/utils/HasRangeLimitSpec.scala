package com.cronparser.parser.utils

import com.cronparser.parser.error.ParsingError
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class HasRangeLimitSpec
  extends AnyFlatSpec
    with Matchers
    with EitherValues {

  behavior of "HasRangeLimit"

  private val MIN: Int = 0
  private val MAX: Int = 5

  case class Sample(ls: List[Int]) {
    require(ls.forall(hasRangeLimit.isInRange))
  }

  implicit val hasRangeLimit: HasRangeLimit[Sample] = new HasRangeLimit[Sample] {
    override def min: Int = MIN
    override def max: Int = MAX
    override def fromList(ls: List[Int]): Try[Sample] = Try(Sample.apply(ls))
  }

  "range" should "create range if valid input provided" in {
    val result = HasRangeLimit[Sample].range(MIN, MAX).value
    result shouldBe Sample(List(0, 1, 2, 3, 4, 5))
  }

  it should "fail to create a range if input is out of the valid range" in {
    val error = HasRangeLimit[Sample].range(-10, MAX).left.value
    error shouldBe ParsingError.RangeProvidedIsWrong(-10, MAX)
  }

  "isInRange" should "return a `true` if the input fits within the HasRangeLimit min and max" in {
    HasRangeLimit[Sample].isInRange(2) shouldBe true
  }

  it should "return a `false` if the input fits out of the HasRangeLimit min and max" in {
    HasRangeLimit[Sample].isInRange(20) shouldBe false
  }

  "atTimes" should "create the Unit with the list provided if all the values are within the limits" in {
    val result = HasRangeLimit[Sample].atTimes(List(MIN, 3, MAX)).value
    result shouldBe Sample(List(MIN, 3, MAX))
  }

  it should "fail to create the Unit if any of the values is outside the valid limits" in {
    val error = HasRangeLimit[Sample].atTimes(List(MIN, 2*MAX)).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(List(MIN, 2*MAX))
  }

  "exactAt" should "create the unit with one exact value if this value is within the limits" in {
    val result = HasRangeLimit[Sample].exactAt(2).value
    result shouldBe Sample(List(2))
  }

  it should "fail to create the unit within one exact value if this value is outside the valid limits" in {
    val error = HasRangeLimit[Sample].exactAt(2*MAX).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(List(2*MAX))
  }

  "every" should "create the unit with a range of values given the `every` step size if it fits in the limits" in {
    val result = HasRangeLimit[Sample].every(2).value
    result shouldBe Sample(List(0, 2, 4))
  }

  it should "fail to create the unit with the range of values given the `every` step size bigger than the range itself" in {
    val error = HasRangeLimit[Sample].every(2*MAX).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(List(2*MAX))
  }

}