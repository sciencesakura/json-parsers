// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import groovy.transform.PackageScope

@PackageScope
final class Characters {

  static final REPLACEMENT_CHAR = '�'

  private Characters() {
  }

  static boolean isWhitespace(c) {
    c == ' ' || c == '\t' || c == '\n' || c == '\r'
  }

  static boolean isAlpha(c) {
    c in 'A'..'Z' || c in 'a'..'z'
  }

  static boolean isDigit(c) {
    c in '0'..'9'
  }

  static boolean isHex(c) {
    isDigit(c) || c in 'A'..'F' || c in 'a'..'f'
  }

  static boolean isControl(c) {
    c in '\u0000'..'\u001F'
  }
}
