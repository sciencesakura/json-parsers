// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import org.jspecify.annotations.NonNull;

final class Lexer implements Iterator<Token> {

  private final ReadableByteChannel channel;

  private final ByteBuffer buffer;

  private int bc = -1;

  private long line = 1;

  private long column;

  private long prevColumn;

  private Token next;

  private Lexer(ReadableByteChannel channel, int bufferSize) {
    this.channel = channel;
    this.buffer = ByteBuffer.allocate(bufferSize);
  }

  private int load() {
    try {
      buffer.clear();
      var n = channel.read(buffer);
      buffer.flip();
      return n;
    } catch (IOException e) {
      throw new UncheckedIOException(e.getMessage(), e);
    }
  }

  static Lexer newLexer(ReadableByteChannel channel, int bufferSize) {
    var lexer = new Lexer(channel, bufferSize);
    lexer.load();
    return lexer;
  }

  @Override
  public boolean hasNext() {
    if (next == null) {
      int c;
      do {
        c = nextChar();
      } while (Strings.isWhitespace(c));
      next = switch (c) {
        case -1 -> new Token.EOF(line, column);
        case '{' -> new Token.LeftCurly(line, column);
        case '}' -> new Token.RightCurly(line, column);
        case '[' -> new Token.LeftBracket(line, column);
        case ']' -> new Token.RightBracket(line, column);
        case ':' -> new Token.Colon(line, column);
        case ',' -> new Token.Comma(line, column);
        case '"' -> recognizeString();
        default -> {
          if (Strings.isAlpha(c)) {
            yield recognizeKeyword(c);
          } else if (Strings.isDigit(c) || c == '-') {
            yield recognizeNumber(c);
          }
          throw ParserException.unexpectedCharacter(c, line, column);
        }
      };
    }
    return !(next instanceof Token.EOF);
  }

  @Override
  @NonNull
  public Token next() {
    if (!hasNext()) {
      throw ParserException.unexpectedEOF(line, column);
    }
    var current = next;
    next = null;
    return current;
  }

  private Token recognizeString() {
    var startColumn = column;
    var str = new StringBuilder();
    var escaped = false;
    for (var c = nextChar(); c != -1; c = nextChar()) {
      if (escaped) {
        switch (c) {
          case '"' -> str.append('"');
          case '\\' -> str.append('\\');
          case '/' -> str.append('/');
          case 'b' -> str.append('\b');
          case 'f' -> str.append('\f');
          case 'n' -> str.append('\n');
          case 'r' -> str.append('\r');
          case 't' -> str.append('\t');
          case 'u' -> str.appendCodePoint(decodeHex(nextChar()) << 12 | decodeHex(nextChar()) << 8
              | decodeHex(nextChar()) << 4 | decodeHex(nextChar()));
          default -> throw ParserException.unexpectedCharacter(c, line, column);
        }
        escaped = false;
      } else if (c == '"') {
        return new Token.String(str.toString(), line, startColumn);
      } else if (c == '\\') {
        escaped = true;
      } else if (Character.isISOControl(c)) {
        throw ParserException.unexpectedCharacter(c, line, column);
      } else {
        str.appendCodePoint(c);
      }
    }
    throw ParserException.unexpectedEOF(line, column);
  }

  private int decodeHex(int c) {
    if (Strings.isDigit(c)) {
      return c - '0';
    }
    if ('A' <= c && c <= 'F') {
      return c - 'A' + 10;
    }
    if ('a' <= c && c <= 'f') {
      return c - 'a' + 10;
    }
    throw ParserException.unexpectedCharacter(c, line, column);
  }

  private Token recognizeKeyword(int c1) {
    var startColumn = column;
    var str = new StringBuilder(5).appendCodePoint(c1);
    int c;
    for (c = nextChar(); Strings.isAlpha(c); c = nextChar()) {
      str.appendCodePoint(c);
    }
    if (c != -1) {
      saveChar(c);
    }
    var keyword = str.toString();
    return switch (keyword) {
      case "true" -> new Token.True(line, startColumn);
      case "false" -> new Token.False(line, startColumn);
      case "null" -> new Token.Null(line, startColumn);
      default -> throw ParserException.unknownToken(keyword, line, startColumn);
    };
  }

  private Token recognizeNumber(int c1) {
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    var startColumn = column;
    var str = new StringBuilder().appendCodePoint(c1);
    int c;
    for (c = nextChar(); Strings.isDigit(c); c = nextChar()) {
      str.appendCodePoint(c);
    }
    if (c1 == '-' && str.length() == 1) {
      throw ParserException.unexpectedCharacter(c, line, column);
    }
    var isFloat = false;
    if (c == '.') {
      str.append('.');
      var cnt = 0;
      for (c = nextChar(); Strings.isDigit(c); c = nextChar(), cnt++) {
        str.appendCodePoint(c);
      }
      if (cnt == 0) {
        throw ParserException.unexpectedCharacter(c, line, column);
      }
      isFloat = true;
    }
    if (c == 'e' || c == 'E') {
      str.append('e');
      c = nextChar();
      if (c == '+' || c == '-') {
        str.appendCodePoint(c);
        c = nextChar();
      }
      var cnt = 0;
      for (; Strings.isDigit(c); c = nextChar(), cnt++) {
        str.appendCodePoint(c);
      }
      if (cnt == 0) {
        throw ParserException.unexpectedCharacter(c, line, column);
      }
      isFloat = true;
    }
    if (c != -1) {
      saveChar(c);
    }
    return isFloat ? new Token.Float(Double.parseDouble(str.toString()), line, startColumn)
        : new Token.Integer(Long.parseLong(str.toString()), line, startColumn);
  }

  private int nextChar() {
    int c;
    if (bc == -1) {
      c = Strings.nextUTF8Char(() -> buffer.hasRemaining() || load() > 0 ? buffer.get() : -1);
    } else {
      c = bc;
      bc = -1;
    }
    if (c == '\n') {
      line++;
      prevColumn = column;
      column = 0;
    } else {
      column++;
    }
    return c;
  }

  private void saveChar(int c) {
    bc = c;
    if (c == '\n') {
      line--;
      column = prevColumn;
    } else {
      column--;
    }
  }
}
