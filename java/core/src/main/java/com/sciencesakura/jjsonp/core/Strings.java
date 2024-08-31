// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.util.function.IntSupplier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A utility class for strings.
 */
public final class Strings {

  public static final String INDENT = "  ";

  public static final int REPLACEMENT_CHAR = '\uFFFD';

  private Strings() {
  }

  /**
   * Returns {@code true} if the given character is alphabetic.
   *
   * @param c the character code point.
   * @return {@code true} if the character is alphabetic.
   */
  public static boolean isAlpha(int c) {
    return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
  }

  /**
   * Returns {@code true} if the given character is a digit.
   *
   * @param c the character code point.
   * @return {@code true} if the character is a digit.
   */
  public static boolean isDigit(int c) {
    return '0' <= c && c <= '9';
  }

  /**
   * Returns {@code true} if the given character is a JSON whitespace character.
   *
   * @param c the character code point.
   * @return {@code true} if the character is a JSON whitespace character.
   */
  public static boolean isWhitespace(int c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
  }

  /**
   * Returns the JSON string representation of the given string.
   *
   * @param s the string to be represented.
   * @return the JSON string representation of the given string.
   */
  @NonNull
  public static String toJson(@Nullable String s) {
    if (s == null) {
      return "null";
    }
    var str = new StringBuilder(s.length() + 2).append('"');
    s.codePoints().forEach(c -> {
      switch (c) {
        case '"' -> str.append("\\\"");
        case '\\' -> str.append("\\\\");
        case '\b' -> str.append("\\b");
        case '\f' -> str.append("\\f");
        case '\n' -> str.append("\\n");
        case '\r' -> str.append("\\r");
        case '\t' -> str.append("\\t");
        default -> str.appendCodePoint(c);
      }
    });
    return str.append('"').toString();
  }

  /**
   * Returns the next UTF-8 character code point from the given source.
   *
   * @param src the source of byte sequence.
   * @return the next UTF-8 character code point, or {@link #REPLACEMENT_CHAR} if the {@code src} supplies an invalid
   *     byte sequence.
   */
  public static int nextUTF8Char(@NonNull IntSupplier src) {
    var b1 = src.getAsInt();
    if (b1 == -1 || (b1 & 0x80) == 0) {
      // U+0000 to U+007F
      return b1;
    }
    var b2 = src.getAsInt();
    if ((b2 & 0xC0) != 0x80) {
      return REPLACEMENT_CHAR;
    }
    if ((b1 & 0xE0) == 0xC0) {
      // U+0080 to U+07FF
      return ((b1 & 0x1F) << 6) | (b2 & 0x3F);
    }
    var b3 = src.getAsInt();
    if ((b3 & 0xC0) != 0x80) {
      return REPLACEMENT_CHAR;
    }
    if ((b1 & 0xF0) == 0xE0) {
      // U+0800 to U+FFFF
      return ((b1 & 0x0F) << 12) | ((b2 & 0x3F) << 6) | (b3 & 0x3F);
    }
    var b4 = src.getAsInt();
    if ((b4 & 0xC0) != 0x80) {
      return REPLACEMENT_CHAR;
    }
    if ((b1 & 0xF8) == 0xF0) {
      // U+10000 to U+10FFFF
      return ((b1 & 0x07) << 18) | ((b2 & 0x3F) << 12) | ((b3 & 0x3F) << 6) | (b4 & 0x3F);
    }
    return REPLACEMENT_CHAR;
  }
}
