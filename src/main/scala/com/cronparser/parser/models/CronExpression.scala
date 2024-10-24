package com.cronparser.parser.models

import java.time.{LocalDateTime, ZoneId}
import java.time.format.{DateTimeFormatter, FormatStyle}

/**
 * Represents a Cron Expression.
 *
 * @param minute        Minute field of the cron expression
 * @param hour          Hour field of the cron expression
 * @param dayOfTheMonth Day of the month field of the cron expression
 * @param month         Month field of the cron expression
 * @param dayOfTheWeek  Day of the week field of the cron expression
 * @param command       Command to be executed by the cron expression
 */
case class CronExpression(
  minute: Minute,
  hour: Hour,
  dayOfTheMonth: DayOfMonth,
  month: Month,
  dayOfTheWeek: DayOfWeek,
  command: Command
) {
  override def toString: String = {
    s"""
       |$minute
       |$hour
       |$dayOfTheMonth
       |$month
       |$dayOfTheWeek
       |$command
       |
       |${next5IterationsAsFullString().mkString("\n")}
       |
       |""".stripMargin
  }

  private def nextIteration(seed: LocalDateTime): LocalDateTime = {
    val seedMinute  = seed.getMinute
    val nMinute     = this.minute.next(seedMinute)

    val seedHour    = if(nMinute < seedMinute) seed.getHour + 1 else seed.getHour
    val nHour       = this.hour.next(seedHour)

    val plusDay     = if(nHour < seedHour) 1 else 0
    val seedDayOfMonth = seed.getDayOfMonth + plusDay
    val nDayOfMonth = this.dayOfTheMonth.next(seedDayOfMonth)

    val seedMonth   = if(nDayOfMonth < seedDayOfMonth) seed.getMonth.getValue + 1 else seed.getMonth.getValue
    val nMonth      = this.month.next(seedMonth)

    val year        = if(nMonth < seedMonth) seed.getYear + 1 else seed.getYear

    LocalDateTime.of(
      year,
      nMonth,
      nDayOfMonth,
      nHour,
      nMinute,
      0
    )
  }

  private def convertDayOfTheWeekCron(dayOfTheWeek: java.time.DayOfWeek): Int = {
    dayOfTheWeek match {
      case java.time.DayOfWeek.SUNDAY     => 0
      case other                          => other.getValue
    }
  }

  /**
   * Allows to generate the next instances that the cronJob would run starting from the seed
   *
   * @param n     number of instances we want to generate
   * @param seed  the starting datetime to generate the instances
   * @return      list of the N instances requested
   */
  def nextIterations(
    n: Int,
    seed: LocalDateTime
  ): List[LocalDateTime] = {
    LazyList.from(0)
      .scanLeft(nextIteration(seed)) { case (seed, _) =>
        nextIteration(seed.plusMinutes(1))
      }
      .filter { candidate =>
        val value = convertDayOfTheWeekCron(candidate.getDayOfWeek)
        this.dayOfTheWeek.days.contains(value)
      }
      .take(n)
      .toList
  }

  private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)

  private def next5IterationsAsFullString(): List[String] = {
    nextIterations(5, seed = LocalDateTime.now())
      .map(_.atZone(ZoneId.systemDefault()).format(dateTimeFormatter))
  }
}