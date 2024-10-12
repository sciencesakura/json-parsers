// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

/**
 * Provides static methods for JSON.
 */
public final class JJson {

  private JJson() {
  }

  /**
   * Parses JSON from the given channel.
   *
   * @param channel    the channel to read JSON from.
   * @param bufferSize the buffer size in bytes.
   * @return the parsed JSON value, or {@link Optional#empty()} if the input is empty.
   * @throws IOException if an I/O error occurs.
   */
  @NonNull
  public static Optional<JsonValue> parse(@NonNull ReadableByteChannel channel, int bufferSize) throws IOException {
    return new Parser(new Lexer(channel, bufferSize)).parse();
  }

  /**
   * Parses JSON from the given input stream.
   *
   * @param stream     the input stream to read JSON from.
   * @param bufferSize the buffer size in bytes.
   * @return the parsed JSON value, or {@link Optional#empty()} if the input is empty.
   * @throws IOException if an I/O error occurs.
   */
  @NonNull
  public static Optional<JsonValue> parse(@NonNull InputStream stream, int bufferSize) throws IOException {
    return parse(Channels.newChannel(stream), bufferSize);
  }

  /**
   * Parses JSON from the given byte array.
   *
   * @param bytes the byte array to read JSON from.
   * @return the parsed JSON value, or {@link Optional#empty()} if the input is empty.
   * @throws IOException if an I/O error occurs.
   */
  @NonNull
  public static Optional<JsonValue> parse(byte @NonNull [] bytes) throws IOException {
    return parse(new ByteArrayInputStream(bytes), bytes.length);
  }

  /**
   * Parses JSON from the given JSON string.
   *
   * @param jsonString the JSON string.
   * @return the parsed JSON value, or {@link Optional#empty()} if the input is empty.
   * @throws IOException if an I/O error occurs.
   */
  @NonNull
  public static Optional<JsonValue> parse(@NonNull CharSequence jsonString) throws IOException {
    return parse(jsonString.toString().getBytes(StandardCharsets.UTF_8));
  }
}
