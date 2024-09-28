// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.Serializable;

/**
 * Represents a JSON value.
 */
public sealed interface JsonValue extends Serializable permits JsonArray, JsonBool, JsonFloat, JsonInteger, JsonNull,
    JsonObject, JsonString {
}
