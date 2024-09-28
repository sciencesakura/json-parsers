// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON string.
 *
 * @param value the string value represented by this JSON string.
 */
public record JsonString(@NonNull String value) implements JsonValue, Comparable<JsonString> {

  @Override
  public int compareTo(@NonNull JsonString o) {
    return value.compareTo(o.value);
  }

  @Override
  @NonNull
  public String toString() {
    return Strings.toQuoted(value);
  }
}
