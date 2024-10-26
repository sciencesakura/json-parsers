// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import spock.lang.Specification

class ParserSpec extends Specification {

  def 'parse null'() {
    given:
    def tokens = [new Token.Null(1, 1)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == null
  }

  def 'parse true'() {
    given:
    def tokens = [new Token.Bool(1, 1, true)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == true
  }

  def 'parse false'() {
    given:
    def tokens = [new Token.Bool(1, 1, false)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == false
  }

  def 'parse string'() {
    given:
    def tokens = [new Token.String(1, 1, 'Hello')]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == 'Hello'
  }

  def 'parse number'() {
    given:
    def tokens = [new Token.Number(1, 1, 42)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == 42
  }

  def 'parse empty array'() {
    given:
    def tokens = [new Token.LeftBracket(1, 1), new Token.RightBracket(1, 2)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == []
  }

  def 'parse array having one element'() {
    given:
    def tokens = [
        new Token.LeftBracket(1, 1),
        new Token.Number(1, 2, 42),
        new Token.RightBracket(1, 3),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == [42]
  }

  def 'parse array having two elements'() {
    given:
    def tokens = [
        new Token.LeftBracket(1, 1),
        new Token.String(1, 2, 'Hello'),
        new Token.Comma(1, 3),
        new Token.Number(1, 4, 42),
        new Token.RightBracket(1, 5),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == ['Hello', 42]
  }

  def "throw exception for unclosed array: '['"() {
    given:
    def tokens = [new Token.LeftBracket(1, 1)]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == 'Unexpected end of input'
  }

  def "throw exception for unclosed array: '[42'"() {
    given:
    // [42
    def tokens = [new Token.LeftBracket(1, 1), new Token.Number(1, 2, 42)]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == 'Unexpected end of input'
  }

  def "throw exception for unclosed array: '[42,'"() {
    given:
    def tokens = [
        new Token.LeftBracket(1, 1),
        new Token.Number(1, 2, 42),
        new Token.Comma(1, 3),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == 'Unexpected end of input'
  }

  def "throw exception for invalid sequence of tokens: '[,]'"() {
    given:
    def tokens = [
        new Token.LeftBracket(1, 1),
        new Token.Comma(1, 2),
        new Token.RightBracket(1, 3),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == "Unexpected token 'Comma' at 1:2"
    assert e.line == 1
    assert e.column == 2
  }

  def "throw exception for invalid sequence of tokens: '[42,,]'"() {
    given:
    // [42,,]
    def tokens = [
        new Token.LeftBracket(1, 1),
        new Token.Number(1, 2, 42),
        new Token.Comma(1, 3),
        new Token.Comma(1, 4),
        new Token.RightBracket(1, 5),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == "Unexpected token 'Comma' at 1:4"
    assert e.line == 1
    assert e.column == 4
  }

  def "throw exception for invalid sequence of tokens: '[42 42]'"() {
    given:
    // [42 42]
    def tokens = [
        new Token.LeftBracket(1, 1),
        new Token.Number(1, 2, 42),
        new Token.Number(1, 3, 42),
        new Token.RightBracket(1, 4),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == "Unexpected token 'Number' at 1:3"
    assert e.line == 1
    assert e.column == 3
  }

  def 'parse empty object'() {
    given:
    def tokens = [new Token.LeftCurly(1, 1), new Token.RightCurly(1, 2)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == [:]
  }

  def 'parse object having one pair'() {
    given:
    def tokens = [
        new Token.LeftCurly(1, 1),
        new Token.String(1, 2, 'foo'),
        new Token.Colon(1, 3),
        new Token.Number(1, 4, 42),
        new Token.RightCurly(1, 5),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == [foo: 42]
  }

  def 'parse object having two pairs'() {
    given:
    def tokens = [
        new Token.LeftCurly(1, 1),
        new Token.String(1, 2, 'foo'),
        new Token.Colon(1, 3),
        new Token.String(1, 4, 'Hello'),
        new Token.Comma(1, 5),
        new Token.String(1, 6, 'bar'),
        new Token.Colon(1, 7),
        new Token.Number(1, 8, 42),
        new Token.RightCurly(1, 9),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == [foo: 'Hello', bar: 42]
  }

  def "throw exception for unclosed object: '{'"() {
    given:
    def tokens = [new Token.LeftCurly(1, 1)]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == 'Unexpected end of input'
  }

  def "throw exception for unclosed object: '{\"foo\"'"() {
    given:
    def tokens = [new Token.LeftCurly(1, 1), new Token.String(1, 2, 'foo')]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == 'Unexpected end of input'
  }

  def "throw exception for unclosed object: '{\"foo\":'"() {
    given:
    def tokens = [
        new Token.LeftCurly(1, 1),
        new Token.String(1, 2, 'foo'),
        new Token.Colon(1, 3),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == 'Unexpected end of input'
  }

  def "throw exception for unclosed object: '{\"foo\": 42'"() {
    given:
    def tokens = [
        new Token.LeftCurly(1, 1),
        new Token.String(1, 2, 'foo'),
        new Token.Colon(1, 3),
        new Token.Number(1, 4, 42),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == 'Unexpected end of input'
  }

  def "throw exception for unclosed object: '{\"foo\": 42,'"() {
    given:
    // {"foo": 42,
    def tokens = [
        new Token.LeftCurly(1, 1),
        new Token.String(1, 2, 'foo'),
        new Token.Colon(1, 3),
        new Token.Number(1, 4, 42),
        new Token.Comma(1, 5),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == 'Unexpected end of input'
  }

  def "throw exception for invalid sequence of tokens: '{42}'"() {
    given:
    def tokens = [
        new Token.LeftCurly(1, 1),
        new Token.Number(1, 2, 42),
        new Token.RightCurly(1, 3),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == "Unexpected token 'Number' at 1:2"
    assert e.line == 1
    assert e.column == 2
  }

  def "throw exception for invalid sequence of tokens: '{\"foo\" 42}'"() {
    given:
    // {"foo" 42}
    def tokens = [
        new Token.LeftCurly(1, 1),
        new Token.String(1, 2, 'foo'),
        new Token.Number(1, 3, 42),
        new Token.RightCurly(1, 4),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == "Unexpected token 'Number' at 1:3"
    assert e.line == 1
    assert e.column == 3
  }

  def "throw exception for invalid sequence of tokens: '{\"foo\":}'"() {
    given:
    def tokens = [
        new Token.LeftCurly(1, 1),
        new Token.String(1, 2, 'foo'),
        new Token.Colon(1, 3),
        new Token.RightCurly(1, 4),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == "Unexpected token 'RightCurly' at 1:4"
    assert e.line == 1
    assert e.column == 4
  }

  def "throw exception for invalid sequence of tokens: '{\"foo\": 42 \"bar\"}'"() {
    given:
    def tokens = [
        new Token.LeftCurly(1, 1),
        new Token.String(1, 2, 'foo'),
        new Token.Colon(1, 3),
        new Token.Number(1, 4, 42),
        new Token.String(1, 5, 'bar'),
        new Token.RightCurly(1, 6),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == "Unexpected token 'String' at 1:5"
    assert e.line == 1
    assert e.column == 5
  }

  def "throw exception for invalid sequence of tokens: '{\"foo\": 42,}'"() {
    given:
    def tokens = [
        new Token.LeftCurly(1, 1),
        new Token.String(1, 2, 'foo'),
        new Token.Colon(1, 3),
        new Token.Number(1, 4, 42),
        new Token.Comma(1, 5),
        new Token.RightCurly(1, 6),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.message == "Unexpected token 'RightCurly' at 1:6"
    assert e.line == 1
    assert e.column == 6
  }
}
