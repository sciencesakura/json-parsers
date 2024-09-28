// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import org.jspecify.annotations.NonNull;

/**
 * Represents a JSON null.
 */
public enum JsonNull implements JsonValue {

  INSTANCE;

  @Override
  @NonNull
  public String toString() {
    return "null";
  }
}
