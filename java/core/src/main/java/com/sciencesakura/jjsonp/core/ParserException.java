// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.Serial;

/**
 * Thrown when an error occurs during parsing.
 */
public final class ParserException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  private final long line;

  private final long column;

  private ParserException(String message, long line, long column) {
    super("%s at %d:%d".formatted(message, line, column));
    this.line = line;
    this.column = column;
  }

  private ParserException(String message) {
    super(message);
    this.line = 0;
    this.column = 0;
  }

  public long getLine() {
    return line;
  }

  public long getColumn() {
    return column;
  }

  static ParserException unexpectedCharacter(int c, long line, long column) {
    if (c == -1) {
      return unexpectedEOF(line, column);
    }
    var ch = Characters.isControl(c) ? "U+%04X".formatted(c) : Character.toString(c);
    return new ParserException("Unexpected character '%s'".formatted(ch), line, column);
  }

  static ParserException unexpectedEOF(long line, long column) {
    return new ParserException("Unexpected end of input", line, column);
  }

  static ParserException unexpectedEOF() {
    return new ParserException("Unexpected end of input");
  }

  static ParserException unknownToken(String token, long line, long column) {
    return new ParserException("Unknown token '%s'".formatted(token), line, column);
  }

  static ParserException unexpectedToken(Token token) {
    return new ParserException("Unexpected token '%s'".formatted(token.getClass().getSimpleName()), token.line(), token.column());
  }
}
