package com.cronparser.parser.models

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
       |${minute}
       |${hour}
       |${dayOfTheMonth}
       |${month}
       |${dayOfTheWeek}
       |${command}
       |""".stripMargin
  }
}