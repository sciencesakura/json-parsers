// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import java.nio.channels.Channels
import java.nio.charset.StandardCharsets
import spock.lang.Specification

class LexerSpec extends Specification {

  def'do nothing when there is no input'() {
    given:
    def text = ''
    def channel = channelFrom text

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens.empty

    cleanup:
    channel?.close()
  }

  def 'ignore whitespace characters'() {
    given:
    def text = ' \t\n\r'
    def channel = channelFrom text

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens.empty

    cleanup:
    channel?.close()
  }

  def 'recognize symbols'() {
    given:
    def text = ',:[]{}'
    def channel = channelFrom text

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens == [
        new Token.Comma(1, 1),
        new Token.Colon(1, 2),
        new Token.LeftBracket(1, 3),
        new Token.RightBracket(1, 4),
        new Token.LeftCurly(1, 5),
        new Token.RightCurly(1, 6),
    ]

    cleanup:
    channel?.close()
  }

  def 'throw exception for unknown symbol'() {
    given:
    def text = ',:[]()'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_CHARACTER
    assert e.line == 1
    assert e.column == 5

    cleanup:
    channel?.close()
  }

  def 'recognize strings'() {
    given:
    def text = '"Hello" "Olá" "こんにちは" "你好" "안녕하세요" "👋👋🏻👋🏼👋🏽👋🏾👋🏿" ""'
    def channel = channelFrom text

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens == [
        new Token.String(1, 1, 'Hello'),
        new Token.String(1, 9, 'Olá'),
        new Token.String(1, 15, 'こんにちは'),
        new Token.String(1, 23, '你好'),
        new Token.String(1, 28, '안녕하세요'),
        new Token.String(1, 36, '👋👋🏻👋🏼👋🏽👋🏾👋🏿'),
        new Token.String(1, 50, ''),
    ]

    cleanup:
    channel?.close()
  }

  def 'recognize strings containing escape sequences'() {
    given:
    def text = '"\\"" "\\\\" "\\/" "\\b" "\\f" "\\n" "\\r" "\\t"'
    def channel = channelFrom text

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens == [
        new Token.String(1, 1, '"'),
        new Token.String(1, 6, '\\'),
        new Token.String(1, 11, '/'),
        new Token.String(1, 16, '\b'),
        new Token.String(1, 21, '\f'),
        new Token.String(1, 26, '\n'),
        new Token.String(1, 31, '\r'),
        new Token.String(1, 36, '\t'),
    ]

    cleanup:
    channel?.close()
  }

  def 'recognize strings containing unicode sequences'() {
    given:
    def text = '''\
        "\\u0048\\u0065\\u006C\\u006C\\u006F"
        "\\u004F\\u006C\\u00E1"
        "\\u3053\\u3093\\u306B\\u3061\\u306F"
        "\\u4F60\\u597D"
        "\\uC548\\uB155\\uD558\\uC138\\uC694"
        "\\uD83D\\uDC4B\\uD83D\\uDC4B\\uD83C\\uDFFB\\uD83D\\uDC4B\\uD83C\\uDFFC\\uD83D\\uDC4B\\uD83C\\uDFFD\\uD83D\\uDC4B\\uD83C\\uDFFE\\uD83D\\uDC4B\\uD83C\\uDFFF"
        '''.stripIndent()
    def channel = channelFrom text

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens == [
        new Token.String(1, 1, 'Hello'),
        new Token.String(2, 1, 'Olá'),
        new Token.String(3, 1, 'こんにちは'),
        new Token.String(4, 1, '你好'),
        new Token.String(5, 1, '안녕하세요'),
        new Token.String(6, 1, '👋👋🏻👋🏼👋🏽👋🏾👋🏿'),
    ]

    cleanup:
    channel?.close()
  }

