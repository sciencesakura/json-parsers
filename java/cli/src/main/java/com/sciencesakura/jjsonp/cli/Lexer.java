// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.cli;

import com.sciencesakura.jjsonp.core.Strings;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

final class Lexer implements Iterator<Token> {

  private final byte[] expression;

  private int bc = -1;

  private int pos;

  private Token next;

  Lexer(String expression) {
    this.expression = expression == null ? new byte[0] : expression.getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public boolean hasNext() {
    if (next == null) {
      int c;
      do {
        c = nextChar();
      } while (Character.isWhitespace(c));
      next = switch (c) {
        case -1 -> Tokens.EOF;
        case '.' -> Tokens.PERIOD;
        case '[' -> Tokens.LEFT_BRACKET;
        case ']' -> Tokens.RIGHT_BRACKET;
        case '"' -> recognizeQuotedString();
        default -> {
          if (Strings.isDigit(c)) {
            yield recognizeInteger(c);
          } else {
            yield recognizeString(c);
          }
        }
      };
    }
    return next != Tokens.EOF;
  }

  @Override
  public Token next() {
    if (!hasNext()) {
      throw ParserException.unexpectedEOF();
    }
    var current = next;
    next = null;
    return current;
  }

  private Token recognizeQuotedString() {
    var str = new StringBuilder();
    var escape = false;
    for (var c = nextChar(); c != -1; c = nextChar()) {
      if (escape) {
        str.appendCodePoint(c);
        escape = false;
      } else if (c == '"') {
        return new Token.String(str.toString());
      } else if (c == '\\') {
        escape = true;
      } else {
        str.appendCodePoint(c);
      }
    }
    throw ParserException.unexpectedEOF();
  }

  private Token recognizeString(int c1) {
    var str = new StringBuilder().appendCodePoint(c1);
    int c;
    for (c = nextChar(); c != -1 && c != '.' && c != '[' && !Character.isWhitespace(c); c = nextChar()) {
      str.appendCodePoint(c);
    }
    if (c != -1) {
      saveChar(c);
    }
    return new Token.String(str.toString());
  }

  private Token recognizeInteger(int c1) {
    var str = new StringBuilder().appendCodePoint(c1);
    int c;
    for (c = nextChar(); c != -1 && Strings.isDigit(c); c = nextChar()) {
      str.appendCodePoint(c);
    }
    if (c != -1) {
      saveChar(c);
    }
    return new Token.Integer(Integer.parseInt(str.toString()));
  }

  private int nextChar() {
    int c;
    if (bc == -1) {
      c = Strings.nextUTF8Char(() -> pos < expression.length ? expression[pos++] : -1);
    } else {
      c = bc;
      bc = -1;
    }
    return c;
  }

  private void saveChar(int c) {
    bc = c;
  }
}
