// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import static com.sciencesakura.jjsonp.core.TestFunctions.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LexerTest {

  @ParameterizedTest
  @EmptySource
  @ValueSource(strings = {" ", "\t", "\n", "\r", " \t\n\r"})
  void ignoreBlanks(String input) throws IOException {
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      var actual = toList(lexer);
      assertThat(actual).isEmpty();
    }
  }

  @Test
  void recognizeSymbols() throws IOException {
    var input = "[]{}:,";
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      var actual = toList(lexer);
      assertThat(actual).containsExactly(
          new Token.LeftBracket(1, 1),
          new Token.RightBracket(1, 2),
          new Token.LeftCurly(1, 3),
          new Token.RightCurly(1, 4),
          new Token.Colon(1, 5),
          new Token.Comma(1, 6)
      );
    }
  }

  @Test
  void throwExceptionForUnknownSymbol() throws IOException {
    var input = "[]();,";
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_CHARACTER);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(3);
          });
    }
  }

  @Test
  void recognizeKeywords() throws IOException {
    var input = "true false null";
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      var actual = toList(lexer);
      assertThat(actual).containsExactly(
          new Token.True(1, 1),
          new Token.False(1, 6),
          new Token.Null(1, 12)
      );
    }
  }

  @Test
  void throwExceptionForUnknownKeyword() throws IOException {
    var input = "true false nil";
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNKNOWN_TOKEN);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(12);
          });
    }
  }

  @Test
  void recognizeStrings() throws IOException {
    var input = """
        "Hello" "Olá" "こんにちは" "你好" "안녕하세요" "👋👋🏻👋🏼👋🏽👋🏾👋🏿" ""
        """;
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      var actual = toList(lexer);
      assertThat(actual).containsExactly(
          new Token.String(1, 1, "Hello"),
          new Token.String(1, 9, "Olá"),
          new Token.String(1, 15, "こんにちは"),
          new Token.String(1, 23, "你好"),
          new Token.String(1, 28, "안녕하세요"),
          new Token.String(1, 36, "👋👋🏻👋🏼👋🏽👋🏾👋🏿"),
          new Token.String(1, 50, "")
      );
    }
  }

  @Test
  void recognizeStringsContainingEscapeSequences() throws IOException {
    var input = """
        "\\"" "\\\\" "\\/" "\\b" "\\f" "\\n" "\\r" "\\t"
        """;
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      var actual = toList(lexer);
      assertThat(actual).containsExactly(
          new Token.String(1, 1, "\""),
          new Token.String(1, 6, "\\"),
          new Token.String(1, 11, "/"),
          new Token.String(1, 16, "\b"),
          new Token.String(1, 21, "\f"),
          new Token.String(1, 26, "\n"),
          new Token.String(1, 31, "\r"),
          new Token.String(1, 36, "\t")
      );
    }
  }

  @Test
  void recognizeStringsContainingUnicodeSequences() throws IOException {
    var input = """
        "\\u0048\\u0065\\u006C\\u006C\\u006F"
        "\\u004F\\u006C\\u00E1"
        "\\u3053\\u3093\\u306B\\u3061\\u306F"
        "\\u4F60\\u597D"
        "\\uC548\\uB155\\uD558\\uC138\\uC694"
        "\\uD83D\\uDC4B\\uD83D\\uDC4B\\uD83C\\uDFFB\\uD83D\\uDC4B\\uD83C\\uDFFC\\uD83D\\uDC4B\\uD83C\\uDFFD\\uD83D\\uDC4B\\uD83C\\uDFFE\\uD83D\\uDC4B\\uD83C\\uDFFF"
        """;
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      var actual = toList(lexer);
      assertThat(actual).containsExactly(
          new Token.String(1, 1, "Hello"),
          new Token.String(2, 1, "Olá"),
          new Token.String(3, 1, "こんにちは"),
          new Token.String(4, 1, "你好"),
          new Token.String(5, 1, "안녕하세요"),
          new Token.String(6, 1, "👋👋🏻👋🏼👋🏽👋🏾👋🏿")
      );
    }
  }

  @Test
  void recognizeStringsContainingInvalidUTF8ByteSequence() throws IOException {
    var input = new int[]{
        0x22, 0x61, 0x80, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xC0, 0x40, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xE0, 0x40, 0x80, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xE0, 0x80, 0x40, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xF0, 0x40, 0x80, 0x80, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xF0, 0x80, 0x40, 0x80, 0x62, 0x22, 0x0A,
        0x22, 0x61, 0xF0, 0x80, 0x80, 0x40, 0x62, 0x22, 0x0A,
    };
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      var actual = toList(lexer);
      assertThat(actual).containsExactly(
          new Token.String(1, 1, "a�b"),
          new Token.String(2, 1, "a�@b"),
          new Token.String(3, 1, "a�@�b"),
          new Token.String(4, 1, "a��@b"),
          new Token.String(5, 1, "a�@��b"),
          new Token.String(6, 1, "a��@�b"),
          new Token.String(7, 1, "a���@b")
      );
    }
  }

  @Test
  void throwExceptionForUnterminatedString_1() throws IOException {
    var input = "\"";
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(2);
          });
    }
  }

  @Test
  void throwExceptionForUnterminatedString_2() throws IOException {
    var input = "\"foo";
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(5);
          });
    }
  }

  @Test
  void throwExceptionForStringContainingControlCharacter() throws IOException {
    var input = """
        "Hello\tWorld"
        """;
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_CHARACTER);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(7);
          });
    }
  }

  @Test
  void throwExceptionForStringContainingInvalidEscapeSequence() throws IOException {
    var input = """
        "\\x"
        """;
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_CHARACTER);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(3);
          });
    }
  }

  @Test
  void throwExceptionForStringContainingInvalidUnicodeEscapeSequence_1() throws IOException {
    var input = """
        "\\u000G"
        """;
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_CHARACTER);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(7);
          });
    }
  }

  @Test
  void throwExceptionForStringContainingInvalidUnicodeEscapeSequence_2() throws IOException {
    var input = """
        "\\u001"
        """;
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_CHARACTER);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(7);
          });
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "234", "-5", "-678"})
  void recognizeIntegerNumbers(String input) throws IOException {
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      var actual = toList(lexer);
      assertThat(actual).containsExactly(
          new Token.Integer(1, 1, Long.parseLong(input))
      );
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"0.1", "23.45", "-6.7", "-89.01"})
  void recognizeFloatingPointNumbersWrittenInDecimalNotation(String input) throws IOException {
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      var actual = toList(lexer);
      assertThat(actual).containsExactly(
          new Token.Float(1, 1, Double.parseDouble(input))
      );
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "0e1", "23e45", "-6e7", "-89e01",
      "0E1", "23E45", "-6E7", "-89E01",
      "0.1e2", "34.56e78", "-9.0e1", "-23.45e67",
      "0.1E2", "34.56E78", "-9.0E1", "-23.45E67",
  })
  void recognizeFloatingPointNumbersWrittenInENotation(String input) throws IOException {
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      var actual = toList(lexer);
      assertThat(actual).containsExactly(
          new Token.Float(1, 1, Double.parseDouble(input))
      );
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "0e+1", "23e+45", "-6e+7", "-89e+01",
      "0E+1", "23E+45", "-6E+7", "-89E+01",
      "0e-1", "23e-45", "-6e-7", "-89e-01",
      "0E-1", "23E-45", "-6E-7", "-89E-01",
      "0.1e+2", "34.56e+78", "-9.0e+1", "-23.45e+67",
      "0.1E+2", "34.56E+78", "-9.0E+1", "-23.45E+67",
      "0.1e-2", "34.56e-78", "-9.0e-1", "-23.45e-67",
      "0.1E-2", "34.56E-78", "-9.0E-1", "-23.45E-67"
  })
  void recognizeFloatingPointNumbersWrittenInSignedENotation(String input) throws IOException {
    try (var ch = channelFrom(input)) {
      var lexer = Lexer.newLexer(ch, 128);
      var actual = toList(lexer);
      assertThat(actual).containsExactly(
          new Token.Float(1, 1, Double.parseDouble(input))
      );
    }
  }

  @Test
  void throwExceptionWhenMinusSignIsNotFollowedByDigit() throws IOException {
    try (var ch = channelFrom("-")) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(2);
          });
    }
  }

  @Test
  void throwExceptionWhenDecimalPointIsNotFollowedByDigit() throws IOException {
    try (var ch = channelFrom("0.")) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(3);
          });
    }
  }

  @Test
  void throwExceptionWhenEIsNotFollowedByDigit() throws IOException {
    try (var ch = channelFrom("0e")) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(3);
          });
    }
  }

  @Test
  void throwExceptionWhenSignedEIsNotFollowedByDigit() throws IOException {
    try (var ch = channelFrom("0e+")) {
      var lexer = Lexer.newLexer(ch, 128);
      assertThatThrownBy(() -> toList(lexer)).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(4);
          });
    }
  }

  private static ReadableByteChannel channelFrom(String input) {
    return channelFrom(input.getBytes(StandardCharsets.UTF_8));
  }

  private static ReadableByteChannel channelFrom(int[] input) {
    var bytes = new byte[input.length];
    for (var i = 0; i < input.length; i++) {
      bytes[i] = (byte) input[i];
    }
    return channelFrom(bytes);
  }

  private static ReadableByteChannel channelFrom(byte[] input) {
    return Channels.newChannel(new ByteArrayInputStream(input));
  }
}
