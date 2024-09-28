// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON number of floating-point type.
 *
 * @param value the floating-point number represented by this JSON number.
 */
public record JsonFloat(double value) implements JsonValue, Comparable<JsonFloat> {

  @Override
  public int compareTo(@NonNull JsonFloat o) {
    return Double.compare(value, o.value);
  }

  @Override
  @NonNull
  public String toString() {
    return Double.toString(value);
  }
}
