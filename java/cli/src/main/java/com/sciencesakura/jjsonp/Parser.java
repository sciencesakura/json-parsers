// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

final class Parser implements Iterator<Instruction> {

  private final Iterator<? extends Token> tokens;

  private Instruction current;

  private Parser(String expression) {
    this.tokens = new Lexer(expression);
  }

  static List<Instruction> parse(String expression) {
    var instructions = new ArrayList<Instruction>();
    new Parser(expression).forEachRemaining(instructions::add);
    return Collections.unmodifiableList(instructions);
  }

  @Override
  public boolean hasNext() {
    if (current == null && tokens.hasNext()) {
      current = nextInstruction(tokens.next());
    }
    return current != null;
  }

  @Override
  public Instruction next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    var current = this.current;
    this.current = null;
    return current;
  }

  private Instruction nextInstruction(Token token) {
    return switch (token) {
      case Token.Period _ -> parsePeriodOpe();
      case Token.LeftBracket _ -> parseBracketOpe();
      default -> throw InvalidExpressionException.unexpectedToken(token);
    };
  }

  private Instruction parsePeriodOpe() {
    var token = tokens.next();
    if (token instanceof Token.String(_, String value)) {
      return new Instruction.GetMember(value);
    }
    throw InvalidExpressionException.unexpectedToken(token);
  }

  private Instruction parseBracketOpe() {
    var token = tokens.next();
    if (token instanceof Token.String(_, String value)) {
      token = tokens.next();
      if (token instanceof Token.RightBracket) {
        return new Instruction.GetMember(value);
      }
    } else if (token instanceof Token.Integer(_, int index)) {
      token = tokens.next();
      if (token instanceof Token.RightBracket) {
        return new Instruction.GetElement(index);
      }
    }
    throw InvalidExpressionException.unexpectedToken(token);
  }
}
