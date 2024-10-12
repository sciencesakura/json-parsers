// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp.core

import groovy.transform.PackageScope

@PackageScope
class Source {

  private final queue = new ArrayDeque()

  private final stream

  Source(InputStream stream) {
    this.stream = stream
  }

  def read() {
    queue ? queue.poll() : stream.read()
  }

  def back(int... bytes) {
    queue.addAll(bytes)
  }
}
