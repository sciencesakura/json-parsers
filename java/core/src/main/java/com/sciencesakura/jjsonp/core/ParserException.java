// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.Serial;
import org.jspecify.annotations.NonNull;

/**
 * Thrown when an error occurs during parsing.
 */
public final class ParserException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  private final Type type;

  private final long line;

  private final long column;

  private ParserException(String message, Type type, long line, long column) {
    super(line == 0 && column == 0 ? message : "%s at %d:%d".formatted(message, line, column));
    this.type = type;
    this.line = line;
    this.column = column;
  }

  private ParserException(String message, Type type) {
    this(message, type, 0, 0);
  }

  @NonNull
  public Type getType() {
    return type;
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
    return new ParserException("Unexpected character '%s'".formatted(ch), Type.UNEXPECTED_CHARACTER, line, column);
  }

  static ParserException unexpectedEOF(long line, long column) {
    return new ParserException("Unexpected end of input", Type.UNEXPECTED_EOF, line, column);
  }

  static ParserException unexpectedEOF() {
    return new ParserException("Unexpected end of input", Type.UNEXPECTED_EOF);
  }

  static ParserException unknownToken(String token, long line, long column) {
    return new ParserException("Unknown token '%s'".formatted(token), Type.UNKNOWN_TOKEN, line, column);
  }

  static ParserException unexpectedToken(Token token) {
    return new ParserException("Unexpected token '%s'".formatted(token), Type.UNEXPECTED_TOKEN, token.line(), token.column());
  }

  /**
   * The type of the parser exception.
   */
  public enum Type {
    UNEXPECTED_CHARACTER,
    UNEXPECTED_EOF,
    UNKNOWN_TOKEN,
    UNEXPECTED_TOKEN,
  }
}
