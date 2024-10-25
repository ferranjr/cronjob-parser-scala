package com.cronparser.parser.utils

import com.cronparser.parser.error.ParsingError
import scala.util.Try

/**
 * Trait representing an entity with a range limit.
 * Given the range limit provides a validation to check if within range
 * and a set of builders to create the list of values within the cronPart
 *
 * @tparam A The type of the entity
 */
trait HasRangeLimit[A] {
  def min: Int
  def max: Int

  def fromList(ls: List[Int]): Try[A]

  def labelsMap: Map[String, Int] = Map.empty

  def wordValue(word: String): Either[ParsingError, Int] =
    labelsMap.get(word).fold[Either[ParsingError, Int]](
      Left(ParsingError.InvalidLabel(word))
    )(Right.apply)

  def isInRange(n: Int): Boolean =
    n >= min && n <= max

  def exactAt(n: Int): Either[ParsingError, A] =
    fromList(List(n))
      .fold[Either[ParsingError, A]](
        _ => Left(ParsingError.InvalidValuesForCronPart(List(n))),
        Right(_)
      )

  def atTimes(ls: List[Int]): Either[ParsingError, A] =
    fromList(ls)
      .fold[Either[ParsingError, A]](
        _ => Left(ParsingError.InvalidValuesForCronPart(ls)),
        Right(_)
      )

  def every(n: Int): Either[ParsingError, A] = {
    if (isInRange(n)) {
      atTimes(Range.inclusive(min, max, n).toList)
    } else {
      Left(ParsingError.InvalidValuesForCronPart(List(n)))
    }
  }

  def range(min: Int, max: Int): Either[ParsingError, A] = {
    if (min < max && isInRange(min) && isInRange(max)) {
      atTimes(Range.inclusive(min, max).toList)
    } else {
      Left(ParsingError.RangeProvidedIsWrong(min, max))
    }
  }
}

object HasRangeLimit {
  def apply[A](implicit ev: HasRangeLimit[A]): HasRangeLimit[A] = ev
}
