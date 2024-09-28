// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

/**
 * Provides static methods for characters.
 */
public final class Characters {

  private Characters() {
  }

  /**
   * Decodes a given character as a hexadecimal digit.
   *
   * @param c the character code point.
   * @return the decoded hexadecimal digit, or {@code -1} if the character is not a hexadecimal digit.
   */
  public static int decodeHex(int c) {
    if (isDigit(c)) {
      return c - '0';
    }
    if ('A' <= c && c <= 'F') {
      return c - 'A' + 10;
    }
    if ('a' <= c && c <= 'f') {
      return c - 'a' + 10;
    }
    return -1;
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
   * Returns {@code true} if the given character is a control character.
   *
   * @param c the character code point.
   * @return {@code true} if the character is a control character.
   */
  public static boolean isControl(int c) {
    return 0x00 <= c && c <= 0x1F;
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
}
