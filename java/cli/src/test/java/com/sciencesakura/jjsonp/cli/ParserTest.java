// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ParserTest {

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t", "\n", "\r", " \t\n\r"})
  void ignoreBlanks(String blank) {
    var parser = new Parser(blank);
    assertThat(parser).toIterable().isEmpty();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 23, 456})
  void parseGetElement(int index) {
    var input = "[%d]".formatted(index);
    var parser = new Parser(input);
    assertThat(parser).toIterable().containsExactly(
        new Instruction.GetElement(index)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "abc"})
  void parseGetMember_1(String name) {
    var input = ".%s".formatted(name);
    var parser = new Parser(input);
    assertThat(parser).toIterable().containsExactly(
        new Instruction.GetMember(name)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "abc"})
  void parseGetMember_2(String name) {
    var input = ".\"%s\"".formatted(name);
    var parser = new Parser(input);
    assertThat(parser).toIterable().containsExactly(
        new Instruction.GetMember(name)
    );
  }

  @Test
  void parseInstruction_1() {
    var input = """
        [0][1].foo.bar[2].baz."qux"[3]."quux"."corge".grault
        """;
    var parser = new Parser(input);
    assertThat(parser).toIterable().containsExactly(
        new Instruction.GetElement(0),
        new Instruction.GetElement(1),
        new Instruction.GetMember("foo"),
        new Instruction.GetMember("bar"),
        new Instruction.GetElement(2),
        new Instruction.GetMember("baz"),
        new Instruction.GetMember("qux"),
        new Instruction.GetElement(3),
        new Instruction.GetMember("quux"),
        new Instruction.GetMember("corge"),
        new Instruction.GetMember("grault")
    );
  }

  @Test
  void parseInstruction_2() {
    var input = """
        [ 0 ] [ 1 ] . foo . bar [ 2 ]
        . baz . "qux" [ 3 ] . "quux" .
        "corge" . grault
        """;
    var parser = new Parser(input);
    assertThat(parser).toIterable().containsExactly(
        new Instruction.GetElement(0),
        new Instruction.GetElement(1),
        new Instruction.GetMember("foo"),
        new Instruction.GetMember("bar"),
        new Instruction.GetElement(2),
        new Instruction.GetMember("baz"),
        new Instruction.GetMember("qux"),
        new Instruction.GetElement(3),
        new Instruction.GetMember("quux"),
        new Instruction.GetMember("corge"),
        new Instruction.GetMember("grault")
    );
  }
}
