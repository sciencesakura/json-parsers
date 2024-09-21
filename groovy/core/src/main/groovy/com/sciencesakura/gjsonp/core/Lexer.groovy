// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import groovy.transform.PackageScope
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.charset.StandardCharsets

@PackageScope
class Lexer implements Iterator<Token> {

  private final channel

  private final buffer

  private current

  private line = 1L

  private column = 0L

  private prevColumn

  private bc = -1

  private Lexer(channel, buffer) {
    this.channel = channel
    this.buffer = buffer
  }

  static Lexer newLexer(ReadableByteChannel channel, int bufferSize) {
    new Lexer(channel, ByteBuffer.allocate(bufferSize)).tap(this::load)
  }

  @Override
  boolean hasNext() {
    if (!current) {
      def c
      do {
        c = nextChar()
      } while (Characters.isWhitespace(c))
      if (c == -1) return false
      current = switch (c) {
        case ',' -> new Token.Comma(line, column)
        case ':' -> new Token.Colon(line, column)
        case '[' -> new Token.LeftBracket(line, column)
        case ']' -> new Token.RightBracket(line, column)
        case '{' -> new Token.LeftCurly(line, column)
        case '}' -> new Token.RightCurly(line, column)
        case '"' -> recognizeString()
        case '-', Characters::isDigit -> recognizeNumber(c)
        case Characters::isAlpha -> recognizeKeyword(c)
        default -> throw ParserException.unexpectedCharacter(c, line, column)
      }
    }
    true
  }

  @Override
  Token next() {
    if (!hasNext()) throw new NoSuchElementException()
    def current = this.current
    this.current = null
    current
  }

  private recognizeString() {
    def startColumn = column
    def str = new StringBuilder()
    def escaped = false
    def c
    while ((c = nextChar()) != -1) {
      if (escaped) {
        escaped = false
        switch (c) {
          case '"', '/', '\\' -> str << c
          case 'b' -> str << '\b'
          case 'f' -> str << '\f'
          case 'n' -> str << '\n'
          case 'r' -> str << '\r'
          case 't' -> str << '\t'
          case 'u' -> str.appendCodePoint(nextHexCodePoint())
          default -> throw ParserException.unexpectedCharacter(c, line, column)
        }
      } else if (c == '"') {
        return new Token.String(line, startColumn, str.toString())
      } else if (c == '\\') {
        escaped = true
      } else if (Characters.isControl(c)) {
        throw ParserException.unexpectedCharacter(c, line, column)
      } else {
        str << c
      }
    }
    throw ParserException.unexpectedEOF(line, column)
  }

  private nextHexCodePoint() {
    def str = new StringBuilder(4)
    4.times {
      def c = nextChar()
      if (!Characters.isHex(c)) throw ParserException.unexpectedCharacter(c, line, column)
      str << c
    }
    Integer.parseInt(str.toString(), 16)
  }

  private recognizeKeyword(c1) {
    def startColumn = column
    def c = c1
    def str = new StringBuilder(5)
    do {
      str << c
    } while (Characters.isAlpha(c = nextChar()))
    backChar(c)
    def value = str.toString()
    switch (value) {
      case 'true' -> new Token.Bool(line, startColumn, true)
      case 'false' -> new Token.Bool(line, startColumn, false)
      case 'null' -> new Token.Null(line, startColumn)
      default -> throw ParserException.unknownToken(value, line, startColumn)
    }
  }

  private recognizeNumber(c1) {
    def startColumn = column
    def c = c1
    def str = new StringBuilder()
    do {
      str << c
    } while (Characters.isDigit(c = nextChar()))
    if (c1 == '-' && str.length() == 1) throw ParserException.unexpectedCharacter(c, line, column)
    def integral = true
    if (c == '.') {
      str << c
      while (Characters.isDigit(c = nextChar())) {
        str << c
      }
      if (str.endsWithAny('.')) throw ParserException.unexpectedCharacter(c, line, column)
      integral = false
    }
    if (c == 'E' || c == 'e') {
      str << 'e'
      c = nextChar()
      if (c == '+' || c == '-') {
        str << c
        c = nextChar()
      }
      for (; Characters.isDigit(c); c = nextChar()) {
        str << c
      }
      if (str.endsWithAny('e', '+', '-')) throw ParserException.unexpectedCharacter(c, line, column)
      integral = false
    }
    backChar(c)
    new Token.Number(line, startColumn, integral ? str as Long : str as Double)
  }

  private nextChar() {
    def c
    if (bc == -1) {
      c = readChar()
    } else {
      c = bc
      bc = -1
    }
    if (c == '\n') {
      line++
      prevColumn = column
      column = 0
    } else {
      column++
    }
    c
  }

  private backChar(c) {
    bc = c
    if (c == '\n') {
      line--
      column = prevColumn
    } else {
      column--
    }
  }

  private readChar() {
    def b1 = readByte()
    if (b1 == -1) return -1
    if (!(b1 & 0x80)) {
      // U+0000 to U+007F
      return b1 as char
    }
    def b2 = readByte()
    if ((b2 & 0xC0) != 0x80) return Characters.REPLACEMENT_CHAR
    if ((b1 & 0xE0) == 0xC0) {
      // U+0080 to U+07FF
      return new String([b1, b2] as byte[], StandardCharsets.UTF_8)
    }
    def b3 = readByte()
    if ((b3 & 0xC0) != 0x80) return Characters.REPLACEMENT_CHAR
    if ((b1 & 0xF0) == 0xE0) {
      // U+0800 to U+FFFF
      return new String([b1, b2, b3] as byte[], StandardCharsets.UTF_8)
    }
    def b4 = readByte()
    if ((b4 & 0xC0) != 0x80) return Characters.REPLACEMENT_CHAR
    if ((b1 & 0xF8) == 0xF0) {
      // U+10000 to U+10FFFF
      return new String([b1, b2, b3, b4] as byte[], StandardCharsets.UTF_8)
    }
    Characters.REPLACEMENT_CHAR
  }

  private readByte() {
    buffer.hasRemaining() || 0 < load() ? buffer.get() : -1
  }

  private load() {
    buffer.clear().with { b ->
      channel.read(b).tap { b.flip() }
    }
  }
}
