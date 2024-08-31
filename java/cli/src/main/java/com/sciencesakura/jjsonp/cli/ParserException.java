// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.cli;

import java.io.Serial;

final class ParserException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  private final Type type;

  ParserException(String message, Type type) {
    super(message);
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  static ParserException unexpectedEOF() {
    return new ParserException("Unexpected end of expression", Type.UNEXPECTED_EOF);
  }

  static ParserException unexpectedToken(Token token) {
    return new ParserException("Unexpected token: '%s'".formatted(token.getClass().getSimpleName()), Type.UNEXPECTED_TOKEN);
  }

  enum Type {
    UNEXPECTED_EOF,
    UNEXPECTED_TOKEN,
  }
}
