// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import java.nio.charset.StandardCharsets

/**
 * Provides static methods for parsing JSON.
 */
final class GJson {

  private GJson() {
  }

  /**
   * Parses JSON from the given input stream.
   *
   * @param stream the input stream to read JSON from.
   * @return the deserialized object.
   */
  static parse(InputStream stream) {
    new Parser(new Lexer(stream)).parse()
  }

  /**
   * Parses JSON from the given string.
   *
   * @param str the JSON string.
   * @return the deserialized object.
   */
  static parse(CharSequence str) {
    parse(new ByteArrayInputStream(str.toString().getBytes(StandardCharsets.UTF_8)))
  }
}
