// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp;

import com.sciencesakura.jjsonp.core.JsonArray;
import com.sciencesakura.jjsonp.core.JsonObject;
import com.sciencesakura.jjsonp.core.JsonValue;
import com.sciencesakura.jjsonp.core.Strings;
import java.io.IOException;

final class Formatter {

  private static final String INDENT = "  ";

  private final Appendable dest;

  Formatter(Appendable dest) {
    this.dest = dest;
  }

  void format(JsonValue value) throws IOException {
    format(value, 0);
  }

  private void format(JsonValue value, int level) throws IOException {
    switch (value) {
      case JsonArray a -> formatArray(a, level);
      case JsonObject o -> formatObject(o, level);
      default -> dest.append(value.toString());
    }
  }

  private void formatArray(JsonArray array, int level) throws IOException {
    dest.append('[');
    var size = array.size();
    if (size == 0) {
      dest.append(']');
      return;
    }
    if (size == 1) {
      format(array.get(0), level);
      dest.append(']');
      return;
    }
    var nextLevel = level + 1;
    dest.append('\n');
    appendIndent(nextLevel);
    for (var i = 0; i < size - 1; i++) {
      format(array.get(i), nextLevel);
      dest.append(",\n");
      appendIndent(nextLevel);
    }
    format(array.get(size - 1), nextLevel);
    dest.append('\n');
    appendIndent(level);
    dest.append(']');
  }

  private void formatObject(JsonObject object, int level) throws IOException {
    dest.append('{');
    var size = object.size();
    if (size == 0) {
      dest.append('}');
      return;
    }
    var names = object.names();
    var nextLevel = level + 1;
    dest.append('\n');
    appendIndent(nextLevel);
    for (var i = 0; i < size - 1; i++) {
      var name = names.get(i);
      dest.append(Strings.toQuoted(name)).append(": ");
      format(object.get(name), nextLevel);
      dest.append(",\n");
      appendIndent(nextLevel);
    }
    var name = names.get(size - 1);
    dest.append(Strings.toQuoted(name)).append(": ");
    format(object.get(name), nextLevel);
    dest.append('\n');
    appendIndent(level);
    dest.append('}');
  }

  private void appendIndent(int level) throws IOException {
    for (var i = 0; i < level; i++) {
      dest.append(INDENT);
    }
  }
}
