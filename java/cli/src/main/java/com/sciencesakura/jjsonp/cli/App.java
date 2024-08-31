// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp.cli;

import com.sciencesakura.jjsonp.core.JsonValue;
import com.sciencesakura.jjsonp.core.Jsons;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * The main class of the JJsonp CLI.
 */
@Command(name = "JJsonp", description = "A JSON Processor", version = "JJsonp 1.0.0", mixinStandardHelpOptions = true)
public final class App implements Callable<Integer> {

  @Option(names = {"-e", "--expression"}, description = "The expression to evaluate", paramLabel = "EXPR")
  private String expression;

  @Option(names = {"-c", "--compact"}, description = "Compact output", defaultValue = "false")
  private boolean compact;

  @Option(names = "--buffer", description = "The buffer size in bytes", paramLabel = "N", defaultValue = "8192")
  private int buffer;

  @Parameters(description = "The JSON files to process", paramLabel = "FILE")
  private List<Path> files;

  public static void main(String[] args) {
    System.exit(new CommandLine(new App()).execute(args));
  }

  @Override
  public Integer call() throws IOException {
    if (files == null || files.isEmpty()) {
      Jsons.parse(System.in, buffer).ifPresent(this::outputJson);
    } else {
      for (var f : files) {
        Jsons.parse(f, buffer).ifPresent(this::outputJson);
      }
    }
    return 0;
  }

  private void outputJson(JsonValue<?> json) {
    var result = Evaluator.eval(new Parser(expression), json);
    System.out.println(compact ? result.toString() : result.toPrettyString());
  }
}
