// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import spock.lang.Specification

class GJsonSpec extends Specification {

  def "parse 'array-01.json'"() {
    given:
    def input = getClass().getResourceAsStream('/array-01.json')

    expect:
    assert GJson.parse(input, 1024) == [
        null,
        true,
        false,
        42,
        3.14,
        -273.15,
        2.99792458e+8,
        'Hello\nOlá\nこんにちは\n你好\n안녕하세요\n👋👋🏻👋🏼👋🏽👋🏾👋🏿',
        'あのイーハトーヴォのすきとおった風、夏でも底に冷たさをもつ青いそら、うつくしい森で飾られたモリーオ市、郊外のぎらぎらひかる草の波。',
        [],
        [
            null, true, false, -2147483648, 'I AM YOUR FATHER.',
            [], [1, [2, [3]]], [:], [aa: 1, ab: [ba: 2, bb: [ca: 3]]]
        ],
        [:],
        [
            aa: null,
            ab: true,
            ac: false,
            ad: 2147483647,
            ae: '👨‍👩‍👧‍👦',
            af: [],
            ag: [1, [2, [3]]],
            ah: [:],
            ai: [ba: 1, bb: [ca: 2, cb: [da: 3]]],
        ],
    ]

    cleanup:
    input?.close()
  }

  def "parse 'object-01.json'"() {
    given:
    def input = getClass().getResourceAsStream('/object-01.json')

    expect:
    assert GJson.parse(input, 1024) == [
        a: null,
        b: true,
        c: false,
        d: 42,
        e: 3.14,
        f: -273.15,
        g: 2.99792458e+8,
        h: 'Hello\nOlá\nこんにちは\n你好\n안녕하세요\n👋👋🏻👋🏼👋🏽👋🏾👋🏿',
        i: 'あのイーハトーヴォのすきとおった風、夏でも底に冷たさをもつ青いそら、うつくしい森で飾られたモリーオ市、郊外のぎらぎらひかる草の波。',
        j: [],
        k: [
            null, true, false, -2147483648, 'I AM YOUR FATHER.',
            [], [1, [2, [3]]], [:], [aa: 1, ab: [ba: 2, bb: [ca: 3]]]
        ],
        l: [:],
        m: [
            aa: null,
            ab: true,
            ac: false,
            ad: 2147483647,
            ae: '👨‍👩‍👧‍👦',
            af: [],
            ag: [1, [2, [3]]],
            ah: [:],
            ai: [ba: 1, bb: [ca: 2, cb: [da: 3]]],
        ],
    ]

    cleanup:
    input?.close()
  }
}
