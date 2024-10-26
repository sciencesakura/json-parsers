// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import groovy.transform.ImmutableOptions
import groovy.transform.PackageScope

@PackageScope
sealed interface Instruction permits GetElement {

  @ImmutableOptions(knownImmutables = 'index')
  @PackageScope
  record GetElement(index) implements Instruction {
  }
}
