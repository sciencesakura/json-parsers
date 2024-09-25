// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import com.sciencesakura.gjsonp.core.Characters
import groovy.transform.PackageScope

@PackageScope
class Lexer implements Iterator<Token> {

  private final expression

  private current

  private pos = 0

  private bc = -1

  Lexer(String expression) {
    this.expression = expression.chars
  }

  @Override
  boolean hasNext() {
    if (!current) {
      def c
      do {
        c = nextChar()
      } while (Characters.isWhitespace(c) || Characters.isControl(c))
      if (c == -1) return false
      current = switch (c) {
        case '.' -> new Token.Period(pos)
        case '[' -> new Token.LeftBracket(pos)
        case ']' -> new Token.RightBracket(pos)
        case '"' -> nextQuotedString()
        case Characters::isDigit -> nextInteger(c)
        default -> nextString(c)
      }
    }
    true
  }

  @Override
  Token next() {
    if (!hasNext()) throw new InvalidExpressionException("Unexpected end of expression at $pos")
    def current = this.current
    this.current = null
    current
  }

  private nextInteger(c1) {
    def startPos = pos
    def c = c1
    def str = new StringBuilder()
    do {
      str << c
    } while (Characters.isDigit(c = nextChar()))
    backChar(c)
    new Token.Integer(startPos, str as int)
  }

  private nextString(c1) {
    def startPos = pos
    def c = c1
    def str = new StringBuilder()
    do {
      if (!Characters.isControl(c)) {
        str << c
      }
    } while ((c = nextChar()) != -1 && c != '.' && c != '[' && c != ']' && !Characters.isWhitespace(c))
    backChar(c)
    new Token.String(startPos, str.toString())
  }

  private nextQuotedString() {
    def startPos = pos
    def str = new StringBuilder()
    def escaped = false
    def c
    while ((c = nextChar()) != -1) {
      if (Characters.isControl(c)) continue
      if (escaped) {
        escaped = false
        switch (c) {
          case 'b' -> str << '\b'
          case 'f' -> str << '\f'
          case 'n' -> str << '\n'
          case 'r' -> str << '\r'
          case 't' -> str << '\t'
          default -> str << c
        }
      } else if (c == '"') {
        return new Token.String(startPos, str.toString())
      } else if (c == '\\') {
        escaped = true
      } else {
        str << c
      }
    }
    throw new InvalidExpressionException("Unterminated string at $pos")
  }

  private nextChar() {
    if (bc != -1) {
      def c = bc
      bc = -1
      return c
    }
    pos < expression.length ? expression[pos++] : -1
  }

  private backChar(c) {
    bc = c
  }
}
