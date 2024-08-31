// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.cli;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.sciencesakura.jjsonp.core.Jsons;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EvaluatorTest {

  @ParameterizedTest
  @CsvSource({
      "0, foo",
      "1, bar",
      "2, baz",
      "3, qux",
  })
  void evalGetElement(int index, String expected) {
    var json = """
        ["foo", "bar", "baz", "qux"]
        """;
    var inst = List.of(new Instruction.GetElement(index));
    var actual = Evaluator.eval(inst.iterator(), Jsons.parse(json).orElseThrow());
    assertThatJson(actual.toString()).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({
      "foo, 1",
      "bar, 2",
      "baz, 3",
      "qux, 4",
  })
  void evalGetMember(String name, long expected) {
    var json = """
        {"foo": 1, "bar": 2, "baz": 3, "qux": 4}
        """;
    var inst = List.of(new Instruction.GetMember(name));
    var actual = Evaluator.eval(inst.iterator(), Jsons.parse(json).orElseThrow());
    assertThatJson(actual.toString()).isEqualTo(expected);
  }

  @Test
  void evalInstructions() {
    var json = """
        {
          "foo": [
            [{"bar": {"baz": [1, 2, 3]}}]
          ]
        }
        """;
    var inst = List.of(
        new Instruction.GetMember("foo"),
        new Instruction.GetElement(0),
        new Instruction.GetElement(0),
        new Instruction.GetMember("bar"),
        new Instruction.GetMember("baz")
    );
    var actual = Evaluator.eval(inst.iterator(), Jsons.parse(json).orElseThrow());
    assertThatJson(actual.toString()).isEqualTo("[1, 2, 3]");
  }
}
