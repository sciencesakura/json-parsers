// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import spock.lang.Specification

class FormatterSpec extends Specification {

  def 'format true'() {
    given:
    def json = true

    when:
    def actual = Formatter.format(json)

    then:
    assert actual == 'true'
  }

  def 'format false'() {
    given:
    def json = false

    when:
    def actual = Formatter.format(json)

    then:
    assert actual == 'false'
  }

  def 'format null'() {
    given:
    def json = null

    when:
    def actual = Formatter.format(json)

    then:
    assert actual == 'null'
  }

  def 'format string'() {
    given:
    def json = '''\
        Hello
        Olá
        こんにちは
        你好
        안녕하세요
        👋👋🏻👋🏼👋🏽👋🏾👋🏿'''.stripIndent()

    when:
    def actual = Formatter.format(json)

    then:
    assert actual == '"Hello\\nOlá\\nこんにちは\\n你好\\n안녕하세요\\n👋👋🏻👋🏼👋🏽👋🏾👋🏿"'
  }

  def 'format number'() {
    given:
    def json = 42

    when:
    def actual = Formatter.format(json)

    then:
    assert actual == '42'
  }

  def 'format array'() {
    given:
    def json = [
        1, 2, 3,
        [], [4], [5, 6, 7],
        [:], [a: 1], [a: 2, b: 3, c: 4],
    ]

    when:
    def actual = Formatter.format(json)

    then:
    assert actual == '''\
        [
          1,
          2,
          3,
          [],
          [4],
          [
            5,
            6,
            7
          ],
          {},
          {
            "a": 1
          },
          {
            "a": 2,
            "b": 3,
            "c": 4
          }
        ]'''.stripIndent()
  }

  def 'format object'() {
    given:
    def json = [
        a: 1,
        b: 2,
        c: [:],
        d: [aa: 1],
        e: [aa: 2, ab: 3, ac: 4],
        f: [],
        g: [1],
        h: [2, 3, 4],
    ]

    when:
    def actual = Formatter.format(json)

    then:
    assert actual == '''\
        {
          "a": 1,
          "b": 2,
          "c": {},
          "d": {
            "aa": 1
          },
          "e": {
            "aa": 2,
            "ab": 3,
            "ac": 4
          },
          "f": [],
          "g": [1],
          "h": [
            2,
            3,
            4
          ]
        }'''.stripIndent()
  }
}
