// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp;

import static com.sciencesakura.jjsonp.TestFunctions.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LexerTest {

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t", "\n", "\r", " \t\n\r"})
  void ignoreWhitespaces(String input) {
    var lexer = new Lexer(input);
    var actual = toList(lexer);
    assertThat(actual).isEmpty();
  }

  @Test
  void recognizeSymbols() {
    var input = ".[]";
    var lexer = new Lexer(input);
    var actual = toList(lexer);
    assertThat(actual).containsExactly(
        new Token.Period(1),
        new Token.LeftBracket(2),
        new Token.RightBracket(3)
    );
  }

  @Test
  void recognizeQuotedStrings() {
    var input = """
        "foo bar" "\\b\\f\\n\\r\\t\\\\"
        """;
    var lexer = new Lexer(input);
    var actual = toList(lexer);
    assertThat(actual).containsExactly(
        new Token.String(1, "foo bar"),
        new Token.String(11, "\b\f\n\r\t\\")
    );
  }

  @Test
  void recognizeStrings() {
    var input = "Hello Olá こんにちは 你好 안녕하세요 👋👋🏻👋🏼👋🏽👋🏾👋🏿";
    var lexer = new Lexer(input);
    var actual = toList(lexer);
    assertThat(actual).containsExactly(
        new Token.String(1, "Hello"),
        new Token.String(7, "Olá"),
        new Token.String(11, "こんにちは"),
        new Token.String(17, "你好"),
        new Token.String(20, "안녕하세요"),
        new Token.String(26, "👋👋🏻👋🏼👋🏽👋🏾👋🏿")
    );
  }

  @Test
  void ignoreControlCharactersInString() {
    var input = """
        \u0000A\u0001B\u0002C\u0003 \u0004"\u0005D\u0006E\u0007F\u0008"
        """;
    var lexer = new Lexer(input);
    var actual = toList(lexer);
    assertThat(actual).containsExactly(
        new Token.String(2, "ABC"),
        new Token.String(10, "DEF")
    );
  }

  @Test
  void throwExceptionForUnterminatedString() {
    var input = "\"foo";
    var lexer = new Lexer(input);
    assertThatThrownBy(() -> toList(lexer)).hasMessage("Unexpected end of expression at position at 5")
        .asInstanceOf(throwable(InvalidExpressionException.class))
        .satisfies(e -> assertThat(e.getPos()).isEqualTo(5));
  }

  @Test
  void recognizeIntegers() {
    var input = "1 234";
    var lexer = new Lexer(input);
    var actual = toList(lexer);
    assertThat(actual).containsExactly(
        new Token.Integer(1, 1),
        new Token.Integer(3, 234)
    );
  }
}
