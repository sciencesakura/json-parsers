// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp;

import static com.sciencesakura.jjsonp.TestFunctions.sequencedMapOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sciencesakura.jjsonp.core.JsonArray;
import com.sciencesakura.jjsonp.core.JsonInteger;
import com.sciencesakura.jjsonp.core.JsonObject;
import com.sciencesakura.jjsonp.core.JsonString;
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
  })
  void getElementFromArray(int index, String expected) {
    // ["foo", "bar", "baz"]
    var json = new JsonArray(
        new JsonString("foo"),
        new JsonString("bar"),
        new JsonString("baz")
    );
    var inst = List.of(new Instruction.GetElement(index));
    var actual = Evaluator.eval(inst, json);
    assertThat(actual).isEqualTo(new JsonString(expected));
  }

  @ParameterizedTest
  @CsvSource({
      "foo, 1",
      "bar, 2",
      "baz, 3",
  })
  void getMemberFromObject(String name, long expected) {
    // {"foo": 1, "bar": 2, "baz": 3}
    var json = new JsonObject(sequencedMapOf(
        "foo", new JsonInteger(1),
        "bar", new JsonInteger(2),
        "baz", new JsonInteger(3)
    ));
    var inst = List.of(new Instruction.GetMember(name));
    var actual = Evaluator.eval(inst, json);
    assertThat(actual).isEqualTo(new JsonInteger(expected));
  }

  @Test
  void getElementFromNestedArray() {
    // ["foo", ["bar", "baz"]]
    var json = new JsonArray(
        new JsonString("foo"),
        new JsonArray(
            new JsonString("bar"),
            new JsonString("baz")
        )
    );
    var inst = List.of(new Instruction.GetElement(1), new Instruction.GetElement(0));
    var actual = Evaluator.eval(inst, json);
    assertThat(actual).isEqualTo(new JsonString("bar"));
  }

  @Test
  void getMemberFromNestedObject() {
    // {"foo": {"bar": 42}}
    var json = new JsonObject(sequencedMapOf(
        "foo", new JsonObject(sequencedMapOf("bar", new JsonInteger(42)))
    ));
    var inst = List.of(new Instruction.GetMember("foo"), new Instruction.GetMember("bar"));
    var actual = Evaluator.eval(inst, json);
    assertThat(actual).isEqualTo(new JsonInteger(42));
  }

  @Test
  void throwExceptionForIndexAccessFromNonArray() {
    // {"foo": "bar"}
    var json = new JsonObject(sequencedMapOf("foo", new JsonString("bar")));
    var inst = List.of(new Instruction.GetElement(0));
    assertThatThrownBy(() -> Evaluator.eval(inst, json))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void throwExceptionForMemberAccessFromNonObject() {
    // ["foo", "bar"]
    var json = new JsonArray(new JsonString("foo"), new JsonString("bar"));
    var inst = List.of(new Instruction.GetMember("foo"));
    assertThatThrownBy(() -> Evaluator.eval(inst, json))
        .isInstanceOf(IllegalStateException.class);
  }
}
