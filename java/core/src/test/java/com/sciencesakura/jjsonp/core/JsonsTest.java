// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JsonsTest {

  private static final Logger LOG = LoggerFactory.getLogger(JsonsTest.class);

  @ParameterizedTest
  @ValueSource(strings = {
      "true",
      "false",
      "null",
      "\"Hello, world!\"",
      "42",
      "3.14",
      "[]",
      "[\"Hello\", \"Olá\", \"こんにちは\", \"你好\", \"안녕하세요\", \"😀😃😄\"]",
      "{}",
      "{\"foo\": \"bar\", \"baz\": \"qux\"}"
  })
  void parseJsonFromString(String json) {
    var actual = Jsons.parse(json);
    assertThat(actual).hasValueSatisfying(it -> {
      LOG.info("{}", it);
      assertThatJson(it.toString()).isEqualTo(json);
      assertThatJson(it.toPrettyString()).isEqualTo(json);
    });
  }

  @Test
  void parseNestedJsonArray() {
    var json = """
        [
          1, 2, 3,
          [], [4], [5, 6, 7],
          {}, {"foo": 1}, {"bar": 2, "baz": 3, "qux": 4}
        ]
        """;
    var actual = Jsons.parse(json);
    assertThat(actual).hasValueSatisfying(it -> {
      LOG.info("{}", it);
      assertThatJson(it.toString()).isEqualTo(json);
      assertThatJson(it.toPrettyString()).isEqualTo(json);
    });
  }

  @Test
  void parseNestedObject() {
    var json = """
        {
          "foo": 1,
          "bar": 2,
          "baz": {},
          "qux": {"a": 1},
          "quux": {"b": 2, "c": 3, "d": 4},
          "corge": [],
          "grault": [1],
          "garply": [2, 3, 4]
        }
        """;
    var actual = Jsons.parse(json);
    assertThat(actual).hasValueSatisfying(it -> {
      LOG.info("{}", it);
      assertThatJson(it.toString()).isEqualTo(json);
      assertThatJson(it.toPrettyString()).isEqualTo(json);
    });
  }
}
