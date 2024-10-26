// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import groovy.transform.PackageScope

@PackageScope
class InvalidExpressionException extends RuntimeException {

  InvalidExpressionException(String message) {
    super(message)
  }

  static InvalidExpressionException unexpectedToken(Token token) {
    new InvalidExpressionException("Unexpected token '${token.class.simpleName}' at ${token.pos()}")
  }
}
