// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.Serial;
import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON floating-point number value.
 */
public final class JsonFloat implements JsonValue<Double>, Comparable<JsonFloat> {

  @Serial
  private static final long serialVersionUID = 1L;

  private final double value;

  JsonFloat(double value) {
    this.value = value;
  }

  @Override
  @NonNull
  public Double value() {
    return value;
  }

  @Override
  public int compareTo(JsonFloat o) {
    return Double.compare(value, o.value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof JsonFloat that) {
      return Double.doubleToLongBits(value) == Double.doubleToLongBits(that.value);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Double.hashCode(value);
  }

  @Override
  public String toString() {
    return Double.toString(value);
  }
}
