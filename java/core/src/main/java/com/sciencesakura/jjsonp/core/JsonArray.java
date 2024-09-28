// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import static java.util.stream.Collectors.joining;

import java.io.Serial;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON array.
 */
public final class JsonArray implements JsonValue, Iterable<JsonValue> {

  /** An empty JSON array. */
  public static final JsonArray EMPTY = new JsonArray();

  @Serial
  private static final long serialVersionUID = 1L;

  private final List<JsonValue> elements;

  /**
   * Constructs a JSON array with the specified elements.
   *
   * @param elements the JSON value list represented by this JSON array.
   */
  public JsonArray(@NonNull List<? extends JsonValue> elements) {
    this.elements = List.copyOf(elements);
  }

  /**
   * Constructs a JSON array with the specified elements.
   *
   * @param elements the JSON value array represented by this JSON array.
   */
  public JsonArray(@NonNull JsonValue... elements) {
    this.elements = List.of(elements);
  }

  /**
   * Returns the element at the specified index.
   *
   * @param index the index of the element to return.
   * @return the element at the specified index.
   * @throws IndexOutOfBoundsException if the index is out of range.
   */
  @NonNull
  public JsonValue get(int index) {
    return elements.get(index);
  }

  /**
   * Returns {@code true} if this array contains no elements.
   *
   * @return {@code true} if this array contains no elements.
   */
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  /**
   * Returns the number of elements in this array.
   *
   * @return the number of elements in this array.
   */
  public int size() {
    return elements.size();
  }

  @Override
  @NonNull
  public Iterator<JsonValue> iterator() {
    return elements.iterator();
  }

  /**
   * Returns a sequential {@code Stream} with this array as its source.
   *
   * @return a sequential {@code Stream} with this array as its source.
   */
  @NonNull
  public Stream<JsonValue> stream() {
    return elements.stream();
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this || (obj instanceof JsonArray a && elements.equals(a.elements));
  }

  @Override
  public int hashCode() {
    return elements.hashCode();
  }

  @Override
  @NonNull
  public String toString() {
    return stream().map(JsonValue::toString).collect(joining(",", "[", "]"));
  }
}
