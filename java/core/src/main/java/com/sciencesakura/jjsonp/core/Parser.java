// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Optional;

final class Parser {

  private final Iterator<?  extends Token> tokens;

  Parser(Iterator<? extends Token> tokens) {
    this.tokens = tokens;
  }

  Optional<JsonValue<?>> parse() {
    return tokens.hasNext() ? Optional.of(parseValue(tokens.next())) : Optional.empty();
  }

  private JsonValue<?> parseValue(Token current) {
    return switch (current) {
      case Token.True _ -> JsonBool.TRUE;
      case Token.False _ -> JsonBool.FALSE;
      case Token.Null _ -> JsonNull.INSTANCE;
      case Token.String t -> new JsonString(t.value());
      case Token.Integer t -> new JsonInteger(t.value());
      case Token.Float t -> new JsonFloat(t.value());
      case Token.LeftBracket _ -> parseArray();
      case Token.LeftCurly _ -> parseObject();
      default -> throw ParserException.unexpectedToken(current);
    };
  }

  private JsonArray parseArray() {
    enum Status {
      INIT,
      AFTER_VALUE,
      AFTER_COMMA,
    }

    var array = new ArrayList<JsonValue<?>>();
    var status = Status.INIT;
    while (tokens.hasNext()) {
      var current = tokens.next();
      switch (status) {
        case INIT -> {
          if (current instanceof Token.RightBracket) {
            return JsonArray.EMPTY;
          }
          array.add(parseValue(current));
          status = Status.AFTER_VALUE;
        }
        case AFTER_VALUE -> {
          if (current instanceof Token.Comma) {
            status = Status.AFTER_COMMA;
          } else if (current instanceof Token.RightBracket) {
            return new JsonArray(array);
          } else {
            throw ParserException.unexpectedToken(current);
          }
        }
        case AFTER_COMMA -> {
          array.add(parseValue(current));
          status = Status.AFTER_VALUE;
        }
      }
    }
    throw ParserException.unexpectedEOF();
  }

  private JsonObject parseObject() {
    enum Status {
      INIT,
      AFTER_NAME,
      AFTER_COLON,
      AFTER_VALUE,
      AFTER_COMMA,
    }

    var object = new LinkedHashMap<String, JsonValue<?>>();
    var status = Status.INIT;
    String currentName = null;
    while (tokens.hasNext()) {
      var current = tokens.next();
      switch (status) {
        case INIT -> {
          if (current instanceof Token.RightCurly) {
            return JsonObject.EMPTY;
          }
          if (current instanceof Token.String name) {
            currentName = name.value();
            status = Status.AFTER_NAME;
          } else {
            throw ParserException.unexpectedToken(current);
          }
        }
        case AFTER_NAME -> {
          if (current instanceof Token.Colon) {
            status = Status.AFTER_COLON;
          } else {
            throw ParserException.unexpectedToken(current);
          }
        }
        case AFTER_COLON -> {
          object.put(currentName, parseValue(current));
          status = Status.AFTER_VALUE;
        }
        case AFTER_VALUE -> {
          if (current instanceof Token.Comma) {
            status = Status.AFTER_COMMA;
          } else if (current instanceof Token.RightCurly) {
            return new JsonObject(object);
          } else {
            throw ParserException.unexpectedToken(current);
          }
        }
        case AFTER_COMMA -> {
          if (current instanceof Token.String name) {
            currentName = name.value();
            status = Status.AFTER_NAME;
          } else {
            throw ParserException.unexpectedToken(current);
          }
        }
      }
    }
    throw ParserException.unexpectedEOF();
  }
}
