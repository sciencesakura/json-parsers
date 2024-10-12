// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.core;

import static com.sciencesakura.jjsonp.core.TestFunctions.newChannel;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class SourceTest {

  @Test
  void bufferSizeIsLessThanInput() throws IOException {
    try (var ch = newChannel(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)) {
      var source = new Source(ch, 4);
      assertThat(source.read()).isEqualTo(0x01);
      assertThat(source.read()).isEqualTo(0x02);
      assertThat(source.read()).isEqualTo(0x03);
      assertThat(source.read()).isEqualTo(0x04);
      assertThat(source.read()).isEqualTo(0x05);
      assertThat(source.read()).isEqualTo(0x06);
      assertThat(source.read()).isEqualTo(0x07);
      assertThat(source.read()).isEqualTo(0x08);
      assertThat(source.read()).isEqualTo(-1);
    }
  }

  @Test
  void bytesCanBePushedBackAndReadAgain() throws IOException {
    try (var ch = newChannel(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)) {
      var source = new Source(ch, 4);
      assertThat(source.read()).isEqualTo(0x01);
      assertThat(source.read()).isEqualTo(0x02);
      assertThat(source.read()).isEqualTo(0x03);
      assertThat(source.read()).isEqualTo(0x04);
      assertThat(source.read()).isEqualTo(0x05);
      source.back(0x03, 0x04, 0x05);
      assertThat(source.read()).isEqualTo(0x03);
      assertThat(source.read()).isEqualTo(0x04);
      assertThat(source.read()).isEqualTo(0x05);
      assertThat(source.read()).isEqualTo(0x06);
      assertThat(source.read()).isEqualTo(0x07);
      assertThat(source.read()).isEqualTo(0x08);
      source.back(0x06, 0x07, 0x08);
      assertThat(source.read()).isEqualTo(0x06);
      assertThat(source.read()).isEqualTo(0x07);
      assertThat(source.read()).isEqualTo(0x08);
      assertThat(source.read()).isEqualTo(-1);
    }
  }
}
