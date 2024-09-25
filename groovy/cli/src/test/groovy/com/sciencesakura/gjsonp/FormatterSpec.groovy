// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import spock.lang.Specification

class FormatterSpec extends Specification {

  def 'format true'() {
    given:
    def dest = new StringBuilder()
    def json = true

    when:
    new Formatter(dest).format(json)

    then:
    assert dest.toString() == 'true'
  }

  def 'format false'() {
    given:
    def dest = new StringBuilder()
    def json = false

    when:
    new Formatter(dest).format(json)

    then:
    assert dest.toString() == 'false'
  }

  def 'format null'() {
    given:
    def dest = new StringBuilder()
    def json = null

    when:
    new Formatter(dest).format(json)

    then:
    assert dest.toString() == 'null'
  }

  def 'format string'() {
    given:
    def dest = new StringBuilder()
    def json = '''\
        Hello
        Olá
        こんにちは
        你好
        안녕하세요
        👋👋🏻👋🏼👋🏽👋🏾👋🏿'''.stripIndent()

    when:
    new Formatter(dest).format(json)

    then:
    assert dest.toString() == '"Hello\\nOlá\\nこんにちは\\n你好\\n안녕하세요\\n👋👋🏻👋🏼👋🏽👋🏾👋🏿"'
  }

  def 'format number'() {
    given:
    def dest = new StringBuilder()
    def json = 42

    when:
    new Formatter(dest).format(json)

    then:
    assert dest.toString() == '42'
  }

  def 'format array'() {
    given:
    def dest = new StringBuilder()
    def json = [
        1, 2, 3,
        [], [4], [5, 6, 7],
        [:], [a: 1], [a: 2, b: 3, c: 4],
    ]

    when:
    new Formatter(dest).format(json)

    then:
    assert dest.toString() == '''\
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
    def dest = new StringBuilder()
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
    new Formatter(dest).format(json)

    then:
    assert dest.toString() == '''\
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
