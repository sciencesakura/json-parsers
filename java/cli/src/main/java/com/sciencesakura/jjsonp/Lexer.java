// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp;

import com.sciencesakura.jjsonp.core.Characters;
import java.util.Iterator;

final class Lexer implements Iterator<Token> {

  private final char[] expression;

  private int bc = -1;

  private int pos;

  private Token current;

  Lexer(String expression) {
    this.expression = expression == null ? new char[0] : expression.toCharArray();
  }

  @Override
  public boolean hasNext() {
    if (current == null) {
      int c;
      do {
        c = nextChar();
      } while (Characters.isWhitespace(c) || Characters.isControl(c));
      if (c == -1) {
        return false;
      }
      current = switch (c) {
        case '.' -> new Token.Period(pos);
        case '[' -> new Token.LeftBracket(pos);
        case ']' -> new Token.RightBracket(pos);
        case '"' -> nextQuotedString();
        default -> Characters.isDigit(c) ? nextInteger(c) : nextString(c);
      };
    }
    return true;
  }

  @Override
  public Token next() {
    if (!hasNext()) {
      throw InvalidExpressionException.unexpectedEOF(pos);
    }
    var current = this.current;
    this.current = null;
    return current;
  }

  private Token nextQuotedString() {
    var startPos = pos;
    var str = new StringBuilder();
    var escaped = false;
    int c;
    while ((c = nextChar()) != -1) {
      if (Characters.isControl(c)) {
        continue;
      }
      if (escaped) {
        escaped = false;
        var ec = switch (c) {
          case 'b' -> '\b';
          case 'f' -> '\f';
          case 'n' -> '\n';
          case 'r' -> '\r';
          case 't' -> '\t';
          default -> (char) c;
        };
        str.append(ec);
      } else if (c == '"') {
        return new Token.String(startPos, str.toString());
      } else if (c == '\\') {
        escaped = true;
      } else {
        str.append((char) c);
      }
    }
    throw InvalidExpressionException.unexpectedEOF(pos);
  }

  private Token nextString(int c1) {
    var startPos = pos;
    var str = new StringBuilder();
    var c = c1;
    do {
      if (Characters.isControl(c)) {
        continue;
      }
      str.append((char) c);
    } while ((c = nextChar()) != -1 && c != '.' && c != '[' && c != ']' && !Characters.isWhitespace(c));
    backChar(c);
    return new Token.String(startPos, str.toString());
  }

  private Token nextInteger(int c1) {
    var startPos = pos;
    var str = new StringBuilder();
    var c = c1;
    do {
      str.append((char) c);
    } while (Characters.isDigit(c = nextChar()));
    backChar(c);
    return new Token.Integer(startPos, Integer.parseInt(str.toString()));
  }

  private int nextChar() {
    if (bc != -1) {
      int c = bc;
      bc = -1;
      return c;
    }
    var idx = pos++;
    return idx < expression.length ? expression[idx] : -1;
  }

  private void backChar(int c) {
    bc = c;
  }
}
