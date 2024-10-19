package com.cronparser.parser

import com.cronparser.lexer.Tokenizer
import com.cronparser.lexer.models.Token
import com.cronparser.parser.error.ParsingError
import com.cronparser.parser.models._
import com.cronparser.parser.utils.HasRangeLimit

import scala.annotation.tailrec

object Parser {

  /**
   * Method specific to parse a comma separated integers that will form the values in the Unit A
   *
   * @param tokens List of token ready to be parsed
   * @param acc    List of accumulated integers that will form the Unit
   * @param ev     Evidence of the instance of HasRangeLimit for A
   * @tparam A The Type (Unit) we are targeting on the parse function
   * @return Either a parsingError or a tuple containing the unit completed and the rest of tokens to process
   */
  @tailrec
  private def parseList[A](
    tokens: List[Token],
    acc: List[Int],
  )(implicit ev: HasRangeLimit[A]): Either[ParsingError, (A, List[Token])] = {
    tokens match {
      case Nil => Left(ParsingError.UnexpectedEndOfList)
      case Token.Number(number) :: Token.Whitespace :: tail =>
        ev.atTimes((number :: acc).reverse).map { value => (value, tail) }
      case Token.Number(number) :: Token.Comma :: tail =>
        parseList[A](tail, number :: acc)
      case unexpected :: _ =>
        Left(ParsingError.UnexpectedToken(unexpected))
    }
  }

  /**
   * Starts parsing the expression for the value type A which implements HasRangeLimit that allows to easily
   * build the list of instances when the cron should trigger. ie. List of the minutes that need to trigger
   *
   * @param tokens List of token ready to be parsed
   * @param ev     Evidence of the instance of HasRangeLimit for A
   * @tparam A The Type (Unit) we are targeting on the parse function
   * @return Either a parsingError or a tuple containing the unit completed and the rest of tokens to process
   */
  private def parseValue[A](tokens: List[Token])(implicit ev: HasRangeLimit[A]): Either[ParsingError, (A, List[Token])] =
    tokens match {
      case Nil => Left(ParsingError.UnexpectedEndOfTokens)
      case Token.Number(number) :: Token.Whitespace :: tail =>
        ev.exactAt(number).map { value => (value, tail) }
      case Token.Asterisk :: Token.Slash :: Token.Number(number) :: Token.Whitespace :: tail =>
        ev.every(number).map { value => (value, tail) }
      case Token.Asterisk :: Token.Whitespace :: tail =>
        ev.every(1).map { value => (value, tail) }
      case Token.Number(number) :: Token.Comma :: tail =>
        parseList[A](tail, List(number))
      case Token.Number(number) :: Token.Hyphen :: Token.Number(number2) :: Token.Whitespace :: tail =>
        ev.range(number, number2).map { value => (value, tail) }
      case unexpected :: _ =>
        Left(ParsingError.UnexpectedToken(unexpected))
    }

  /**
   * Method to start the parsing of the Command
   *
   * @param tokens List of token ready to be parsed
   * @return Either a ParsingError to the actual Command
   */
  private def parseCommand(tokens: List[Token]): Either[ParsingError, Command] = {
    tokens match {
      case Nil => Left(ParsingError.UnexpectedEndOfTokens)
      case commandParts => parseCommandParts(commandParts, "")
    }
  }

  /**
   * Recursive parsing method to keep processing teh last tokens that will form the full command
   *
   * @param tokens List of token ready to be parsed
   * @param acc    Accumulated String from the already consumed from the Tokens
   * @return Either a ParsingError to the actual Command
   */
  @tailrec
  private def parseCommandParts(tokens: List[Token], acc: String): Either[ParsingError, Command] = {
    tokens match {
      case Nil => Right(Command(acc))
      case token :: tail => parseCommandParts(tail, acc = s"$acc${token.asText}")
    }
  }

  /**
   * Given a list of Tokens returns either a ParsingError or a well-formed CronExpression.
   *
   * @param tokens List of token ready to be parsed
   * @return Either a ParsingError to the resulting CronExpression
   */
  def parserTokens(tokens: List[Token]): Either[ParsingError, CronExpression] = {
    for {
      parsedMinute <- parseValue[Minute](tokens)
      parsedHour <- parseValue[Hour](parsedMinute._2)
      parsedDayOfTheMonth <- parseValue[DayOfMonth](parsedHour._2)
      parsedMonth <- parseValue[Month](parsedDayOfTheMonth._2)
      parsedDayOfTheWeek <- parseValue[DayOfWeek](parsedMonth._2)
      command <- parseCommand(parsedDayOfTheWeek._2)
    } yield CronExpression(
      parsedMinute._1,
      parsedHour._1,
      parsedDayOfTheMonth._1,
      parsedMonth._1,
      parsedDayOfTheWeek._1,
      command
    )
  }

  /**
   * Parses the input string, it does it by first running the lexer and generating the tokens
   * then running the parserTokens
   *
   * @param input Raw input string to be parsed
   * @return Either a ParsingError to the resulting CronExpression
   */
  def parse(input: String): Either[ParsingError, CronExpression] =
    parserTokens(Tokenizer.tokenize(input))
}
