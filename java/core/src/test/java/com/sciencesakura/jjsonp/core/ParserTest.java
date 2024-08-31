// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ParserTest {

  private static final Logger LOG = LoggerFactory.getLogger(ParserTest.class);

  @Test
  void whenEmpty() {
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
    var tokens = List.of(new Token.String("foo", 0, 0));
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonString("foo"));
  }

  @Test
  void parseInteger() {
    var tokens = List.of(new Token.Integer(42, 0, 0));
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonInteger(42));
  }

  @Test
  void parseFloat() {
    var tokens = List.of(new Token.Float(3.14, 0, 0));
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
  void parse1ElementArray() {
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.String("foo", 0, 0),
        new Token.RightBracket(0, 0)
    );
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonArray(List.of(
        new JsonString("foo")
    )));
  }

  @Test
  void parseNElementsArray() {
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Comma(0, 0),
        new Token.Integer(42, 0, 0),
        new Token.RightBracket(0, 0)
    );
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonArray(List.of(
        new JsonString("foo"),
        new JsonInteger(42)
    )));
  }

  @Test
  void throwExceptionWhenArrayEndsUnexpectedly_1() {
    var tokens = List.of(
        new Token.LeftBracket(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
        });
  }

  @Test
  void throwExceptionWhenArrayEndsUnexpectedly_2() {
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.String("foo", 0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
        });
  }

  @Test
  void throwExceptionWhenArrayEndsUnexpectedly_3() {
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Comma(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
        });
  }

  @Test
  void throwExceptionOnUnexpectedTokenInArray_1() {
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.Comma(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_TOKEN);
        });
  }

  @Test
  void throwExceptionOnUnexpectedTokenInArray_2() {
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Comma(0, 0),
        new Token.Comma(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_TOKEN);
        });
  }

  @Test
  void throwExceptionOnUnexpectedTokenInArray_3() {
    var tokens = List.of(
        new Token.LeftBracket(0, 0),
        new Token.String("foo", 0, 0),
        new Token.String("bar", 0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_TOKEN);
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
  void parse1MemberObject() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Colon(0, 0),
        new Token.String("bar", 0, 0),
        new Token.RightCurly(0, 0)
    );
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonObject(Map.of(
        "foo", new JsonString("bar")
    )));
  }

  @Test
  void parseNMembersObject() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Colon(0, 0),
        new Token.String("bar", 0, 0),
        new Token.Comma(0, 0),
        new Token.String("baz", 0, 0),
        new Token.Colon(0, 0),
        new Token.Integer(42, 0, 0),
        new Token.RightCurly(0, 0)
    );
    var actual = new Parser(tokens.iterator()).parse();
    assertThat(actual).contains(new JsonObject(Map.of(
        "foo", new JsonString("bar"),
        "baz", new JsonInteger(42)
    )));
  }

  @Test
  void throwExceptionWhenObjectEndsUnexpectedly_1() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
        });
  }

  @Test
  void throwExceptionWhenObjectEndsUnexpectedly_2() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
        });
  }

  @Test
  void throwExceptionWhenObjectEndsUnexpectedly_3() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Colon(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
        });
  }

  @Test
  void throwExceptionWhenObjectEndsUnexpectedly_4() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Colon(0, 0),
        new Token.String("bar", 0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
        });
  }

  @Test
  void throwExceptionWhenObjectEndsUnexpectedly_5() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Colon(0, 0),
        new Token.String("bar", 0, 0),
        new Token.Comma(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_EOF);
        });
  }

  @Test
  void throwExceptionOnUnexpectedTokenInObject_1() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.Colon(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_TOKEN);
        });
  }

  @Test
  void throwExceptionOnUnexpectedTokenInObject_2() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Colon(0, 0),
        new Token.Colon(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_TOKEN);
        });
  }

  @Test
  void throwExceptionOnUnexpectedTokenInObject_3() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.Comma(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_TOKEN);
        });
  }

  @Test
  void throwExceptionOnUnexpectedTokenInObject_4() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Comma(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_TOKEN);
        });
  }

  @Test
  void throwExceptionOnUnexpectedTokenInObject_5() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Colon(0, 0),
        new Token.Comma(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_TOKEN);
        });
  }

  @Test
  void throwExceptionOnUnexpectedTokenInObject_6() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Colon(0, 0),
        new Token.String("bar", 0, 0),
        new Token.Comma(0, 0),
        new Token.Comma(0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_TOKEN);
        });
  }

  @Test
  void throwExceptionOnUnexpectedTokenInObject_7() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0),
        new Token.String("bar", 0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_TOKEN);
        });
  }

  @Test
  void throwExceptionOnUnexpectedTokenInObject_8() {
    var tokens = List.of(
        new Token.LeftCurly(0, 0),
        new Token.String("foo", 0, 0),
        new Token.Colon(0, 0),
        new Token.String("bar", 0, 0),
        new Token.String("baz", 0, 0)
    );
    var parser = new Parser(tokens.iterator());
    assertThatThrownBy(parser::parse).asInstanceOf(throwable(ParserException.class))
        .satisfies(e -> {
          LOG.info("thrown exception", e);
          assertThat(e.getType()).isEqualTo(ParserException.Type.UNEXPECTED_TOKEN);
        });
  }
}
