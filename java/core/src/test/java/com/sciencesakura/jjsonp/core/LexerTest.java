// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LexerTest {

  private static final Logger LOG = LoggerFactory.getLogger(LexerTest.class);

  @ParameterizedTest
  @EmptySource
  @ValueSource(strings = {" ", "\t", "\n", "\r", " \t\n\r"})
  void ignoreBlanks(String blank) throws IOException {
    try (var ch = stringChannel(blank)) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThat(lexer).toIterable().isEmpty();
    }
  }

  @Test
  void recognizeSymbols() throws IOException {
    try (var ch = stringChannel("[]{}:,")) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThat(lexer).toIterable().containsExactly(
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
  void throwExceptionOnUnknownSymbol() throws IOException {
    try (var ch = stringChannel("[]();,")) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThat(lexer.next()).isEqualTo(new Token.LeftBracket(1, 1));
      assertThat(lexer.next()).isEqualTo(new Token.RightBracket(1, 2));
      assertThatThrownBy(lexer::next).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            LOG.info("thrown exception", e);
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_CHARACTER);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(3);
          });
    }
  }

  @Test
  void recognizeKeywords() throws IOException {
    try (var ch = stringChannel("true false null")) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThat(lexer).toIterable().containsExactly(
          new Token.True(1, 1),
          new Token.False(1, 6),
          new Token.Null(1, 12)
      );
    }
  }

  @Test
  void throwExceptionOnUnknownKeyword() throws IOException {
    try (var ch = stringChannel("true false nil")) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThat(lexer.next()).isEqualTo(new Token.True(1, 1));
      assertThat(lexer.next()).isEqualTo(new Token.False(1, 6));
      assertThatThrownBy(lexer::next).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            LOG.info("thrown exception", e);
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNKNOWN_TOKEN);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(12);
          });
    }
  }

  @Test
  void recognizeStrings() throws IOException {
    var input = """
        "Hello" "Olá" "こんにちは" "你好" "안녕하세요" "😀😃😄"
        """;
    try (var ch = stringChannel(input)) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThat(lexer).toIterable().containsExactly(
          new Token.String("Hello", 1, 1),
          new Token.String("Olá", 1, 9),
          new Token.String("こんにちは", 1, 15),
          new Token.String("你好", 1, 23),
          new Token.String("안녕하세요", 1, 28),
          new Token.String("😀😃😄", 1, 36)
      );
    }
  }

  @Test
  void recognizeEscapeSequences() throws IOException {
    var input = """
        "\\"" "\\\\" "\\/" "\\b" "\\f" "\\n" "\\r" "\\t"
        """;
    try (var ch = stringChannel(input)) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThat(lexer).toIterable().containsExactly(
          new Token.String("\"", 1, 1),
          new Token.String("\\", 1, 6),
          new Token.String("/", 1, 11),
          new Token.String("\b", 1, 16),
          new Token.String("\f", 1, 21),
          new Token.String("\n", 1, 26),
          new Token.String("\r", 1, 31),
          new Token.String("\t", 1, 36)
      );
    }
  }

  @Test
  void recognizeUnicodeEscapeSequences() throws IOException {
    var input = """
        "\\u4F60\\u597D\\u002C\\u0020\\u004A\\u0053\\u004F\\u004E\\u7684\\u4E16\\u754C\\u0021"
        "\\u4f60\\u597d\\u002c\\u0020\\u004a\\u0053\\u004f\\u004e\\u7684\\u4e16\\u754c\\u0021"
        """;
    try (var ch = stringChannel(input)) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThat(lexer).toIterable().containsExactly(
          new Token.String("你好, JSON的世界!", 1, 1),
          new Token.String("你好, JSON的世界!", 2, 1)
      );
    }
  }

  @Test
  void throwExceptionWhenStringEndsUnexpectedly_1() throws IOException {
    var input = "\"";
    try (var ch = stringChannel(input)) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThatThrownBy(lexer::next).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            LOG.info("thrown exception", e);
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(2);
          });
    }
  }

  @Test
  void throwExceptionWhenStringEndsUnexpectedly_2() throws IOException {
    var input = "\"foo";
    try (var ch = stringChannel(input)) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThatThrownBy(lexer::next).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            LOG.info("thrown exception", e);
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(5);
          });
    }
  }

  @Test
  void throwExceptionWhenStringContainsControl() throws IOException {
    var input = """
        "Hello
        World"
        """;
    try (var ch = stringChannel(input)) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThatThrownBy(lexer::next).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            LOG.info("thrown exception", e);
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_CHARACTER);
            assertThat(e.getLine()).isEqualTo(2);
            assertThat(e.getColumn()).isEqualTo(0);
          });
    }
  }

  @Test
  void throwExceptionWhenStringContainsInvalidEscapeSequence() throws IOException {
    var input = """
        "\\x"
        """;
    try (var ch = stringChannel(input)) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThatThrownBy(lexer::next).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            LOG.info("thrown exception", e);
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_CHARACTER);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(3);
          });
    }
  }

  @Test
  void throwExceptionWhenStringContainsInvalidUnicodeEscapeSequence() throws IOException {
    var input = """
        "\\u000G"
        """;
    try (var ch = stringChannel(input)) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThatThrownBy(lexer::next).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            LOG.info("thrown exception", e);
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_CHARACTER);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(7);
          });
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "234", "-5", "-678"})
  void recognizeIntegers(String input) throws IOException {
    try (var ch = stringChannel(input)) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThat(lexer).toIterable().containsExactly(
          new Token.Integer(Long.parseLong(input), 1, 1)
      );
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "0.1", "23.45", "-6.7", "-89.01",
      "0e1", "23e45", "-6e7", "-89e01",
      "0e+1", "23e+45", "-6e+7", "-89e+01",
      "0e-1", "23e-45", "-6e-7", "-89e-01",
      "0.1e2", "34.56e78", "-9.0e1", "-23.45e67",
      "0.1e+2", "34.56e+78", "-9.0e+1", "-23.45e+67",
      "0.1e-2", "34.56e-78", "-9.0e-1", "-23.45e-67",
      "0E1", "23E45", "-6E7", "-89E01",
      "0E+1", "23E+45", "-6E+7", "-89E+01",
      "0E-1", "23E-45", "-6E-7", "-89E-01",
      "0.1E2", "34.56E78", "-9.0E1", "-23.45E67",
      "0.1E+2", "34.56E+78", "-9.0E+1", "-23.45E+67",
      "0.1E-2", "34.56E-78", "-9.0E-1", "-23.45E-67"
  })
  void recognizeFloats(String input) throws IOException {
    try (var ch = stringChannel(input)) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThat(lexer).toIterable().containsExactly(
          new Token.Float(Double.parseDouble(input), 1, 1)
      );
    }
  }

  @Test
  void throwExceptionWhenNumberEndsUnexpectedly_1() throws IOException {
    try (var ch = stringChannel("-")) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThatThrownBy(lexer::next).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            LOG.info("thrown exception", e);
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(2);
          });
    }
  }

  @Test
  void throwExceptionWhenNumberEndsUnexpectedly_2() throws IOException {
    try (var ch = stringChannel("0.")) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThatThrownBy(lexer::next).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            LOG.info("thrown exception", e);
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(3);
          });
    }
  }

  @Test
  void throwExceptionWhenNumberEndsUnexpectedly_3() throws IOException {
    try (var ch = stringChannel("0e")) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThatThrownBy(lexer::next).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            LOG.info("thrown exception", e);
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(3);
          });
    }
  }

  @Test
  void throwExceptionWhenNumberEndsUnexpectedly_4() throws IOException {
    try (var ch = stringChannel("0e+")) {
      var lexer = Lexer.newLexer(ch, 1024);
      assertThatThrownBy(lexer::next).asInstanceOf(throwable(ParserException.class))
          .satisfies(e -> {
            LOG.info("thrown exception", e);
            assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
            assertThat(e.getLine()).isEqualTo(1);
            assertThat(e.getColumn()).isEqualTo(4);
          });
    }
  }

  @Test
  void bufferIsFullAndReload() throws IOException {
    var input = """
        "こんにちは"
        """;
    try (var ch = stringChannel(input)) {
      var lexer = Lexer.newLexer(ch, 4);
      assertThat(lexer).toIterable().containsExactly(
          new Token.String("こんにちは", 1, 1)
      );
    }
  }

  private static ReadableByteChannel stringChannel(String input) {
    return Channels.newChannel(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
  }
}
