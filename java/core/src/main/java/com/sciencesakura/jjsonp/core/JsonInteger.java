// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON number of integer type.
 *
 * @param value the integer number represented by this JSON number.
 */
public record JsonInteger(long value) implements JsonValue, Comparable<JsonInteger> {

  @Override
  public int compareTo(@NonNull JsonInteger o) {
    return Long.compare(value, o.value);
  }

  @Override
  @NonNull
  public String toString() {
    return Long.toString(value);
  }
}
