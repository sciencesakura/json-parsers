// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp;

import static com.sciencesakura.jjsonp.TestFunctions.sequencedMapOf;
import static org.assertj.core.api.Assertions.assertThat;

import com.sciencesakura.jjsonp.core.JsonArray;
import com.sciencesakura.jjsonp.core.JsonBool;
import com.sciencesakura.jjsonp.core.JsonFloat;
import com.sciencesakura.jjsonp.core.JsonInteger;
import com.sciencesakura.jjsonp.core.JsonNull;
import com.sciencesakura.jjsonp.core.JsonObject;
import com.sciencesakura.jjsonp.core.JsonString;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class FormatterTest {

  @Test
  void formatNull() throws IOException {
    var json = JsonNull.INSTANCE;
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("null");
  }

  @Test
  void formatTrue() throws IOException {
    var json = JsonBool.TRUE;
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("true");
  }

  @Test
  void formatFalse() throws IOException {
    var json = JsonBool.FALSE;
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("false");
  }

  @Test
  void formatInteger() throws IOException {
    var json = new JsonInteger(42);
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("42");
  }

  @Test
  void formatFloat() throws IOException {
    var json = new JsonFloat(3.14);
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("3.14");
  }

  @Test
  void formatString() throws IOException {
    var json = new JsonString("Hello, World!");
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("\"Hello, World!\"");
  }

  @Test
  void formatEmptyArray() throws IOException {
    var json = JsonArray.EMPTY;
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("[]");
  }

  @Test
  void formatArrayHavingOneElement() throws IOException {
    var json = new JsonArray(new JsonString("foo"));
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("[\"foo\"]");
  }

  @Test
  void formatArrayHavingTwoElements() throws IOException {
    var json = new JsonArray(new JsonString("foo"), new JsonString("bar"));
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("""
        [
          "foo",
          "bar"
        ]""");
  }

  @Test
  void formatEmptyObject() throws IOException {
    var json = JsonObject.EMPTY;
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("{}");
  }

  @Test
  void formatObjectHavingOneMember() throws IOException {
    var json = new JsonObject(sequencedMapOf("a", new JsonString("foo")));
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("""
        {
          "a": "foo"
        }""");
  }

  @Test
  void formatObjectHavingTwoMembers() throws IOException {
    var json = new JsonObject(sequencedMapOf(
        "a", new JsonString("foo"),
        "b", new JsonInteger(42)
    ));
    var dest = new StringBuilder();
    new Formatter(dest).format(json);
    assertThat(dest).hasToString("""
        {
          "a": "foo",
          "b": 42
        }""");
  }
}
