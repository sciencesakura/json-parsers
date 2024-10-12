// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

final class Source {

  private final ReadableByteChannel channel;

  private final ByteBuffer buffer;

  private final int[] queue = {-1, -1, -1, -1};

  private int queueHead = 0;

  private int queueTail = 0;

  Source(ReadableByteChannel channel, int bufferSize) throws IOException {
    this.channel = channel;
    this.buffer = ByteBuffer.allocate(bufferSize);
    load();
  }

  int read() throws IOException {
    var b = queue[queueHead];
    if (b == -1) {
      return buffer.hasRemaining() || 0 < load() ? Byte.toUnsignedInt(buffer.get()) : -1;
    }
    queue[queueHead] = -1;
    queueHead = (queueHead + 1) % queue.length;
    return b;
  }

  void back(int... bytes) {
    var position = buffer.position();
    var shortage = bytes.length - position;
    for (var i = 0; i < shortage; i++) {
      queue[queueTail] = bytes[i];
      queueTail = (queueTail + 1) % queue.length;
    }
    buffer.position(Math.max(0, -shortage));
  }

  private int load() throws IOException {
    buffer.clear();
    var n = channel.read(buffer);
    buffer.flip();
    return n;
  }
}
