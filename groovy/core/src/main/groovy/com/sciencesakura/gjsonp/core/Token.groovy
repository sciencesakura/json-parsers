// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import groovy.transform.ImmutableOptions
import groovy.transform.PackageScope

@PackageScope
sealed interface Token permits Bool, Colon, Comma, LeftBracket, LeftCurly, Null, Number, RightBracket, RightCurly,
    String {

  long line()

  long column()

  record Comma(long line, long column) implements Token {
  }

  record Colon(long line, long column) implements Token {
  }

  record LeftBracket(long line, long column) implements Token {
  }

  record RightBracket(long line, long column) implements Token {
  }

  record LeftCurly(long line, long column) implements Token {
  }

  record RightCurly(long line, long column) implements Token {
  }

  record Null(long line, long column) implements Token {
  }

  record Bool(long line, long column, boolean value) implements Token {
  }

  record String(long line, long column, java.lang.String value) implements Token {
  }

  @ImmutableOptions(knownImmutables = 'value')
  record Number(long line, long column, java.lang.Number value) implements Token {
  }
}
