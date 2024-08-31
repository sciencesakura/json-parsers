// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.Serial;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON object.
 */
public final class JsonObject implements JsonContainer<Map<String, JsonValue<?>>> {

  @Serial
  private static final long serialVersionUID = 1;

  public static final JsonObject EMPTY = new JsonObject(Collections.emptyMap());

  private final Map<String, JsonValue<?>> members;

  JsonObject(Map<String, JsonValue<?>> members) {
    this.members = Collections.unmodifiableMap(members);
  }

  /**
   * Returns the value associated with the specified name.
   *
   * @param name the name of the value to return.
   * @return the value associated with the specified name.
   * @throws NoSuchElementException if no such member exists.
   */
  @NonNull
  public JsonValue<?> get(@NonNull String name) {
    var value = members.get(name);
    if (value == null) {
      throw new NoSuchElementException("No such member: " + name);
    }
    return value;
  }

  @Override
  public int size() {
    return members.size();
  }

  @Override
  @NonNull
  public Map<String, JsonValue<?>> value() {
    return members;
  }

  /**
   * Performs the given action for each member in this object.
   *
   * @param action the action to be performed for each member.
   */
  public void forEach(@NonNull BiConsumer<? super String, ? super JsonValue<?>> action) {
    members.forEach(action);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof JsonObject that) {
      return members.equals(that.members);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return members.hashCode();
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "{}";
    }
    var s = new StringBuilder().append('{');
    forEach((n, v) -> s.append(Strings.toJson(n)).append(':').append(v).append(','));
    return s.deleteCharAt(s.length() - 1).append('}').toString();
  }

  @Override
  @NonNull
  public String toPrettyString() {
    return toPrettyString(0);
  }

  String toPrettyString(int level) {
    if (isEmpty()) {
      return "{}";
    }
    var nextLevel = level + 1;
    var nextIndent = Strings.INDENT.repeat(nextLevel);
    var s = new StringBuilder().append('{');
    forEach((n, v) -> {
      s.append('\n').append(nextIndent).append(Strings.toJson(n)).append(": ");
      switch (v) {
        case JsonArray a -> s.append(a.toPrettyString(nextLevel));
        case JsonObject o -> s.append(o.toPrettyString(nextLevel));
        default -> s.append(v.toPrettyString());
      }
      s.append(',');
    });
    return s.deleteCharAt(s.length() - 1).append('\n').append(Strings.INDENT.repeat(level)).append('}').toString();
  }
}
