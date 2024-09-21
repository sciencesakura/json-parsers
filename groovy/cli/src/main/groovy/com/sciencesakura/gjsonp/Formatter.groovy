// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import groovy.transform.PackageScope

@PackageScope
class Formatter {

  private static final INDENT = '  '

  static String format(json) {
    formatJson(json) as String
  }

  private static formatJson(json, level = 0) {
    if (json == null) return 'null'
    switch (json) {
      case String -> formatString(json)
      case List -> formatArray(json, level)
      case Map -> formatObject(json, level)
      default -> json
    }
  }

  private static formatString(s) {
    def str = new StringBuilder('"')
    s.chars.each {
      switch (it) {
        case '\b' -> str << '\\b'
        case '\t' -> str << '\\t'
        case '\n' -> str << '\\n'
        case '\f' -> str << '\\f'
        case '\r' -> str << '\\r'
        case '"' -> str << '\\"'
        case '\\' -> str << '\\\\'
        default -> str << it
      }
    }
    str << '"'
  }

  private static formatArray(array, level) {
    if (!array) return '[]'
    if (array.size() == 1) {
      return "[${formatJson(array[0], level)}]"
    }
    def nextLevel = level + 1
    def nextIndent = INDENT * nextLevel
    def s = new StringBuilder('[')
    array.each {
      s << '\n' << nextIndent << formatJson(it, nextLevel) << ','
    }
    s.deleteCharAt(s.length() - 1) << '\n' << (INDENT * level) << ']'
  }

  private static formatObject(obj, level) {
    if (!obj) return '{}'
    def nextLevel = level + 1
    def nextIndent = INDENT * nextLevel
    def s = new StringBuilder('{')
    obj.each { n, v ->
      s << '\n' << nextIndent << formatString(n) << ': ' << formatJson(v, nextLevel) << ','
    }
    s.deleteCharAt(s.length() - 1) << '\n' << (INDENT * level) << '}'
  }
}