  def 'recognize strings containing invalid UTF-8 byte sequence'() {
    given:
    def bytes = [
        0x22, 0x61, 0x80, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xC0, 0x40, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xE0, 0x40, 0x80, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xE0, 0x80, 0x40, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xF0, 0x40, 0x80, 0x80, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xF0, 0x80, 0x40, 0x80, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xF0, 0x80, 0x80, 0x40, 0x62, 0x22, 0x0A,
    ] as byte[]
    def channel = channelFrom bytes

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens == [
        new Token.String(1, 1, 'a�b'),
        new Token.String(2, 1, 'a�@b'),
        new Token.String(3, 1, 'a�@�b'),
        new Token.String(4, 1, 'a��@b'),
        new Token.String(5, 1, 'a�@��b'),
        new Token.String(6, 1, 'a��@�b'),
        new Token.String(7, 1, 'a���@b'),
    ]

    cleanup:
    channel?.close()
  }

  def "throw exception for unterminated string: '\"foo'"() {
    given:
    def text = '"foo'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
    assert e.line == 1
    assert e.column == 5

    cleanup:
    channel?.close()
  }

  def "throw exception for unterminated string: '\"'"() {
    given:
    def text = '"'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
    assert e.line == 1
    assert e.column == 2

    cleanup:
    channel?.close()
  }

  def 'throw exception for string containing invalid escape sequence'() {
    given:
    def text = '"\\x"'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_CHARACTER
    assert e.line == 1
    assert e.column == 3

    cleanup:
    channel?.close()
  }

  def "throw exception for string containing invalid unicode sequence: '\"\\u000G\"'"() {
    given:
    def text = '"\\u000G"'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_CHARACTER
    assert e.line == 1
    assert e.column == 7

    cleanup:
    channel?.close()
  }

  def "throw exception for string containing invalid unicode sequence: '\"\\u004\"'"() {
    given:
    def text = '"\\u004"'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_CHARACTER
    assert e.line == 1
    assert e.column == 7

    cleanup:
    channel?.close()
  }

  def 'throw exception for string containing control character'() {
    given:
    def text = '"Hello\tWorld"'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_CHARACTER
    assert e.line == 1
    assert e.column == 7

    cleanup:
    channel?.close()
  }

  def 'recognize keywords'() {
    given:
    def text = 'true false null'
    def channel = channelFrom text

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens == [
        new Token.Bool(1, 1, true),
        new Token.Bool(1, 6, false),
        new Token.Null(1, 12),
    ]

    cleanup:
    channel?.close()
  }

  def 'throw exception for unknown keyword'() {
    given:
    def text = 'true false nil'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNKNOWN_TOKEN
    assert e.line == 1
    assert e.column == 12

    cleanup:
    channel?.close()
  }

  def 'recognize integer numbers'() {
    given:
    def text = '1 234 -6 -678'
    def channel = channelFrom text

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens == [
        new Token.Number(1, 1, 1L),
        new Token.Number(1, 3, 234L),
        new Token.Number(1, 7, -6L),
        new Token.Number(1, 10, -678L),
    ]

    cleanup:
    channel?.close()
  }

  def 'recognize floating-point numbers written in decimal notation'() {
    given:
    def text = '0.1 23.45 -6.7 -89.01'
    def channel = channelFrom text

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens == [
        new Token.Number(1, 1, 0.1D),
        new Token.Number(1, 5, 23.45D),
        new Token.Number(1, 11, -6.7D),
        new Token.Number(1, 16, -89.01D),
    ]

    cleanup:
    channel?.close()
  }

  def 'recognize floating-point numbers written in E notation'() {
    given:
    def text = '''\
        0e1 23e45 -6e7 -89e01
        0E1 23E45 -6E7 -89E01
        0.1e2 34.56e78 -9.0e1 -23.45e67
        0.1E2 34.56E78 -9.0E1 -23.45E67
        '''.stripIndent()
    def channel = channelFrom text

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens == [
        new Token.Number(1, 1, 0e1D),
        new Token.Number(1, 5, 23e45D),
        new Token.Number(1, 11, -6e7D),
        new Token.Number(1, 16, -89e1D),
        new Token.Number(2, 1, 0e1D),
        new Token.Number(2, 5, 23e45D),
        new Token.Number(2, 11, -6e7D),
        new Token.Number(2, 16, -89e1D),
        new Token.Number(3, 1, 0.1e2D),
        new Token.Number(3, 7, 34.56e78D),
        new Token.Number(3, 16, -9.0e1D),
        new Token.Number(3, 23, -23.45e67D),
        new Token.Number(4, 1, 0.1e2D),
        new Token.Number(4, 7, 34.56e78D),
        new Token.Number(4, 16, -9.0e1D),
        new Token.Number(4, 23, -23.45e67D),
    ]

    cleanup:
    channel?.close()
  }

