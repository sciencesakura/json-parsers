// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

/**
 * Represents a JSON container that can contain other JSON values.
 *
 * @param <C> the type of the container's contents.
 */
public sealed interface JsonContainer<C> extends JsonValue<C> permits JsonArray, JsonObject {

  /**
   * Returns the number of contents in this container.
   *
   * @return the number of contents in this container.
   */
  int size();

  /**
   * Returns whether this container is empty.
   *
   * @return {@code true} if this container is empty, {@code false} otherwise.
   */
  default boolean isEmpty() {
    return size() == 0;
  }
}
