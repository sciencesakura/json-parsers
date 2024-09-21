// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import groovy.transform.PackageScope

/**
 * Throws when a syntax error is found during parsing.
 */
class ParserException extends RuntimeException {

  final Type type

  final long line

  final long column

  private ParserException(message, type, line, column) {
    super(line && column ? "${message} at ${line}:${column}" : message)
    this.type = type
    this.line = line
    this.column = column
  }

  @PackageScope
  static ParserException unexpectedCharacter(c, long line, long column) {
    if (c == -1) return unexpectedEOF(line, column)
    def display = Characters.isControl(c) ? 'U+%04X'.formatted((c as char) as int) : c
    new ParserException("Unexpected character: '$display'", Type.UNEXPECTED_CHARACTER, line, column)
  }

  @PackageScope
  static ParserException unexpectedEOF(long line = 0, long column = 0) {
    new ParserException('Unexpected end of input', Type.UNEXPECTED_EOF, line, column)
  }

  @PackageScope
  static ParserException unknownToken(token, long line, long column) {
    new ParserException("Unknown token '$token'", Type.UNKNOWN_TOKEN, line, column)
  }

  @PackageScope
  static ParserException unexpectedToken(Token token) {
    new ParserException("Unexpected token: $token", Type.UNEXPECTED_TOKEN, token.line(), token.column())
  }

  enum Type {
    UNEXPECTED_CHARACTER,
    UNEXPECTED_EOF,
    UNEXPECTED_TOKEN,
    UNKNOWN_TOKEN,
  }
}
