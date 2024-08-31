// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.Serial;
import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON integer value.
 */
public final class JsonInteger implements JsonValue<Long>, Comparable<JsonInteger> {

  @Serial
  private static final long serialVersionUID = 1L;

  private final long value;

  JsonInteger(long value) {
    this.value = value;
  }

  @Override
  @NonNull
  public Long value() {
    return value;
  }

  @Override
  public int compareTo(JsonInteger o) {
    return Long.compare(value, o.value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof JsonInteger that) {
      return value == that.value;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(value);
  }

  @Override
  public String toString() {
    return Long.toString(value);
  }
}
