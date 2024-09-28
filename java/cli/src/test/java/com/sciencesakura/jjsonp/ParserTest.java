// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ParserTest {

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t", "\n", "\r", " \t\n\r"})
  void ignoreWhitespaces(String input) {
    var actual = Parser.parse(input);
    assertThat(actual).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 23, 456})
  void parseBracketOperatorWithIntegerOperand(int index) {
    var input = "[%d]".formatted(index);
    var actual = Parser.parse(input);
    assertThat(actual).containsExactly(
        new Instruction.GetElement(index)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "abc"})
  void parseBracketOperatorWithStringOperand(String name) {
    var input = "[%s]".formatted(name);
    var actual = Parser.parse(input);
    assertThat(actual).containsExactly(
        new Instruction.GetMember(name)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "abc", "abc def"})
  void parseBracketOperatorWithQuotedStringOperand(String name) {
    var input = "[\"%s\"]".formatted(name);
    var actual = Parser.parse(input);
    assertThat(actual).containsExactly(
        new Instruction.GetMember(name)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "abc"})
  void parsePeriodOperatorWithStringOperand(String name) {
    var input = ".%s".formatted(name);
    var actual = Parser.parse(input);
    assertThat(actual).containsExactly(
        new Instruction.GetMember(name)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "abc", "abc def"})
  void parsePeriodOperatorWithQuotedStringOperand(String name) {
    var input = ".\"%s\"".formatted(name);
    var actual = Parser.parse(input);
    assertThat(actual).containsExactly(
        new Instruction.GetMember(name)
    );
  }

  @Test
  void parseInstructions_1() {
    var input = ".foo.bar";
    var actual = Parser.parse(input);
    assertThat(actual).containsExactly(
        new Instruction.GetMember("foo"),
        new Instruction.GetMember("bar")
    );
  }

  @Test
  void parseInstructions_2() {
    var input = ".foo[42]";
    var actual = Parser.parse(input);
    assertThat(actual).containsExactly(
        new Instruction.GetMember("foo"),
        new Instruction.GetElement(42)
    );
  }

  @Test
  void parseInstructions_3() {
    var input = "[42][43]";
    var actual = Parser.parse(input);
    assertThat(actual).containsExactly(
        new Instruction.GetElement(42),
        new Instruction.GetElement(43)
    );
  }

  @Test
  void parseInstructions_4() {
    var input = "[42].foo";
    var actual = Parser.parse(input);
    assertThat(actual).containsExactly(
        new Instruction.GetElement(42),
        new Instruction.GetMember("foo")
    );
  }

  @Test
  void throwExceptionForInvalidExpression_1() {
    var input = "foo";
    assertThatThrownBy(() -> Parser.parse(input)).asInstanceOf(throwable(InvalidExpressionException.class))
        .satisfies(e -> assertThat(e.getPos()).isEqualTo(1));
  }

  @Test
  void throwExceptionForInvalidExpression_2() {
    var input = ".[1]";
    assertThatThrownBy(() -> Parser.parse(input)).asInstanceOf(throwable(InvalidExpressionException.class))
        .satisfies(e -> assertThat(e.getPos()).isEqualTo(2));
  }

  @Test
  void throwExceptionForInvalidExpression_3() {
    var input = "[.]";
    assertThatThrownBy(() -> Parser.parse(input)).asInstanceOf(throwable(InvalidExpressionException.class))
        .satisfies(e -> assertThat(e.getPos()).isEqualTo(2));
  }

  @Test
  void throwExceptionForInvalidExpression_4() {
    var input = "[foo.bar]";
    assertThatThrownBy(() -> Parser.parse(input)).asInstanceOf(throwable(InvalidExpressionException.class))
        .satisfies(e -> assertThat(e.getPos()).isEqualTo(5));
  }

  @Test
  void throwExceptionForInvalidExpression_5() {
    var input = ".";
    assertThatThrownBy(() -> Parser.parse(input)).asInstanceOf(throwable(InvalidExpressionException.class))
        .satisfies(e -> assertThat(e.getPos()).isEqualTo(2));
  }
}
