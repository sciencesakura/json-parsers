// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.cli;

import com.sciencesakura.jjsonp.core.JsonArray;
import com.sciencesakura.jjsonp.core.JsonObject;
import com.sciencesakura.jjsonp.core.JsonValue;
import java.util.Iterator;

final class Evaluator {

  private Evaluator() {
  }

  static JsonValue<?> eval(Iterator<? extends Instruction> instructions, JsonValue<?> value) {
    var current = value;
    while (instructions.hasNext()) {
      var i = instructions.next();
      current = switch (i) {
        case Instruction.GetElement(int index) -> getElement(current, index);
        case Instruction.GetMember(String name) -> getMember(current, name);
      };
    }
    return current;
  }

  private static JsonValue<?> getElement(JsonValue<?> value, int index) {
    if (value instanceof JsonArray a) {
      return a.get(index);
    }
    throw new IllegalArgumentException("Could not access by [%d] from non-array".formatted(index));
  }

  private static JsonValue<?> getMember(JsonValue<?> value, String name) {
    if (value instanceof JsonObject o) {
      return o.get(name);
    }
    throw new IllegalArgumentException("Could not access by '.%s' from non-object".formatted(name));
  }
}
