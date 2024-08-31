// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.Serializable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents a JSON value.
 *
 * @param <V> the type of the Java value representation of this JSON value.
 */
public sealed interface JsonValue<V> extends Serializable permits JsonBool, JsonContainer, JsonFloat, JsonInteger, JsonNull, JsonString {

  /**
   * Returns the Java value representation of this JSON value.
   *
   * @return the Java value representation of this JSON value.
   */
  @Nullable
  V value();

  /**
   * Returns the pretty string representation of this JSON value.
   *
   * @return the pretty string representation of this JSON value.
   */
  @NonNull
  default String toPrettyString() {
    return toString();
  }
}
