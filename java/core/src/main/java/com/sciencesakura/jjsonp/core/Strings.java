// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import org.jspecify.annotations.NonNull;

/**
 * Provides static methods for strings.
 */
public final class Strings {

  private Strings() {
  }

  /**
   * Converts the given string to a double-quoted string.
   *
   * @param s the string.
   * @return the double-quoted string.
   */
  @NonNull
  public static String toQuoted(@NonNull String s) {
    var str = new StringBuilder("\"");
    for (char c : s.toCharArray()) {
      switch (c) {
        case '"' -> str.append("\\\"");
        case '\\' -> str.append("\\\\");
        case '\b' -> str.append("\\b");
        case '\f' -> str.append("\\f");
        case '\n' -> str.append("\\n");
        case '\r' -> str.append("\\r");
        case '\t' -> str.append("\\t");
        default -> str.append(c);
      }
    }
    return str.append('"').toString();
  }
}
