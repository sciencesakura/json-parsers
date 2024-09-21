// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import groovy.transform.PackageScope

@PackageScope
class Parser implements Iterator<Instruction> {

  private final tokens

  private current

  Parser(String expression) {
    this.tokens = expression ? new Lexer(expression) : Collections.emptyIterator()
  }

  @Override
  boolean hasNext() {
    if (!current && tokens.hasNext()) {
      current = parseInstruction(tokens.next())
    }
    current != null
  }

  @Override
  Instruction next() {
    if (!hasNext()) throw new NoSuchElementException()
    def current = this.current
    this.current = null
    current
  }

  private parseInstruction(t) {
    switch (t) {
      case Token.Period -> parseGetElementByPeriod()
      case Token.LeftBracket -> parseGetElementByBracket()
      default -> throw InvalidExpressionException.unexpectedToken(t)
    }
  }

  // .xxx
  private parseGetElementByPeriod() {
    def t = tokens.next()
    switch (t) {
      case Token.String -> new Instruction.GetElement(t.value())
      default -> throw InvalidExpressionException.unexpectedToken(t)
    }
  }

  // [xxx]
  private parseGetElementByBracket() {
    def t = tokens.next()
    if (t instanceof Token.Integer || t instanceof Token.String) {
      def value = t.value()
      if ((t = tokens.next()) instanceof Token.RightBracket) {
        return new Instruction.GetElement(value)
      }
    }
    throw InvalidExpressionException.unexpectedToken(t)
  }
}
