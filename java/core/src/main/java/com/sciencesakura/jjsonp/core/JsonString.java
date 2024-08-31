// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.Serial;
import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON string value.
 */
public final class JsonString implements JsonValue<String>, Comparable<JsonString> {

  @Serial
  private static final long serialVersionUID = 1L;

  private final String value;

  JsonString(String value) {
    this.value = value;
  }

  @Override
  @NonNull
  public String value() {
    return value;
  }

  @Override
  public int compareTo(JsonString o) {
    return value.compareTo(o.value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof JsonString other) {
      return value.equals(other.value);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return Strings.toJson(value);
  }
}
