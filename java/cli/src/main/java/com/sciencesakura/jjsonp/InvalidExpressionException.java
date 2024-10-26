// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp;

class InvalidExpressionException extends RuntimeException {

  private final int pos;

  InvalidExpressionException(String message, int pos) {
    super("%s at %d".formatted(message, pos));
    this.pos = pos;
  }

  public int getPos() {
    return pos;
  }

  static InvalidExpressionException unexpectedEOF(int pos) {
    return new InvalidExpressionException("Unexpected end of expression at position", pos);
  }

  static InvalidExpressionException unexpectedToken(Token token) {
    return new InvalidExpressionException("Unexpected token: '%s'".formatted(token.getClass().getSimpleName()), token.pos());
  }
}
