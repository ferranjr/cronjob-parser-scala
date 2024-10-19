package com.cronparser.parser

import com.cronparser.lexer.models.Token
import com.cronparser.parser.error.ParsingError
import com.cronparser.parser.models.{Command, CronExpression, DayOfMonth, DayOfWeek, Hour, Minute, Month}
import com.cronparser.parser.utils.HasRangeLimit
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ParserSpec
  extends AnyFlatSpec
    with Matchers
    with EitherValues {

  behavior of "Parser"

  "parserTokens" should "fail if unexpected token at start" in {
    val tokens = List(Token.Whitespace)
    val result = Parser.parserTokens(tokens)

    result.isLeft shouldBe true
    result.left.value shouldBe ParsingError.UnexpectedToken(Token.Whitespace)
  }

  it should "fail if we do not find all minimum cronParts" in {
    val tokens = List(Token.Asterisk, Token.Whitespace)
    val result = Parser.parserTokens(tokens)

    result.isLeft shouldBe true
    result.left.value shouldBe ParsingError.UnexpectedEndOfTokens
  }

  it should "fail if unexpected token when we are expecting a different one" in {
    val tokens = List(Token.Asterisk, Token.Whitespace, Token.Whitespace)
    val result = Parser.parserTokens(tokens)

    result.isLeft shouldBe true
    result.left.value shouldBe ParsingError.UnexpectedToken(Token.Whitespace)
  }

  it should "fail if trying to use a value out of range" in {
    val tokens = List(
      Token.Number(1000), Token.Whitespace,
      Token.Asterisk, Token.Whitespace,
      Token.Asterisk, Token.Whitespace,
      Token.Asterisk, Token.Whitespace,
      Token.Asterisk, Token.Whitespace,
      Token.Slash, Token.Text("bin"), Token.Slash, Token.Text("cronJob"), Token.Whitespace, Token.Text("foobar")
    )

    val error = Parser.parserTokens(tokens).left.value
    error shouldBe ParsingError.InvalidValuesForCronPart(List(1000))
  }

  it should "succeed when all tokens in place and valid" in {
    val tokens = List(
      Token.Asterisk, Token.Whitespace,
      Token.Asterisk, Token.Whitespace,
      Token.Asterisk, Token.Whitespace,
      Token.Asterisk, Token.Whitespace,
      Token.Asterisk, Token.Whitespace,
      Token.Slash, Token.Text("bin"), Token.Slash, Token.Text("cronJob"),
      Token.Whitespace, Token.Text("foobar"), Token.Whitespace, Token.OtherSymbol('>')
    )

    val result = Parser.parserTokens(tokens).value
    result shouldBe CronExpression(
      HasRangeLimit[Minute].every(1).value,
      HasRangeLimit[Hour].every(1).value,
      HasRangeLimit[DayOfMonth].every(1).value,
      HasRangeLimit[Month].every(1).value,
      HasRangeLimit[DayOfWeek].every(1).value,
      Command("/bin/cronJob foobar >")
    )
  }

  "parse" should "given an input string should tokenise and parse the string into a CronExpression" in {
    val input = "* * * * * /bin/cronJob foobar >"
    val result = Parser.parse(input).value
    result shouldBe CronExpression(
      HasRangeLimit[Minute].every(1).value,
      HasRangeLimit[Hour].every(1).value,
      HasRangeLimit[DayOfMonth].every(1).value,
      HasRangeLimit[Month].every(1).value,
      HasRangeLimit[DayOfWeek].every(1).value,
      Command("/bin/cronJob foobar >")
    )
  }

  it should "work for our sample example" in {
    val input = "*/15 0 1,15 * 1-5 /usr/bin/find"
    val result = Parser.parse(input).value
    result shouldBe CronExpression(
      HasRangeLimit[Minute].atTimes(List(0, 15, 30, 45)).value,
      HasRangeLimit[Hour].exactAt(0).value,
      HasRangeLimit[DayOfMonth].atTimes(List(1, 15)).value,
      HasRangeLimit[Month].every(1).value,
      HasRangeLimit[DayOfWeek].atTimes(List(1, 2, 3, 4, 5)).value,
      Command("/usr/bin/find")
    )
  }
}
