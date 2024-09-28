// SPDX-License-Identifier: GPL-3.0-or-later

/**
 * CLI module of JJson Parser.
 */
module jjsonp.cli {
  requires info.picocli;
  requires jjsonp.core;
  opens com.sciencesakura.jjsonp to info.picocli;
}