  def 'recognize floating-point numbers written in E notation (signed)'() {
    given:
    def text = '''\
        0e+1 23e+45 -6e+7 -89e+01
        0E+1 23E+45 -6E+7 -89E+01
        0e-1 23e-45 -6e-7 -89e-01
        0E-1 23E-45 -6E-7 -89E-01
        0.1e+2 34.56e+78 -9.0e+1 -23.45e+67
        0.1E+2 34.56E+78 -9.0E+1 -23.45E+67
        0.1e-2 34.56e-78 -9.0e-1 -23.45e-67
        0.1E-2 34.56E-78 -9.0E-1 -23.45E-67
        '''.stripIndent()
    def channel = channelFrom text

    when:
    def tokens = Lexer.newLexer(channel, 128).toList()

    then:
    assert tokens == [
        new Token.Number(1, 1, 0e+1D),
        new Token.Number(1, 6, 23e+45D),
        new Token.Number(1, 13, -6e+7D),
        new Token.Number(1, 19, -89e+1D),
        new Token.Number(2, 1, 0e+1D),
        new Token.Number(2, 6, 23e+45D),
        new Token.Number(2, 13, -6e+7D),
        new Token.Number(2, 19, -89e+1D),
        new Token.Number(3, 1, 0e-1D),
        new Token.Number(3, 6, 23e-45D),
        new Token.Number(3, 13, -6e-7D),
        new Token.Number(3, 19, -89e-1D),
        new Token.Number(4, 1, 0e-1D),
        new Token.Number(4, 6, 23e-45D),
        new Token.Number(4, 13, -6e-7D),
        new Token.Number(4, 19, -89e-1D),
        new Token.Number(5, 1, 0.1e+2D),
        new Token.Number(5, 8, 34.56e+78D),
        new Token.Number(5, 18, -9.0e+1D),
        new Token.Number(5, 26, -23.45e+67D),
        new Token.Number(6, 1, 0.1e+2D),
        new Token.Number(6, 8, 34.56e+78D),
        new Token.Number(6, 18, -9.0e+1D),
        new Token.Number(6, 26, -23.45e+67D),
        new Token.Number(7, 1, 0.1e-2D),
        new Token.Number(7, 8, 34.56e-78D),
        new Token.Number(7, 18, -9.0e-1D),
        new Token.Number(7, 26, -23.45e-67D),
        new Token.Number(8, 1, 0.1e-2D),
        new Token.Number(8, 8, 34.56e-78D),
        new Token.Number(8, 18, -9.0e-1D),
        new Token.Number(8, 26, -23.45e-67D),
    ]

    cleanup:
    channel?.close()
  }

  def 'throw exception when minus sign is not followed by a digit'() {
    given:
    def text = '-'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
    assert e.line == 1
    assert e.column == 2

    cleanup:
    channel?.close()
  }

  def 'throw exception when decimal point is not followed by a digit'() {
    given:
    def text = '1.'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
    assert e.line == 1
    assert e.column == 3

    cleanup:
    channel?.close()
  }

  def "throw exception when 'e' is not followed by a digit"() {
    given:
    def text = '1e'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
    assert e.line == 1
    assert e.column == 3

    cleanup:
    channel?.close()
  }

  def "throw exception when 'e+' is not followed by a digit"() {
    given:
    def text = '1e+'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
    assert e.line == 1
    assert e.column == 4

    cleanup:
    channel?.close()
  }

  def "throw exception when 'e-' is not followed by a digit"() {
    given:
    def text = '1e-'
    def channel = channelFrom text

    when:
    Lexer.newLexer(channel, 128).toList()

    then:
    def e = thrown(ParserException)
    assert e.type == ParserException.Type.UNEXPECTED_EOF
    assert e.line == 1
    assert e.column == 4

    cleanup:
    channel?.close()
  }

  private static channelFrom(String s) {
    channelFrom(s.getBytes(StandardCharsets.UTF_8))
  }

  private static channelFrom(byte[] bytes) {
    Channels.newChannel(new ByteArrayInputStream(bytes))
  }
}
