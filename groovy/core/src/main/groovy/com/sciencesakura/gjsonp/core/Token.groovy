// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import groovy.transform.ImmutableOptions
import groovy.transform.PackageScope

@PackageScope
sealed interface Token permits Bool, Colon, Comma, LeftBracket, LeftCurly, Null, Number, RightBracket, RightCurly,
    String {

  long line()

  long column()

  @PackageScope
  record Comma(long line, long column) implements Token {
  }

  @PackageScope
  record Colon(long line, long column) implements Token {
  }

  @PackageScope
  record LeftBracket(long line, long column) implements Token {
  }

  @PackageScope
  record RightBracket(long line, long column) implements Token {
  }

  @PackageScope
  record LeftCurly(long line, long column) implements Token {
  }

  @PackageScope
  record RightCurly(long line, long column) implements Token {
  }

  @PackageScope
  record Null(long line, long column) implements Token {
  }

  @PackageScope
  record Bool(long line, long column, boolean value) implements Token {
  }

  @PackageScope
  record String(long line, long column, java.lang.String value) implements Token {
  }

  @ImmutableOptions(knownImmutables = 'value')
  @PackageScope
  record Number(long line, long column, java.lang.Number value) implements Token {
  }
}
