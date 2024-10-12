// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import com.sciencesakura.gjsonp.core.GJson
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

  private static final FMT = new Formatter(System.out)

  @Option(names = ['-e', '--expression'], description = 'Use EXPR as an expression.', paramLabel = 'EXPR')
  private String expression

  @Parameters(description = 'JSON files to process.', paramLabel = 'FILE')
  private List<File> files

  static void main(args) {
    System.exit(new CommandLine(new App()).execute(args))
  }

  @Override
  void run() {
    def inst = new Parser(expression)
    if (files) {
      files.each {
        output(GJson.parse(it.newInputStream()), inst)
      }
    } else {
      output(GJson.parse(System.in), inst)
    }
  }

  private static output(json, inst) {
    FMT.format(Evaluator.eval(inst, json))
    println()
  }
}
