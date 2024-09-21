// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import spock.lang.Specification

class ParserSpec extends Specification {

  def 'parse null'() {
    given:
    def tokens = [new Token.Null(0, 0)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == null
  }

  def 'parse true'() {
    given:
    def tokens = [new Token.Bool(0, 0, true)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == true
  }

  def 'parse false'() {
    given:
    def tokens = [new Token.Bool(0, 0, false)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == false
  }

  def 'parse string'() {
    given:
    def tokens = [new Token.String(0, 0, 'Hello')]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == 'Hello'
  }

  def 'parse number'() {
    given:
    def tokens = [new Token.Number(0, 0, 42)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == 42
  }

  def 'parse empty array'() {
    given:
    def tokens = [new Token.LeftBracket(0, 0), new Token.RightBracket(0, 0)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == []
  }

  def 'parse array having one element'() {
    given:
    def tokens = [
        new Token.LeftBracket(0, 0),
        new Token.Number(0, 0, 42),
        new Token.RightBracket(0, 0),
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
        new Token.LeftBracket(0, 0),
        new Token.String(0, 0, 'Hello'),
        new Token.Comma(0, 0),
        new Token.Number(0, 0, 42),
        new Token.RightBracket(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == ['Hello', 42]
  }

  def 'throw exception when array ends unexpectedly 1'() {
    given:
    // [
    def tokens = [new Token.LeftBracket(0, 0)]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
  }

  def 'throw exception when array ends unexpectedly 2'() {
    given:
    // [42
    def tokens = [new Token.LeftBracket(0, 0), new Token.Number(0, 0, 42)]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
  }

  def 'throw exception when array ends unexpectedly 3'() {
    given:
    // [42,
    def tokens = [
        new Token.LeftBracket(0, 0),
        new Token.Number(0, 0, 42),
        new Token.Comma(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
  }

  def 'throw exception when unexpected token is found in array 1'() {
    given:
    // [,]
    def tokens = [
        new Token.LeftBracket(0, 0),
        new Token.Comma(0, 0),
        new Token.RightBracket(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_TOKEN
  }

  def 'throw exception when unexpected token is found in array 2'() {
    given:
    // [42,,]
    def tokens = [
        new Token.LeftBracket(0, 0),
        new Token.Number(0, 0, 42),
        new Token.Comma(0, 0),
        new Token.Comma(0, 0),
        new Token.RightBracket(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_TOKEN
  }

  def 'throw exception when unexpected token is found in array 3'() {
    given:
    // [42 42]
    def tokens = [
        new Token.LeftBracket(0, 0),
        new Token.Number(0, 0, 42),
        new Token.Number(0, 0, 42),
        new Token.RightBracket(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_TOKEN
  }

  def 'parse empty object'() {
    given:
    def tokens = [new Token.LeftCurly(0, 0), new Token.RightCurly(0, 0)]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == [:]
  }

  def 'parse object having one pair'() {
    given:
    def tokens = [
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, 'foo'),
        new Token.Colon(0, 0),
        new Token.Number(0, 0, 42),
        new Token.RightCurly(0, 0),
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
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, 'foo'),
        new Token.Colon(0, 0),
        new Token.String(0, 0, 'Hello'),
        new Token.Comma(0, 0),
        new Token.String(0, 0, 'bar'),
        new Token.Colon(0, 0),
        new Token.Number(0, 0, 42),
        new Token.RightCurly(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    def actual = parser.parse()

    then:
    assert actual == [foo: 'Hello', bar: 42]
  }

  def 'throw exception when object ends unexpectedly 1'() {
    given:
    // {
    def tokens = [new Token.LeftCurly(0, 0)]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
  }

  def 'throw exception when object ends unexpectedly 2'() {
    given:
    // {"foo"
    def tokens = [new Token.LeftCurly(0, 0), new Token.String(0, 0, 'foo')]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
  }

  def 'throw exception when object ends unexpectedly 3'() {
    given:
    // {"foo":
    def tokens = [
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, 'foo'),
        new Token.Colon(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
  }

  def 'throw exception when object ends unexpectedly 4'() {
    given:
    // {"foo": 42
    def tokens = [
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, 'foo'),
        new Token.Colon(0, 0),
        new Token.Number(0, 0, 42),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
  }

  def 'throw exception when object ends unexpectedly 5'() {
    given:
    // {"foo": 42,
    def tokens = [
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, 'foo'),
        new Token.Colon(0, 0),
        new Token.Number(0, 0, 42),
        new Token.Comma(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
  }

  def 'throw exception when unexpected token is found in object 1'() {
    given:
    // {42}
    def tokens = [
        new Token.LeftCurly(0, 0),
        new Token.Number(0, 0, 42),
        new Token.RightCurly(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_TOKEN
  }

  def 'throw exception when unexpected token is found in object 2'() {
    given:
    // {"foo" 42}
    def tokens = [
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, 'foo'),
        new Token.Number(0, 0, 42),
        new Token.RightCurly(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_TOKEN
  }

  def 'throw exception when unexpected token is found in object 3'() {
    given:
    // {"foo":}
    def tokens = [
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, 'foo'),
        new Token.Colon(0, 0),
        new Token.RightCurly(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_TOKEN
  }

  def 'throw exception when unexpected token is found in object 4'() {
    given:
    // {"foo": 42 "bar"}
    def tokens = [
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, 'foo'),
        new Token.Colon(0, 0),
        new Token.Number(0, 0, 42),
        new Token.String(0, 0, 'bar'),
        new Token.RightCurly(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_TOKEN
  }

  def 'throw exception when unexpected token is found in object 5'() {
    given:
    // {"foo": 42,}
    def tokens = [
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, 'foo'),
        new Token.Colon(0, 0),
        new Token.Number(0, 0, 42),
        new Token.Comma(0, 0),
        new Token.RightCurly(0, 0),
    ]
    def parser = new Parser(tokens.iterator())

    when:
    parser.parse()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_TOKEN
  }
}
