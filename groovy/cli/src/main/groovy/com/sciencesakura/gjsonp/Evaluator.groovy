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
      if (i instanceof Instruction.GetElement) {
        current = current[i.index()]
      } else {
        throw new IllegalStateException("Unknown instruction: $i")
      }
    }
    current
  }
}
