// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.charset.StandardCharsets

/**
 * Provides static methods for parsing JSON.
 */
final class GJson {

  private GJson() {
  }

  /**
   * Parses JSON from the given channel.
   *
   * @param channel the channel to read JSON from.
   * @param bufferSize the buffer size in bytes.
   * @return the deserialized object.
   */
  static parse(ReadableByteChannel channel, int bufferSize) {
    new Parser(Lexer.newLexer(channel, bufferSize)).parse()
  }

  /**
   * Parses JSON from the given input stream.
   *
   * @param stream the input stream to read JSON from.
   * @param bufferSize the buffer size in bytes.
   * @return the deserialized object.
   */
  static parse(InputStream stream, int bufferSize) {
    parse(Channels.newChannel(stream), bufferSize)
  }

  /**
   * Parses JSON from the given string.
   *
   * @param str the JSON string.
   * @return the deserialized object.
   */
  static parse(CharSequence str) {
    parse(new ByteArrayInputStream(str.toString().getBytes(StandardCharsets.UTF_8)), str.length())
  }
}
