package com.cronparser.lexer.models

/**
 * Represents a lexical token in a programming language
 * or any other domain where tokenization is required.
 *
 * This sealed trait serves as a base for different kinds
 * of tokens that can be recognized. Subclasses of Token
 * will represent specific types of tokens such as identifiers,
 * keywords, literals, operators, etc.
 */
sealed trait Token {
  def asText: String =
    this match {
      case Token.Asterisk => "*"
      case Token.Comma => ","
      case Token.Whitespace => " "
      case Token.Hyphen => "-"
      case Token.Number(value) => s"$value"
      case Token.Slash => "/"
      case Token.OtherSymbol(value) => value.toString
      case Token.Text(value) => value
    }
}

object Token {
  case object Asterisk extends Token

  case object Comma extends Token

  case object Whitespace extends Token

  case object Hyphen extends Token

  case class Number(value: Int) extends Token

  case object Slash extends Token

  case class OtherSymbol(char: Char) extends Token

  case class Text(value: String) extends Token
}