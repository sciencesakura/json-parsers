// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp;

sealed interface Instruction {

  record GetElement(int index) implements Instruction {
  }

  record GetMember(String name) implements Instruction {
  }
}
