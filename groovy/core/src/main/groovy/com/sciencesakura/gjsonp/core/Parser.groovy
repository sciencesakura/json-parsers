// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import groovy.transform.PackageScope

@PackageScope
class Parser {

  private final tokens

  Parser(Iterator<Token> tokens) {
    this.tokens = tokens
  }

  def parse() {
    if (tokens.hasNext()) return parseValue(tokens.next())
    throw ParserException.unexpectedEOF()
  }

  private parseValue(token) {
    switch (token) {
      case Token.Null -> null
      case Token.Bool, Token.String, Token.Number -> token.value()
      case Token.LeftBracket -> parseArray()
      case Token.LeftCurly -> parseObject()
      default -> throw ParserException.unexpectedToken(token)
    }
  }

  private parseArray() {
    def status = ArrayStatus.INIT
    def array = []
    while (tokens.hasNext()) {
      def token = tokens.next()
      switch (status) {
        case ArrayStatus.INIT:
          // [
          if (token instanceof Token.RightBracket) {
            return array.asImmutable()
          }
          array << parseValue(token)
          status = ArrayStatus.AFTER_VALUE
          break
        case ArrayStatus.AFTER_VALUE:
          // [xxx
          if (token instanceof Token.RightBracket) {
            return array.asImmutable()
          }
          if (token instanceof Token.Comma) {
            status = ArrayStatus.AFTER_COMMA
            break
          }
          throw ParserException.unexpectedToken(token)
        case ArrayStatus.AFTER_COMMA:
          // [xxx,
          array << parseValue(token)
          status = ArrayStatus.AFTER_VALUE
          break
      }
    }
    throw ParserException.unexpectedEOF()
  }

  private parseObject() {
    def status = ObjectStatus.INIT
    def object = [:]
    def name
    while (tokens.hasNext()) {
      def token = tokens.next()
      switch (status) {
        case ObjectStatus.INIT:
          // {
          if (token instanceof Token.RightCurly) {
            return object.asImmutable()
          }
          if (token instanceof Token.String) {
            name = token.value()
            status = ObjectStatus.AFTER_NAME
            break
          }
          throw ParserException.unexpectedToken(token)
        case ObjectStatus.AFTER_NAME:
          // {"xxx"
          if (token instanceof Token.Colon) {
            status = ObjectStatus.AFTER_COLON
            break
          }
          throw ParserException.unexpectedToken(token)
        case ObjectStatus.AFTER_COLON:
          // {"xxx":
          object[name] = parseValue(token)
          status = ObjectStatus.AFTER_VALUE
          break
        case ObjectStatus.AFTER_VALUE:
          // {"xxx": yyy
          if (token instanceof Token.RightCurly) {
            return object.asImmutable()
          }
          if (token instanceof Token.Comma) {
            status = ObjectStatus.AFTER_COMMA
            break
          }
          throw ParserException.unexpectedToken(token)
        case ObjectStatus.AFTER_COMMA:
          // {"xxx": yyy,
          if (token instanceof Token.String) {
            name = token.value()
            status = ObjectStatus.AFTER_NAME
            break
          }
          throw ParserException.unexpectedToken(token)
      }
    }
    throw ParserException.unexpectedEOF()
  }

  private enum ArrayStatus {
    INIT,
    AFTER_VALUE,
    AFTER_COMMA,
  }

  private enum ObjectStatus {
    INIT,
    AFTER_NAME,
    AFTER_COLON,
    AFTER_VALUE,
    AFTER_COMMA,
  }
}
