// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.cli;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class Parser implements Iterator<Instruction> {

  private final Iterator<? extends Token> tokens;

  private Instruction next;

  Parser(String expression) {
    this.tokens = expression == null ? Collections.emptyIterator() : new Lexer(expression);
  }

  @Override
  public boolean hasNext() {
    if (next == null) {
      next = tokens.hasNext() ? parseInstruction(tokens.next()) : null;
    }
    return next != null;
  }

  @Override
  public Instruction next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    var current = next;
    next = null;
    return current;
  }

  private Instruction parseInstruction(Token current) {
    return switch (current) {
      case Tokens.LEFT_BRACKET -> parseGetElement();
      case Tokens.PERIOD -> parseGetMember();
      default -> throw ParserException.unexpectedToken(current);
    };
  }

  private Instruction.GetElement parseGetElement() {
    var current = tokens.next();
    if (current instanceof Token.Integer(int value) && (current = tokens.next()) == Tokens.RIGHT_BRACKET) {
      return new Instruction.GetElement(value);
    }
    throw ParserException.unexpectedToken(current);
  }

  private Instruction.GetMember parseGetMember() {
    var current = tokens.next();
    if (current instanceof Token.String(String value)) {
      return new Instruction.GetMember(value);
    }
    throw ParserException.unexpectedToken(current);
  }
}
