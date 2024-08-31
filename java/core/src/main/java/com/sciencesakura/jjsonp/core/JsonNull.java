// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

/**
 * Represents a JSON null value.
 */
public enum JsonNull implements JsonValue<Void> {

  INSTANCE;

  @Override
  public Void value() {
    return null;
  }

  @Override
  public String toString() {
    return "null";
  }
}
