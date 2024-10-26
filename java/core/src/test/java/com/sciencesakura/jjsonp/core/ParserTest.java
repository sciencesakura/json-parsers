// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import static com.sciencesakura.jjsonp.core.TestFunctions.sequencedMapOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParserTest {

  @Test
  void returnEmptyIfNoToken() {
    var actual = new Parser(Collections.emptyIterator()).parse();
    assertThat(actual).isEmpty();
  }

  @Test
  void parseTrue() {
    var tokens = List.of(new Token.True(0, 0));
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(JsonBool.TRUE);
  }

  @Test
  void parseFalse() {
    var tokens = List.of(new Token.False(0, 0));
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(JsonBool.FALSE);
  }

  @Test
  void parseNull() {
    var tokens = List.of(new Token.Null(0, 0));
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(JsonNull.INSTANCE);
  }

  @Test
  void parseString() {
    var tokens = List.of(new Token.String(0, 0, "foo"));
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonString("foo"));
  }

  @Test
  void parseInteger() {
    var tokens = List.of(new Token.Integer(0, 0, 42));
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonInteger(42));
  }

  @Test
  void parseFloat() {
    var tokens = List.of(new Token.Float(0, 0, 3.14));
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonFloat(3.14));
  }

  @Test
  void parseEmptyArray() {
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.RightBracket(0, 0)
    );
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(JsonArray.EMPTY);
  }

  @Test
  void parseArrayHavingOneElement() {
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.String(0, 0, "foo"),
        new Token.RightBracket(0, 0)
    );
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonArray(new JsonString("foo")));
  }

  @Test
  void parseArrayHavingTwoElements() {
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.String(0, 0, "foo"),
        new Token.Comma(0, 0),
        new Token.Integer(0, 0, 42),
        new Token.RightBracket(0, 0)
    );
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonArray(
        new JsonString("foo"),
        new JsonInteger(42)
    ));
  }

  @Test
  void throwExceptionForUnclosedArray_1() {
    // [
    var tokens = List.of(new Token.LeftBracket(0, 0));
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected end of input");
  }

  @Test
  void throwExceptionForUnclosedArray_2() {
    // ["foo"
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.String(0, 0, "foo")
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected end of input");
  }

  @Test
  void throwExceptionForUnclosedArray_3() {
    // ["foo",
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.String(0, 0, "foo"),
        new Token.Comma(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected end of input");
  }

  @Test
  void throwExceptionForInvalidSequenceOfTokens_1_1() {
    // [,]
    var tokens = List.of(
        new Token.LeftBracket(1, 2),
        new Token.Comma(3, 4),
        new Token.RightBracket(5, 6)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected token 'Comma' at 3:4")
        .asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          assertThat(e.getLine()).isEqualTo(3);
          assertThat(e.getColumn()).isEqualTo(4);
        });
  }

  @Test
  void throwExceptionForInvalidSequenceOfTokens_1_2() {
    // ["foo",,]
    var tokens = List.of(
        new Token.LeftBracket(1, 2),
        new Token.String(3, 4, "foo"),
        new Token.Comma(5, 6),
        new Token.Comma(7, 8),
        new Token.RightBracket(9, 10)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected token 'Comma' at 7:8")
        .asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          assertThat(e.getLine()).isEqualTo(7);
          assertThat(e.getColumn()).isEqualTo(8);
        });
  }

  @Test
  void throwExceptionForInvalidSequenceOfTokens_1_3() {
    // ["foo" "bar"]
    var tokens = List.of(
        new Token.LeftBracket(1, 2),
        new Token.String(3, 4, "foo"),
        new Token.String(5, 6, "bar"),
        new Token.RightBracket(7, 8)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected token 'String' at 5:6")
        .asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          assertThat(e.getLine()).isEqualTo(5);
          assertThat(e.getColumn()).isEqualTo(6);
        });
  }

  @Test
  void parseEmptyObject() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.RightCurly(0, 0)
    );
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(JsonObject.EMPTY);
  }

  @Test
  void parseObjectHavingOnePair() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, "foo"),
        new Token.Colon(0, 0),
        new Token.String(0, 0, "bar"),
        new Token.RightCurly(0, 0)
    );
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonObject(sequencedMapOf("foo", new JsonString("bar"))));
  }

  @Test
  void parseObjectHavingTwoPairs() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, "foo"),
        new Token.Colon(0, 0),
        new Token.String(0, 0, "bar"),
        new Token.Comma(0, 0),
        new Token.String(0, 0, "baz"),
        new Token.Colon(0, 0),
        new Token.Integer(0, 0, 42),
        new Token.RightCurly(0, 0)
    );
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonObject(sequencedMapOf(
        "foo", new JsonString("bar"),
        "baz", new JsonInteger(42)
    )));
  }

  @Test
  void throwExceptionForUnclosedObject_1() {
    // {
    var tokens = List.of(new Token.LeftCurly(0, 0));
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected end of input");
  }

  @Test
  void throwExceptionForUnclosedObject_2() {
    // {"foo"
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, "foo")
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected end of input");
  }

  @Test
  void throwExceptionForUnclosedObject_3() {
    // {"foo":
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, "foo"),
        new Token.Colon(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected end of input");
  }

  @Test
  void throwExceptionForUnclosedObject_4() {
    // {"foo": "bar"
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, "foo"),
        new Token.Colon(0, 0),
        new Token.String(0, 0, "bar")
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected end of input");
  }

  @Test
  void throwExceptionForUnclosedObject_5() {
    // {"foo": "bar",
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String(0, 0, "foo"),
        new Token.Colon(0, 0),
        new Token.String(0, 0, "bar"),
        new Token.Comma(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected end of input");
  }

  @Test
  void throwExceptionForInvalidSequenceOfTokens_2_1() {
    // {:}
    var tokens = List.of(
        new Token.LeftCurly(1, 2),
        new Token.Colon(3, 4),
        new Token.RightCurly(5, 6)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected token 'Colon' at 3:4")
        .asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          assertThat(e.getLine()).isEqualTo(3);
          assertThat(e.getColumn()).isEqualTo(4);
        });
  }

  @Test
  void throwExceptionForInvalidSequenceOfTokens_2_2() {
    // {"foo"}
    var tokens = List.of(
        new Token.LeftCurly(1, 2),
        new Token.String(3, 4, "foo"),
        new Token.RightCurly(5, 6)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected token 'RightCurly' at 5:6")
        .asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          assertThat(e.getLine()).isEqualTo(5);
          assertThat(e.getColumn()).isEqualTo(6);
        });
  }

  @Test
  void throwExceptionForInvalidSequenceOfTokens_2_3() {
    // {"foo"::}
    var tokens = List.of(
        new Token.LeftCurly(1, 2),
        new Token.String(3, 4, "foo"),
        new Token.Colon(5, 6),
        new Token.Colon(7, 8),
        new Token.RightCurly(9, 10)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected token 'Colon' at 7:8")
        .asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          assertThat(e.getLine()).isEqualTo(7);
          assertThat(e.getColumn()).isEqualTo(8);
        });
  }

  @Test
  void throwExceptionForInvalidSequenceOfTokens_2_4() {
    // {"foo" "bar"}
    var tokens = List.of(
        new Token.LeftCurly(1, 2),
        new Token.String(3, 4, "foo"),
        new Token.String(5, 6, "bar"),
        new Token.RightCurly(7, 8)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected token 'String' at 5:6")
        .asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          assertThat(e.getLine()).isEqualTo(5);
          assertThat(e.getColumn()).isEqualTo(6);
        });
  }

  @Test
  void throwExceptionForInvalidSequenceOfTokens_2_5() {
    // {"foo": 42,}
    var tokens = List.of(
        new Token.LeftCurly(1, 2),
        new Token.String(3, 4, "foo"),
        new Token.Colon(5, 6),
        new Token.Integer(7, 8, 42),
        new Token.Comma(9, 10),
        new Token.RightCurly(11, 12)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).hasMessage("Unexpected token 'RightCurly' at 11:12")
        .asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          assertThat(e.getLine()).isEqualTo(11);
          assertThat(e.getColumn()).isEqualTo(12);
        });
  }
}
