package com.cronparser.parser.models

import com.cronparser.parser.utils.HasRangeLimit

import scala.util.Try

/**
 * Represents a part of a cron expression.
 *
 * A cron expression is used to specify a schedule in Unix-based systems.
 * Each cron expression consists of five or six parts, which define the schedule
 * in terms of minute, hour, day of month, month, day of week, and optionally year.
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

case class Minute private (minutes: List[Int]) extends CronPart {
  require(minutes.forall(Minute.hasRangeLimit.isInRange))
}

object Minute {
  implicit val hasRangeLimit: HasRangeLimit[Minute] = new HasRangeLimit[Minute] {
    override def min: Int = 0
    override def max: Int = 59
    override def fromList(ls: List[Int]): Try[Minute] = Try(Minute.apply(ls))
  }
}

case class Hour(hours: List[Int]) extends CronPart {
  require(hours.forall(Hour.hasRangeLimit.isInRange))
}

object Hour {
  implicit val hasRangeLimit: HasRangeLimit[Hour] = new HasRangeLimit[Hour] {
    override def min: Int = 0
    override def max: Int = 23
    override def fromList(ls: List[Int]): Try[Hour] = Try(Hour.apply(ls))
  }
}

case class DayOfMonth(days: List[Int]) extends CronPart {
  require(days.forall(DayOfMonth.hasRangeLimit.isInRange))
}

object DayOfMonth {
  implicit val hasRangeLimit: HasRangeLimit[DayOfMonth] = new HasRangeLimit[DayOfMonth] {
    override def min: Int = 1
    override def max: Int = 31
    override def fromList(ls: List[Int]): Try[DayOfMonth] = Try(DayOfMonth.apply(ls))
  }
}

case class Month(months: List[Int]) extends CronPart {
  require(months.forall(Month.hasRangeLimit.isInRange))
}

object Month {
  implicit val hasRangeLimit: HasRangeLimit[Month] = new HasRangeLimit[Month] {
    override def min: Int = 1
    override def max: Int = 12
    override def fromList(ls: List[Int]): Try[Month] = Try(Month.apply(ls))
  }
}

case class DayOfWeek(days: List[Int]) extends CronPart {
  require(days.forall(DayOfWeek.hasRangeLimit.isInRange))
}

object DayOfWeek {
  implicit val hasRangeLimit: HasRangeLimit[DayOfWeek] = new HasRangeLimit[DayOfWeek] {
    override def min: Int = 0
    override def max: Int = 6
    override def fromList(ls: List[Int]): Try[DayOfWeek] = Try(DayOfWeek.apply(ls))
  }
}

case class Command(cmd: String) extends CronPart
