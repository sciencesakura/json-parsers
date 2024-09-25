// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

/**
 * Provides static methods for characters.
 */
final class Characters {

  private Characters() {
  }

  /**
   * Returns {@code true} if the given character is a whitespace character.
   *
   * @param c the character to check.
   * @return {@code true} if the given character is a whitespace character.
   */
  static boolean isWhitespace(c) {
    c == ' ' || c == '\t' || c == '\n' || c == '\r'
  }

  /**
   * Returns {@code true} if the given character is an alphabetic character.
   *
   * @param c the character to check.
   * @return {@code true} if the given character is an alphabetic character.
   */
  static boolean isAlpha(c) {
    c in 'A'..'Z' || c in 'a'..'z'
  }

  /**
   * Returns {@code true} if the given character is a digit character.
   *
   * @param c the character to check.
   * @return {@code true} if the given character is a digit character.
   */
  static boolean isDigit(c) {
    c in '0'..'9'
  }

  /**
   * Returns {@code true} if the given character is a hexadecimal character.
   *
   * @param c the character to check.
   * @return {@code true} if the given character is a hexadecimal character.
   */
  static boolean isHex(c) {
    isDigit(c) || c in 'A'..'F' || c in 'a'..'f'
  }

  /**
   * Returns {@code true} if the given character is a control character.
   *
   * @param c the character to check.
   * @return {@code true} if the given character is a control character.
   */
  static boolean isControl(c) {
    c in '\u0000'..'\u001F'
  }
}
