// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import groovy.transform.PackageScope

/**
 * Throws when a syntax error is found during parsing.
 */
class ParserException extends RuntimeException {

  final long line

  final long column

  private ParserException(message, line, column) {
    super("${message} at ${line}:${column}")
    this.line = line
    this.column = column
  }

  private ParserException(message) {
    super(message)
    this.line = 0
    this.column = 0
  }

  @PackageScope
  static ParserException unexpectedCharacter(c, long line, long column) {
    if (c == -1) return unexpectedEOF(line, column)
    def display = Characters.isControl(c) ? 'U+%04X'.formatted((c as char) as int) : c
    new ParserException("Unexpected character '$display'", line, column)
  }

  @PackageScope
  static ParserException unexpectedEOF(long line, long column) {
    new ParserException('Unexpected end of input', line, column)
  }

  @PackageScope
  static ParserException unexpectedEOF() {
    new ParserException('Unexpected end of input')
  }

  @PackageScope
  static ParserException unknownToken(CharSequence token, long line, long column) {
    new ParserException("Unknown token '$token'", line, column)
  }

  @PackageScope
  static ParserException unexpectedToken(Token token) {
    new ParserException("Unexpected token '${token.class.simpleName}'", token.line(), token.column())
  }
}
