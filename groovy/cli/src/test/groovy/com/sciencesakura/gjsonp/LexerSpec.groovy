// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import spock.lang.Specification

class LexerSpec extends Specification {

  def 'ignore whitespace characters and control characters'() {
    given:
    def text = ' \t\n\r\u0000'

    when:
    def tokens = new Lexer(text).toList()

    then:
    assert tokens.empty
  }

  def 'recognize symbols'() {
    given:
    def text = '.[]'

    when:
    def tokens = new Lexer(text).toList()

    then:
    assert tokens == [
        new Token.Period(1),
        new Token.LeftBracket(2),
        new Token.RightBracket(3),
    ]
  }

  def 'recognize integers'() {
    given:
    def text = '1 234'

    when:
    def tokens = new Lexer(text).toList()

    then:
    assert tokens == [
        new Token.Integer(1, 1),
        new Token.Integer(3, 234),
    ]
  }

  def 'recognize strings'() {
    given:
    def text = 'Hello Olá こんにちは 你好 안녕하세요 👋👋🏻👋🏼👋🏽👋🏾👋🏿'

    when:
    def tokens = new Lexer(text).toList()

    then:
    assert tokens == [
        new Token.String(1, 'Hello'),
        new Token.String(7, 'Olá'),
        new Token.String(11, 'こんにちは'),
        new Token.String(17, '你好'),
        new Token.String(20, '안녕하세요'),
        new Token.String(26, '👋👋🏻👋🏼👋🏽👋🏾👋🏿'),
    ]
  }

  def 'ignore control characters in strings'() {
    given:
    def text = 'a\u0000b\u0001c\u0002'

    when:
    def tokens = new Lexer(text).toList()

    then:
    assert tokens == [
        new Token.String(1, 'abc'),
    ]
  }

  def 'recognize quoted strings'() {
    given:
    def text = '"foo bar" "\\b\\f\\n\\r\\t\\\\"'

    when:
    def tokens = new Lexer(text).toList()

    then:
    assert tokens == [
        new Token.String(1, 'foo bar'),
        new Token.String(11, '\b\f\n\r\t\\'),
    ]
  }

  def 'ignore control characters in quoted strings'() {
    given:
    def text = '"a\u0000b\u0001c\u0002"'

    when:
    def tokens = new Lexer(text).toList()

    then:
    assert tokens == [
        new Token.String(1, 'abc'),
    ]
  }

  def "throw exception for unterminated string: '\"foo'"() {
    given:
    def text = '"foo'

    when:
    new Lexer(text).toList()

    then:
    def e = thrown(InvalidExpressionException)
    assert e.message == 'Unterminated string at 4'
  }

  def "throw exception for unterminated string: '\"'"() {
    given:
    def text = '"'

    when:
    new Lexer(text).toList()

    then:
    def e = thrown(InvalidExpressionException)
    assert e.message == 'Unterminated string at 1'
  }
}
