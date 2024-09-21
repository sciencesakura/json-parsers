// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import groovy.transform.PackageScope

@PackageScope
class Evaluator {

  private final instructions

  Evaluator(Iterator<Instruction> instructions) {
    this.instructions = instructions
  }

  def eval(json) {
    def current = json
    while (instructions.hasNext()) {
      def i = instructions.next()
      current = switch (i) {
        case Instruction.GetElement -> current[i.index()]
        default -> throw new IllegalStateException("Unknown instruction: $i")
      }
    }
    current
  }
}
