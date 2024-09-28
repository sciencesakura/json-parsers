// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import static com.sciencesakura.jjsonp.core.TestFunctions.sequencedMapOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class JsonValueTest {

  @Test
  void testOrderOfBoolean() {
    assertThat(JsonBool.TRUE).isGreaterThan(JsonBool.FALSE);
    assertThat(JsonBool.FALSE).isLessThan(JsonBool.TRUE);
  }

  @Test
  void testOrderOfInteger() {
    assertThat(new JsonInteger(1)).isGreaterThan(new JsonInteger(0));
    assertThat(new JsonInteger(0)).isLessThan(new JsonInteger(1));
  }

  @Test
  void testOrderOfFloat() {
    assertThat(new JsonFloat(1.0)).isGreaterThan(new JsonFloat(0.0));
    assertThat(new JsonFloat(0.0)).isLessThan(new JsonFloat(1.0));
  }

  @Test
  void testOrderOfString() {
    assertThat(new JsonString("B")).isGreaterThan(new JsonString("A"));
    assertThat(new JsonString("A")).isLessThan(new JsonString("B"));
  }

  @Test
  void testAccessByIndex() {
    var elements = List.of(
        new JsonString("foo"),
        new JsonInteger(42),
        new JsonFloat(3.14)
    );
    var array = new JsonArray(elements);
    assertThat(array.get(0)).isEqualTo(new JsonString("foo"));
    assertThat(array.get(1)).isEqualTo(new JsonInteger(42));
    assertThat(array.get(2)).isEqualTo(new JsonFloat(3.14));
    assertThatThrownBy(() -> array.get(3)).isInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  void testForEachOfArray() {
    var elements = List.of(
        new JsonString("foo"),
        new JsonInteger(42),
        new JsonFloat(3.14)
    );
    var array = new JsonArray(elements);
    var actual = new ArrayList<JsonValue>();
    array.forEach(actual::add);
    assertThat(actual).containsExactlyElementsOf(elements);
  }

  @Test
  void testIteratorOfArray() {
    var elements = List.of(
        new JsonString("foo"),
        new JsonInteger(42),
        new JsonFloat(3.14)
    );
    var array = new JsonArray(elements);
    assertThat(array.iterator()).toIterable().containsExactlyElementsOf(elements);
  }

  @Test
  void testStreamOfArray() {
    var elements = List.of(
        new JsonString("foo"),
        new JsonInteger(42),
        new JsonFloat(3.14)
    );
    var array = new JsonArray(elements);
    assertThat(array.stream()).containsExactlyElementsOf(elements);
  }

  @Test
  void testArrayToString() {
    var elements = List.of(
        new JsonString("foo"),
        new JsonInteger(42),
        new JsonFloat(3.14)
    );
    var array = new JsonArray(elements);
    assertThat(array).hasToString("[\"foo\",42,3.14]");
  }

  @Test
  void testAccessByMemberName() {
    var members = sequencedMapOf(
        "a", new JsonString("foo"),
        "b", new JsonInteger(42),
        "c", new JsonFloat(3.14)
    );
    var object = new JsonObject(members);
    assertThat(object.get("a")).isEqualTo(new JsonString("foo"));
    assertThat(object.get("b")).isEqualTo(new JsonInteger(42));
    assertThat(object.get("c")).isEqualTo(new JsonFloat(3.14));
    assertThatThrownBy(() -> object.get("d")).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void testForEachOfObject() {
    var members = sequencedMapOf(
        "a", new JsonString("foo"),
        "b", new JsonInteger(42),
        "c", new JsonFloat(3.14)
    );
    var object = new JsonObject(members);
    var actual = new HashMap<String, JsonValue>();
    object.forEach(actual::put);
    assertThat(actual).containsExactlyEntriesOf(members);
  }

  @Test
  void testObjectToString() {
    var members = sequencedMapOf(
        "a", new JsonString("foo"),
        "b", new JsonInteger(42),
        "c", new JsonFloat(3.14)
    );
    var object = new JsonObject(members);
    assertThat(object).hasToString("{\"a\":\"foo\",\"b\":42,\"c\":3.14}");
  }
}
