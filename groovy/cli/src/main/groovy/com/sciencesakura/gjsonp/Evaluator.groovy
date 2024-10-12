// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import groovy.transform.PackageScope

@PackageScope
class Evaluator {

  static eval(Iterator<Instruction> instructions, json) {
    def current = json
    while (instructions.hasNext()) {
      def i = instructions.next()
      if (i instanceof Instruction.GetElement) {
        current = current[i.index()]
      } else {
        throw new IllegalStateException("Unknown instruction: $i")
      }
    }
    current
  }
}
