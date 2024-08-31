// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.cli;

sealed interface Token permits Token.Integer, Token.String, Tokens {

  record String(java.lang.String value) implements Token {
  }

  record Integer(int value) implements Token {
  }
}
