// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import groovy.transform.PackageScope

@PackageScope
sealed interface Token permits Integer, LeftBracket, Period, RightBracket, String {

  int pos()

  @PackageScope
  record Period(int pos) implements Token {
  }

  @PackageScope
  record LeftBracket(int pos) implements Token {
  }

  @PackageScope
  record RightBracket(int pos) implements Token {
  }

  @PackageScope
  record String(int pos, java.lang.String value) implements Token {
  }

  @PackageScope
  record Integer(int pos, int value) implements Token {
  }
}
