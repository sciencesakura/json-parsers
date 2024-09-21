// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import com.sciencesakura.gjsonp.core.GJson
import java.nio.file.Path
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

/**
 * The main class of the CLI application.
 */
@Command(name = 'gjsonp', description = 'A Groovy JSON parser.', version = 'GJson Parser 1.0.0',
    mixinStandardHelpOptions = true, sortOptions = false)
class App implements Runnable {

  @Option(names = ['-e', '--expression'], description = 'Use EXPR as an expression.', paramLabel = 'EXPR')
  private String expression

  @Option(names = '--buffer', description = 'Use N bytes as a buffer.', paramLabel = 'N', defaultValue = '8192')
  private int buffer

  @Parameters(description = 'JSON files to process.', paramLabel = 'FILE')
  private List<Path> files

  static void main(args) {
    System.exit(new CommandLine(new App()).execute(args))
  }

  @Override
  void run() {
    if (files) {
      files.each {
        output GJson.parse(it.newInputStream(), buffer)
      }
    } else {
      output GJson.parse(System.in, buffer)
    }
  }

  private output(json) {
    def value = new Parser(expression).with {
      new Evaluator(it).eval(json)
    }
    println Formatter.format(value)
  }
}
