// SPDX-License-Identifier: GPL-3.0-or-later

/**
 * CLI module of JJsonp.
 */
module jjsonp.cli {
  requires info.picocli;
  requires jjsonp.core;
  opens com.sciencesakura.jjsonp.cli to info.picocli;
}
