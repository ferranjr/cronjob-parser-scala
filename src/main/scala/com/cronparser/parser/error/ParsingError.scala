package com.cronparser.parser.error

import com.cronparser.lexer.models.Token

/**
 * Represents a ParsingError
 */
sealed trait ParsingError extends Exception

object ParsingError {
  case class UnexpectedToken(unexpectedToken: Token) extends ParsingError
  case object UnexpectedEndOfTokens extends ParsingError
  case object UnexpectedEndOfList extends ParsingError
  case class InvalidValuesForCronPart(values: List[Int]) extends ParsingError
  case class RangeProvidedIsWrong(min: Int, max: Int) extends ParsingError
}