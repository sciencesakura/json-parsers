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
      current = nextInstruction(tokens.next())
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

  private nextInstruction(t) {
    switch (t) {
      case Token.Period -> nextGetElementByPeriod()
      case Token.LeftBracket -> nextGetElementByBracket()
      default -> throw InvalidExpressionException.unexpectedToken(t)
    }
  }

  // .xxx
  private nextGetElementByPeriod() {
    def t = tokens.next()
    if (t instanceof Token.String) {
      return new Instruction.GetElement(t.value())
    }
    throw InvalidExpressionException.unexpectedToken(t)
  }

  // [xxx]
  private nextGetElementByBracket() {
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
