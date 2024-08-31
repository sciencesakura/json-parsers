// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import static java.util.stream.Collectors.joining;

import java.io.Serial;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON array.
 */
public final class JsonArray implements JsonContainer<List<JsonValue<?>>>, Iterable<JsonValue<?>> {

  @Serial
  private static final long serialVersionUID = 1L;

  public static final JsonArray EMPTY = new JsonArray(Collections.emptyList());

  private final List<JsonValue<?>> elements;

  JsonArray(List<JsonValue<?>> elements) {
    this.elements = Collections.unmodifiableList(elements);
  }

  /**
   * Returns the element at the specified index.
   *
   * @param index the index of the element to return.
   * @return the element at the specified index.
   * @throws IndexOutOfBoundsException if the index is out of range.
   */
  @NonNull
  public JsonValue<?> get(int index) {
    return elements.get(index);
  }

  @Override
  public int size() {
    return elements.size();
  }

  @Override
  @NonNull
  public List<JsonValue<?>> value() {
    return elements;
  }

  @Override
  @NonNull
  public Iterator<JsonValue<?>> iterator() {
    return elements.iterator();
  }

  /**
   * Returns a sequential {@code Stream} over the elements in this array.
   *
   * @return a sequential {@code Stream} over the elements in this array.
   */
  @NonNull
  public Stream<JsonValue<?>> stream() {
    return elements.stream();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof JsonArray that) {
      return elements.equals(that.elements);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return elements.hashCode();
  }

  @Override
  public String toString() {
    return stream().map(JsonValue::toString).collect(joining(",", "[", "]"));
  }

  @Override
  @NonNull
  public String toPrettyString() {
    return toPrettyString(0);
  }

  String toPrettyString(int level) {
    if (isEmpty()) {
      return "[]";
    }
    var nextLevel = level + 1;
    var nextIndent = Strings.INDENT.repeat(nextLevel);
    var s = new StringBuilder().append('[');
    for (var e : this) {
      s.append('\n').append(nextIndent);
      switch (e) {
        case JsonArray a -> s.append(a.toPrettyString(nextLevel));
        case JsonObject o -> s.append(o.toPrettyString(nextLevel));
        default -> s.append(e.toPrettyString());
      }
      s.append(',');
    }
    return s.deleteCharAt(s.length() - 1).append('\n').append(Strings.INDENT.repeat(level)).append(']').toString();
  }
}
