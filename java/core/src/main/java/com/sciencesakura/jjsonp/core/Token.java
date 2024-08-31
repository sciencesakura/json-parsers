// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

sealed interface Token {

  long line();

  long column();

  record EOF(long line, long column) implements Token {
  }

  record LeftBracket(long line, long column) implements Token {
  }

  record RightBracket(long line, long column) implements Token {
  }

  record LeftCurly(long line, long column) implements Token {
  }

  record RightCurly(long line, long column) implements Token {
  }

  record Colon(long line, long column) implements Token {
  }

  record Comma(long line, long column) implements Token {
  }

  record True(long line, long column) implements Token {
  }

  record False(long line, long column) implements Token {
  }

  record Null(long line, long column) implements Token {
  }

  record String(java.lang.String value, long line, long column) implements Token {
  }

  record Integer(long value, long line, long column) implements Token {
  }

  record Float(double value, long line, long column) implements Token {
  }
}
