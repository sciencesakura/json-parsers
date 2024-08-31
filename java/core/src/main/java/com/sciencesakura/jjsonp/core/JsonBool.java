// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON boolean value.
 */
public enum JsonBool implements JsonValue<Boolean> {

  FALSE,

  TRUE;

  @Override
  @NonNull
  public Boolean value() {
    return this == TRUE;
  }

  @Override
  public String toString() {
    return String.valueOf(this == TRUE);
  }
}
