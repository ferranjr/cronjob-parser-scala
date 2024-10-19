package com.cronparser.lexer

import com.cronparser.lexer.models.Token
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class TokenizerSpec
  extends AnyFlatSpec
    with Matchers {

  behavior of "tokenize"

  it should "return the number token when number is provided" in {
    val input = "1982"
    val result = Tokenizer.tokenize(input)
    result shouldBe List(Token.Number(1982))
  }

  it should "return the number token when a string is provided" in {
    val input = "foobar"
    val result = Tokenizer.tokenize(input)
    result shouldBe List(Token.Text("foobar"))
  }

  it should "return OtherSymbol for unrecognised chars" in {
    val input = "!"
    val result = Tokenizer.tokenize(input)

    result shouldBe List(Token.OtherSymbol('!'))
  }

  it should "return the expected tokens" in {
    val input = "1 / * foobar 1,2,3 > foo"
    val result = Tokenizer.tokenize(input)
    val expected = List(
      Token.Number(1),
      Token.Whitespace,
      Token.Slash,
      Token.Whitespace,
      Token.Asterisk,
      Token.Whitespace,
      Token.Text("foobar"),
      Token.Whitespace,
      Token.Number(1),
      Token.Comma,
      Token.Number(2),
      Token.Comma,
      Token.Number(3),
      Token.Whitespace,
      Token.OtherSymbol('>'),
      Token.Whitespace,
      Token.Text("foo"),
    )

    result shouldBe expected
  }
}
