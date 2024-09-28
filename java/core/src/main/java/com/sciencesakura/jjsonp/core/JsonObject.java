// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import static java.util.stream.Collectors.joining;

import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SequencedMap;
import java.util.function.BiConsumer;
import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON object.
 */
public final class JsonObject implements JsonValue {

  /** An empty JSON object. */
  public static final JsonObject EMPTY = new JsonObject(Collections.emptySortedMap());

  @Serial
  private static final long serialVersionUID = 1L;

  private final SequencedMap<String, JsonValue> pairs;

  /**
   * Constructs a JSON object with the specified name-value pairs.
   *
   * @param pairs the name-value pairs represented by this JSON object.
   */
  public JsonObject(@NonNull SequencedMap<String, ? extends JsonValue> pairs) {
    this.pairs = Collections.unmodifiableSequencedMap(pairs);
  }

  /**
   * Returns the value associated with the specified name.
   *
   * @param name the name of the value to return.
   * @return the value associated with the specified name.
   * @throws NoSuchElementException if no such member exists.
   */
  @NonNull
  public JsonValue get(@NonNull String name) {
    var v = pairs.get(name);
    if (v == null) {
      throw new NoSuchElementException("No such member: " + name);
    }
    return v;
  }

  /**
   * Returns {@code true} if this object contains no elements.
   *
   * @return {@code true} if this object contains no elements.
   */
  public boolean isEmpty() {
    return pairs.isEmpty();
  }

  /**
   * Returns the number of elements in this object.
   *
   * @return the number of elements in this object.
   */
  public int size() {
    return pairs.size();
  }

  /**
   * Returns all names of members in this object.
   *
   * @return all names of members in this object.
   */
  @NonNull
  public List<String> names() {
    return List.copyOf(pairs.keySet());
  }

  /**
   * Performs the given action for each member in this object.
   *
   * @param action the action to be performed for each member.
   */
  public void forEach(@NonNull BiConsumer<? super String, ? super JsonValue> action) {
    pairs.forEach(action);
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this || (obj instanceof JsonObject o && pairs.equals(o.pairs));
  }

  @Override
  public int hashCode() {
    return pairs.hashCode();
  }

  @Override
  @NonNull
  public String toString() {
    return pairs.entrySet().stream().map(e -> Strings.toQuoted(e.getKey()) + ':' + e.getValue())
        .collect(joining(",", "{", "}"));
  }
}
