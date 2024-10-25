package com.cronparser.lexer

import com.cronparser.lexer.models.Token._
import com.cronparser.lexer.models.Token

import scala.annotation.tailrec

/**
 * Tokenizer object is responsible for processing and converting a raw string input into a list of tokens.
 */
object Tokenizer {

  @tailrec
  private def tokenizeRec(
    chars: List[Char],
    currentToken: Option[Token],
    acc: List[Token]
  ): List[Token] = {
    chars match {
      case Nil => currentToken.fold(acc)(t => (t :: acc).reverse)
      case c :: tail =>
        c match {
          case '*' => commitToken(tail, Asterisk, currentToken, acc)
          case ',' => commitToken(tail, Comma, currentToken, acc)
          case ' ' | '\t' | '\n' => commitToken(tail, Whitespace, currentToken, acc)
          case '-' => commitToken(tail, Hyphen, currentToken, acc)
          case '/' => commitToken(tail, Slash, currentToken, acc)
          case d if d.isDigit =>
            currentToken match {
              case Some(Number(num)) => tokenizeRec(tail, Some(Number(num * 10 + d.asDigit)), acc)
              case Some(_) => commitToken(tail, Number(d.asDigit), currentToken, acc)
              case None => tokenizeRec(tail, Some(Number(d.asDigit)), acc)
            }
          case s if s.isLetter =>
            currentToken match {
              case Some(Text(prev)) => tokenizeRec(tail, Some(Text(s"$prev$s")), acc)
              case Some(_) => commitToken(tail, Text(s.toString), currentToken, acc)
              case None => tokenizeRec(tail, Some(Text(s.toString)), acc)
            }
          case s => commitToken(tail, OtherSymbol(s), currentToken, acc)
        }
    }
  }

  private def commitToken(
    chars: List[Char],
    newToken: Token,
    currentToken: Option[Token],
    acc: List[Token]
  ): List[Token] = {
    val newAcc = currentToken.fold(acc)(_ :: acc)
    tokenizeRec(chars, Some(newToken), newAcc)
  }

  /**
   * Converts an input string into a List of Token that can then be parsed
   *
   * @param input The Row String to be tokenised
   * @return      List of Token
   */
  def tokenize(input: String): List[Token] =
    tokenizeRec(input.toList, None, Nil)
}
