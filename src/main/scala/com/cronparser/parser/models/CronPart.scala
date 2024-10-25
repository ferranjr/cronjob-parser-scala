package com.cronparser.parser.models

import com.cronparser.parser.utils.HasRangeLimit

import scala.util.Try

/**
 * Represents a part of a cron expression.
 *
 * A cron expression is used to specify a schedule in Unix-based systems.
 * Each cron expression consists of five or six parts, which define the schedule
 * in terms of minute, hour, day of month, month, day of week.
 */
sealed trait CronPart {
  private val columnSize: Int = 14

  private def labelStr(label: String, width: Int): String = {
    val whiteSpaces = " " * (width - label.length)
    s"$label$whiteSpaces"
  }

  override def toString: String = {
    this match {
      case Minute(minutes) => s"${labelStr("minute", columnSize)}${minutes.mkString(" ")}"
      case Hour(hours) => s"${labelStr("hour", columnSize)}${hours.mkString(" ")}"
      case DayOfMonth(days) => s"${labelStr("day of month", columnSize)}${days.mkString(" ")}"
      case Month(months) => s"${labelStr("month", columnSize)}${months.mkString(" ")}"
      case DayOfWeek(days) => s"${labelStr("day of week", columnSize)}${days.mkString(" ")}"
      case Command(cmd) => s"${labelStr("command", columnSize)}$cmd"
    }
  }
}

case class Minute private (instances: List[Int]) extends CronPart {
  require(instances.forall(Minute.hasRangeLimit.isInRange))

  def next(current: Int): Int =
    instances.dropWhile(current > _).headOption.getOrElse(instances.head)
}

object Minute {
  implicit val hasRangeLimit: HasRangeLimit[Minute] = new HasRangeLimit[Minute] {
    override def min: Int = 0
    override def max: Int = 59
    override def fromList(ls: List[Int]): Try[Minute] = Try(Minute.apply(ls))
  }
}

case class Hour(instances: List[Int]) extends CronPart {
  require(instances.forall(Hour.hasRangeLimit.isInRange))

  def next(current: Int): Int =
    instances.dropWhile(current > _).headOption.getOrElse(instances.head)
}

object Hour {
  implicit val hasRangeLimit: HasRangeLimit[Hour] = new HasRangeLimit[Hour] {
    override def min: Int = 0
    override def max: Int = 23
    override def fromList(ls: List[Int]): Try[Hour] = Try(Hour.apply(ls))
  }
}

case class DayOfMonth(instances: List[Int]) extends CronPart {
  require(instances.forall(DayOfMonth.hasRangeLimit.isInRange))

  def next(current: Int): Int =
    instances.dropWhile(current > _).headOption.getOrElse(instances.head)
}

object DayOfMonth {
  implicit val hasRangeLimit: HasRangeLimit[DayOfMonth] = new HasRangeLimit[DayOfMonth] {
    override def min: Int = 1
    override def max: Int = 31
    override def fromList(ls: List[Int]): Try[DayOfMonth] = Try(DayOfMonth.apply(ls))
  }
}

case class Month(instances: List[Int]) extends CronPart {
  require(instances.forall(Month.hasRangeLimit.isInRange))

  def next(current: Int): Int =
    instances.dropWhile(current > _).headOption.getOrElse(instances.head)
}

object Month {
  implicit val hasRangeLimit: HasRangeLimit[Month] = new HasRangeLimit[Month] {
    override def min: Int = 1
    override def max: Int = 12

    override def labelsMap: Map[String, Int] = Map(
      "JAN" -> 1,
      "FEB" -> 2,
      "MAR" -> 3,
      "APR" -> 4,
      "MAY" -> 5,
      "JUN" -> 6,
      "JUL" -> 7,
      "AUG" -> 8,
      "SEP" -> 9,
      "OCT" -> 10,
      "NOV" -> 11,
      "DEC" -> 12,
    )
    override def fromList(ls: List[Int]): Try[Month] = Try(Month.apply(ls))
  }
}

case class DayOfWeek(instances: List[Int]) extends CronPart {
  require(instances.forall(DayOfWeek.hasRangeLimit.isInRange))
  def next(current: Int): Int =
    instances.dropWhile(current >= _).headOption.getOrElse(instances.head)
}

object DayOfWeek {
  implicit val hasRangeLimit: HasRangeLimit[DayOfWeek] = new HasRangeLimit[DayOfWeek] {
    override def min: Int = 0 // SUN
    override def max: Int = 6 // SAT

    override def labelsMap: Map[String, Int] = Map(
      "SUN" -> 0,
      "MON" -> 1,
      "TUE" -> 2,
      "WED" -> 3,
      "THU" -> 4,
      "FRI" -> 5,
      "SAT" -> 6,
    )
    override def fromList(ls: List[Int]): Try[DayOfWeek] = Try(DayOfWeek.apply(ls))
  }
}

case class Command(cmd: String) extends CronPart
