// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import static java.nio.file.StandardOpenOption.READ;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A utility class for JSON.
 */
public final class Jsons {

  private Jsons() {
  }

  /**
   * Parse a JSON document from the given channel.
   *
   * @param channel    the channel to read from.
   * @param bufferSize the buffer size to use.
   * @return the parsed JSON document.
   * @throws ParserException if an error occurs during parsing.
   */
  @NonNull
  public static Optional<JsonValue<?>> parse(@NonNull ReadableByteChannel channel, int bufferSize) {
    var lexer = Lexer.newLexer(channel, bufferSize);
    var parser = new Parser(lexer);
    return parser.parse();
  }

  /**
   * Parse a JSON document from the given input stream.
   *
   * @param stream     the input stream to read from.
   * @param bufferSize the buffer size to use.
   * @return the parsed JSON document.
   * @throws ParserException if an error occurs during parsing.
   */
  @NonNull
  public static Optional<JsonValue<?>> parse(@NonNull InputStream stream, int bufferSize) {
    return parse(Channels.newChannel(stream), bufferSize);
  }

  /**
   * Parse a JSON document from the given JSON string.
   *
   * @param jsonString the JSON string to parse.
   * @return the parsed JSON document.
   * @throws ParserException if an error occurs during parsing.
   */
  @NonNull
  public static Optional<JsonValue<?>> parse(@Nullable String jsonString) {
    if (jsonString == null) {
      return Optional.empty();
    }
    var bytes = jsonString.getBytes(StandardCharsets.UTF_8);
    return parse(new ByteArrayInputStream(bytes), bytes.length);
  }

  /**
   * Parse a JSON document from the given file path.
   *
   * @param path       the file path to read from.
   * @param bufferSize the buffer size to use.
   * @return the parsed JSON document.
   * @throws ParserException if an error occurs during parsing.
   * @throws IOException     if an I/O error occurs.
   */
  @NonNull
  public static Optional<JsonValue<?>> parse(@NonNull Path path, int bufferSize) throws IOException {
    try (var ch = Files.newByteChannel(path, READ)) {
      return parse(ch, bufferSize);
    }
  }
}
