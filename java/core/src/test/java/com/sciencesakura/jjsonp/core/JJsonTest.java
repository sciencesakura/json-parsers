// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import static com.sciencesakura.jjsonp.core.TestFunctions.sequencedMapOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.iterable;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class JJsonTest {

  @Test
  void parseArray() throws IOException {
    try (var stream = getClass().getResourceAsStream("/array-01.json")) {
      var json = JJson.parse(stream, 1024);
      assertThat(json).get(iterable(JsonValue.class)).containsExactly(
          JsonNull.INSTANCE,
          JsonBool.TRUE,
          JsonBool.FALSE,
          new JsonInteger(42),
          new JsonFloat(3.14),
          new JsonFloat(-273.15),
          new JsonFloat(2.99792458e+8),
          new JsonString("Hello\nOlá\nこんにちは\n你好\n안녕하세요\n👋👋🏻👋🏼👋🏽👋🏾👋🏿"),
          new JsonString("あのイーハトーヴォのすきとおった風、夏でも底に冷たさをもつ青いそら、うつくしい森で飾られたモリーオ市、郊外のぎらぎらひかる草の波。"),
          JsonArray.EMPTY,
          new JsonArray(
              JsonNull.INSTANCE,
              JsonBool.TRUE,
              JsonBool.FALSE,
              new JsonInteger(-2147483648L),
              new JsonString("I AM YOUR FATHER."),
              JsonArray.EMPTY,
              new JsonArray(
                  new JsonInteger(1),
                  new JsonArray(
                      new JsonInteger(2),
                      new JsonArray(new JsonInteger(3))
                  )
              ),
              JsonObject.EMPTY,
              new JsonObject(sequencedMapOf(
                  "aa", new JsonInteger(1),
                  "ab", new JsonObject(sequencedMapOf(
                      "ba", new JsonInteger(2),
                      "bb", new JsonObject(sequencedMapOf(
                          "ca", new JsonInteger(3)
                      ))
                  ))
              ))
          ),
          JsonObject.EMPTY,
          new JsonObject(sequencedMapOf(
              "aa", JsonNull.INSTANCE,
              "ab", JsonBool.TRUE,
              "ac", JsonBool.FALSE,
              "ad", new JsonInteger(2147483647),
              "ae", new JsonString("👨‍👩‍👧‍👦"),
              "af", JsonArray.EMPTY,
              "ag", new JsonArray(
                  new JsonInteger(1),
                  new JsonArray(
                      new JsonInteger(2),
                      new JsonArray(new JsonInteger(3))
                  )
              ),
              "ah", JsonObject.EMPTY,
              "ai", new JsonObject(sequencedMapOf(
                  "ba", new JsonInteger(1),
                  "bb", new JsonObject(sequencedMapOf(
                      "ca", new JsonInteger(2),
                      "cb", new JsonObject(sequencedMapOf(
                          "da", new JsonInteger(3)
                      ))
                  ))
              ))
          ))
      );
    }
  }

  @Test
  void parseObject() throws IOException {
    try (var stream = getClass().getResourceAsStream("/object-01.json")) {
      var json = JJson.parse(stream, 1024);
      assertThat(json).get(type(JsonObject.class)).satisfies(o -> {
        assertThat(o.get("a")).isEqualTo(JsonNull.INSTANCE);
        assertThat(o.get("b")).isEqualTo(JsonBool.TRUE);
        assertThat(o.get("c")).isEqualTo(JsonBool.FALSE);
        assertThat(o.get("d")).isEqualTo(new JsonInteger(42));
        assertThat(o.get("e")).isEqualTo(new JsonFloat(3.14));
        assertThat(o.get("f")).isEqualTo(new JsonFloat(-273.15));
        assertThat(o.get("g")).isEqualTo(new JsonFloat(2.99792458e+8));
        assertThat(o.get("h")).isEqualTo(new JsonString("Hello\nOlá\nこんにちは\n你好\n안녕하세요\n👋👋🏻👋🏼👋🏽👋🏾👋🏿"));
        assertThat(o.get("i")).isEqualTo(new JsonString("あのイーハトーヴォのすきとおった風、夏でも底に冷たさをもつ青いそら、うつくしい森で飾られたモリーオ市、郊外のぎらぎらひかる草の波。"));
        assertThat(o.get("j")).isEqualTo(JsonArray.EMPTY);
        assertThat(o.get("k")).isEqualTo(new JsonArray(
            JsonNull.INSTANCE,
            JsonBool.TRUE,
            JsonBool.FALSE,
            new JsonInteger(-2147483648L),
            new JsonString("I AM YOUR FATHER."),
            JsonArray.EMPTY,
            new JsonArray(
                new JsonInteger(1),
                new JsonArray(
                    new JsonInteger(2),
                    new JsonArray(new JsonInteger(3))
                )
            ),
            JsonObject.EMPTY,
            new JsonObject(sequencedMapOf(
                "aa", new JsonInteger(1),
                "ab", new JsonObject(sequencedMapOf(
                    "ba", new JsonInteger(2),
                    "bb", new JsonObject(sequencedMapOf(
                        "ca", new JsonInteger(3)
                    ))
                ))
            ))
        ));
        assertThat(o.get("l")).isEqualTo(JsonObject.EMPTY);
        assertThat(o.get("m")).isEqualTo(new JsonObject(sequencedMapOf(
            "aa", JsonNull.INSTANCE,
            "ab", JsonBool.TRUE,
            "ac", JsonBool.FALSE,
            "ad", new JsonInteger(2147483647),
            "ae", new JsonString("👨‍👩‍👧‍👦"),
            "af", JsonArray.EMPTY,
            "ag", new JsonArray(
                new JsonInteger(1),
                new JsonArray(
                    new JsonInteger(2),
                    new JsonArray(new JsonInteger(3))
                )
            ),
            "ah", JsonObject.EMPTY,
            "ai", new JsonObject(sequencedMapOf(
                "ba", new JsonInteger(1),
                "bb", new JsonObject(sequencedMapOf(
                    "ca", new JsonInteger(2),
                    "cb", new JsonObject(sequencedMapOf(
                        "da", new JsonInteger(3)
                    ))
                ))
            ))
        )));
      });
    }
  }
}
