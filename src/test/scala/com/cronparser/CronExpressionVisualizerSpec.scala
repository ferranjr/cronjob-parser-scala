package com.cronparser

import com.cronparser.parser.models.{Command, CronExpression, DayOfMonth, DayOfWeek, Hour, Minute, Month}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.LocalDateTime

class CronExpressionVisualizerSpec
  extends AnyFlatSpec
    with Matchers
    with EitherValues {

  behavior of "CronExpressionVisualizer"

  it should "display the next 5 iterations for the given expression when only every 5 minutes" in {
    val cronExpression = CronExpression(
      Minute.hasRangeLimit.every(5).value,
      Hour.hasRangeLimit.every(1).value,
      DayOfMonth.hasRangeLimit.every(1).value,
      Month.hasRangeLimit.every(1).value,
      DayOfWeek.hasRangeLimit.every(1).value,
      Command("foobar")
    )
    val result = cronExpression.nextIterations(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-09-10T00:00:00"),
      LocalDateTime.parse("2024-09-10T00:05:00"),
      LocalDateTime.parse("2024-09-10T00:10:00"),
      LocalDateTime.parse("2024-09-10T00:15:00"),
      LocalDateTime.parse("2024-09-10T00:20:00"),
    )
  }

  it should "display the every hour at minute 10" in {
    val cronExpression = CronExpression(
      Minute.hasRangeLimit.exactAt(10).value,
      Hour.hasRangeLimit.every(1).value,
      DayOfMonth.hasRangeLimit.every(1).value,
      Month.hasRangeLimit.every(1).value,
      DayOfWeek.hasRangeLimit.every(1).value,
      Command("foobar")
    )
    val result = cronExpression.nextIterations(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-09-10T00:10:00"),
      LocalDateTime.parse("2024-09-10T01:10:00"),
      LocalDateTime.parse("2024-09-10T02:10:00"),
      LocalDateTime.parse("2024-09-10T03:10:00"),
      LocalDateTime.parse("2024-09-10T04:10:00"),
    )
  }

  it should "display the once a day at same time" in {
    val cronExpression = CronExpression(
      Minute.hasRangeLimit.exactAt(10).value,
      Hour.hasRangeLimit.exactAt(12).value,
      DayOfMonth.hasRangeLimit.every(1).value,
      Month.hasRangeLimit.every(1).value,
      DayOfWeek.hasRangeLimit.every(1).value,
      Command("foobar")
    )
    val result = cronExpression.nextIterations(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-09-10T12:10:00"),
      LocalDateTime.parse("2024-09-11T12:10:00"),
      LocalDateTime.parse("2024-09-12T12:10:00"),
      LocalDateTime.parse("2024-09-13T12:10:00"),
      LocalDateTime.parse("2024-09-14T12:10:00"),
    )
  }

  it should "display the once a month at same time" in {
    val cronExpression = CronExpression(
      Minute.hasRangeLimit.exactAt(10).value,
      Hour.hasRangeLimit.exactAt(12).value,
      DayOfMonth.hasRangeLimit.exactAt(15).value,
      Month.hasRangeLimit.every(1).value,
      DayOfWeek.hasRangeLimit.every(1).value,
      Command("foobar")
    )
    val result = cronExpression.nextIterations(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-09-15T12:10:00"),
      LocalDateTime.parse("2024-10-15T12:10:00"),
      LocalDateTime.parse("2024-11-15T12:10:00"),
      LocalDateTime.parse("2024-12-15T12:10:00"),
      LocalDateTime.parse("2025-01-15T12:10:00"),
    )
  }

  it should "respect running only MON-FRI day of the week" in {
    val cronExpression = CronExpression(
      Minute.hasRangeLimit.exactAt(10).value,
      Hour.hasRangeLimit.exactAt(12).value,
      DayOfMonth.hasRangeLimit.exactAt(15).value,
      Month.hasRangeLimit.every(1).value,
      DayOfWeek.hasRangeLimit.range(1, 5).value, // Monday to Friday
      Command("foobar")
    )
    val result = cronExpression.nextIterations(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-10-15T12:10:00"), // TUESDAY
      LocalDateTime.parse("2024-11-15T12:10:00"), // FRIDAY
      LocalDateTime.parse("2025-01-15T12:10:00"), // WEDNESDAY
      LocalDateTime.parse("2025-04-15T12:10:00"), // TUESDAY
      LocalDateTime.parse("2025-05-15T12:10:00"), // THURSDAY
    )
  }

  it should "respect end of month not being valid" in {
    val cronExpression = CronExpression(
      Minute.hasRangeLimit.exactAt(10).value,
      Hour.hasRangeLimit.exactAt(12).value,
      DayOfMonth.hasRangeLimit.exactAt(31).value,
      Month.hasRangeLimit.every(1).value,
      DayOfWeek.hasRangeLimit.every(1).value,
      Command("foobar")
    )
    val result = cronExpression.nextIterations(n = 5, LocalDateTime.parse("2024-09-10T00:00:00"))
    result shouldBe List(
      LocalDateTime.parse("2024-10-31T12:10:00"),
      LocalDateTime.parse("2024-12-31T12:10:00"),
      LocalDateTime.parse("2025-01-31T12:10:00"),
      LocalDateTime.parse("2025-03-31T12:10:00"),
      LocalDateTime.parse("2025-05-31T12:10:00"),
    )
  }
}
