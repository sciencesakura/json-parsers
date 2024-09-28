// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON boolean.
 */
public enum JsonBool implements JsonValue {

  FALSE,

  TRUE;

  /**
   * Returns the boolean value represented by this JSON boolean.
   *
   * @return the boolean value represented by this JSON boolean.
   */
  public boolean value() {
    return this == TRUE;
  }

  @Override
  @NonNull
  public String toString() {
    return String.valueOf(this == TRUE);
  }
}
