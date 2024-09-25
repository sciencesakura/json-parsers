// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import groovy.transform.PackageScope

@PackageScope
class Formatter {

  private static final INDENT = '  '

  private final dest

  Formatter(dest) {
    this.dest = dest
  }

  void format(json, level = 0) {
    if (json == null) {
      dest << 'null'
    } else {
      switch (json) {
        case String -> formatString(json)
        case List -> formatArray(json, level)
        case Map -> formatObject(json, level)
        default -> dest << json
      }
    }
  }

  private formatString(s) {
    dest << '"'
    s.chars.each {
      dest << switch (it) {
        case '\b' -> '\\b'
        case '\t' -> '\\t'
        case '\n' -> '\\n'
        case '\f' -> '\\f'
        case '\r' -> '\\r'
        case '"' -> '\\"'
        case '\\' -> '\\\\'
        default -> it
      }
    }
    dest << '"'
  }

  private formatArray(array, level) {
    dest << '['
    def size = array.size()
    if (size == 0) {
      dest << ']'
      return
    }
    if (size == 1) {
      format(array[0], level)
      dest << ']'
      return
    }
    def nextLevel = level + 1
    def nextIndent = INDENT * nextLevel
    dest << '\n' << nextIndent
    (size - 1).times { i ->
      format(array[i], nextLevel)
      dest << ',\n' << nextIndent
    }
    format(array[size - 1], nextLevel)
    dest << '\n'
    level.times { dest << INDENT }
    dest << ']'
  }

  private formatObject(obj, level) {
    def size = obj.size()
    dest << '{'
    if (size == 0) {
      dest << '}'
      return
    }
    def nextLevel = level + 1
    def nextIndent = INDENT * nextLevel
    dest << '\n' << nextIndent
    def names = obj.keySet()
    (size - 1).times { i ->
      def name = names[i]
      formatString(name)
      dest << ': '
      format(obj[name], nextLevel)
      dest << ',\n' << nextIndent
    }
    def name = names[size - 1]
    formatString(name)
    dest << ': '
    format(obj[name], nextLevel)
    dest << '\n'
    level.times { dest << INDENT }
    dest << '}'
  }
}
