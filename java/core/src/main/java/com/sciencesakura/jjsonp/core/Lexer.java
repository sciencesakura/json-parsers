// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

final class Lexer implements Iterator<Token> {

  private static final int REPLACEMENT_CHAR = 0xFFFD;

  private final Deque<Byte> bb = new ArrayDeque<>();

  private final ReadableByteChannel channel;

  private final ByteBuffer buffer;

  private int bc = -1;

  private long line = 1;

  private long column;

  private long prevColumn;

  private Token current;

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
    if (current == null) {
      int c;
      do {
        c = nextChar();
      } while (Characters.isWhitespace(c));
      if (c == -1) {
        return false;
      }
      current = switch (c) {
        case '{' -> new Token.LeftCurly(line, column);
        case '}' -> new Token.RightCurly(line, column);
        case '[' -> new Token.LeftBracket(line, column);
        case ']' -> new Token.RightBracket(line, column);
        case ':' -> new Token.Colon(line, column);
        case ',' -> new Token.Comma(line, column);
        case '"' -> nextString();
        default -> {
          if (Characters.isAlpha(c)) {
            yield nextKeyword(c);
          } else if (Characters.isDigit(c) || c == '-') {
            yield nextNumber(c);
          }
          throw ParserException.unexpectedCharacter(c, line, column);
        }
      };
    }
    return true;
  }

  @Override
  public Token next() {
    if (!hasNext()) {
      throw ParserException.unexpectedEOF(line, column);
    }
    var current = this.current;
    this.current = null;
    return current;
  }

  private Token nextString() {
    var startColumn = column;
    var str = new StringBuilder();
    int c;
    var escaped = false;
    while ((c = nextChar()) != -1) {
      if (escaped) {
        escaped = false;
        switch (c) {
          case '"', '\\', '/' -> str.appendCodePoint(c);
          case 'b' -> str.append('\b');
          case 'f' -> str.append('\f');
          case 'n' -> str.append('\n');
          case 'r' -> str.append('\r');
          case 't' -> str.append('\t');
          case 'u' -> str.appendCodePoint(nextHex() << 12 | nextHex() << 8 | nextHex() << 4 | nextHex());
          default -> throw ParserException.unexpectedCharacter(c, line, column);
        }
      } else if (c == '"') {
        return new Token.String(line, startColumn, str.toString());
      } else if (c == '\\') {
        escaped = true;
      } else if (Characters.isControl(c)) {
        throw ParserException.unexpectedCharacter(c, line, column);
      } else {
        str.appendCodePoint(c);
      }
    }
    throw ParserException.unexpectedEOF(line, column);
  }

  private int nextHex() {
    var c = nextChar();
    var h = Characters.decodeHex(c);
    if (h == -1) {
      throw ParserException.unexpectedCharacter(c, line, column);
    }
    return h;
  }

  private Token nextKeyword(int c1) {
    var startColumn = column;
    var str = new StringBuilder(5);
    var c = c1;
    do {
      str.appendCodePoint(c);
    } while (Characters.isAlpha(c = nextChar()));
    backChar(c);
    var keyword = str.toString();
    return switch (keyword) {
      case "true" -> new Token.True(line, startColumn);
      case "false" -> new Token.False(line, startColumn);
      case "null" -> new Token.Null(line, startColumn);
      default -> throw ParserException.unknownToken(keyword, line, startColumn);
    };
  }

  private Token nextNumber(int c1) {
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    var startColumn = column;
    var str = new StringBuilder();
    var c = c1;
    do {
      str.appendCodePoint(c);
    } while (Characters.isDigit(c = nextChar()));
    if (c1 == '-' && str.length() == 1) {
      throw ParserException.unexpectedCharacter(c, line, column);
    }
    var integral = true;
    if (c == '.') {
      str.append('.');
      var cnt = 0;
      while (Characters.isDigit(c = nextChar())) {
        str.appendCodePoint(c);
        cnt++;
      }
      if (cnt == 0) {
        throw ParserException.unexpectedCharacter(c, line, column);
      }
      integral = false;
    }
    if (c == 'e' || c == 'E') {
      str.append('e');
      c = nextChar();
      if (c == '+' || c == '-') {
        str.appendCodePoint(c);
        c = nextChar();
      }
      var cnt = 0;
      for (; Characters.isDigit(c); c = nextChar(), cnt++) {
        str.appendCodePoint(c);
      }
      if (cnt == 0) {
        throw ParserException.unexpectedCharacter(c, line, column);
      }
      integral = false;
    }
    backChar(c);
    return integral ? new Token.Integer(line, startColumn, Long.parseLong(str.toString()))
        : new Token.Float(line, startColumn, Double.parseDouble(str.toString()));
  }

  private int nextChar() {
    int c;
    if (bc == -1) {
      c = readChar();
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

  private void backChar(int c) {
    bc = c;
    if (c == '\n') {
      line--;
      column = prevColumn;
    } else {
      column--;
    }
  }

  private int readChar() {
    var b1 = readByte();
    if (b1 == -1) {
      return -1;
    }
    var length = lengthOfChar(b1);
    if (length == 1) {
      // U+0000 to U+007F
      return b1;
    }
    if (length == -1) {
      return REPLACEMENT_CHAR;
    }
    var b2 = readByte();
    if ((b2 & 0xC0) != 0x80) {
      backBytes(b2);
      return REPLACEMENT_CHAR;
    }
    if (length == 2) {
      // U+0080 to U+07FF
      return ((b1 & 0x1F) << 6) | (b2 & 0x3F);
    }
    var b3 = readByte();
    if ((b3 & 0xC0) != 0x80) {
      backBytes(b2, b3);
      return REPLACEMENT_CHAR;
    }
    if (length == 3) {
      // U+0800 to U+FFFF
      return ((b1 & 0x0F) << 12) | ((b2 & 0x3F) << 6) | (b3 & 0x3F);
    }
    var b4 = readByte();
    if ((b4 & 0xC0) != 0x80) {
      backBytes(b2, b3, b4);
      return REPLACEMENT_CHAR;
    }
    if (length == 4) {
      // U+10000 to U+10FFFF
      return ((b1 & 0x07) << 18) | ((b2 & 0x3F) << 12) | ((b3 & 0x3F) << 6) | (b4 & 0x3F);
    }
    throw new AssertionError("MUST NOT REACH HERE");
  }

  private static int lengthOfChar(byte b1) {
    if ((b1 & 0x80) == 0) {
      return 1;
    } else if ((b1 & 0xE0) == 0xC0) {
      return 2;
    } else if ((b1 & 0xF0) == 0xE0) {
      return 3;
    } else if ((b1 & 0xF8) == 0xF0) {
      return 4;
    } else {
      return -1;
    }
  }

  private byte readByte() {
    if (bb.isEmpty()) {
      return buffer.hasRemaining() || 0 < load() ? buffer.get() : -1;
    }
    return bb.poll();
  }

  private void backBytes(byte... bytes) {
    for (var b : bytes) {
      bb.add(b);
    }
  }
}
