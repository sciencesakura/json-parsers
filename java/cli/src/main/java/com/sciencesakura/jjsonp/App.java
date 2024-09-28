// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp;

import com.sciencesakura.jjsonp.core.JJson;
import com.sciencesakura.jjsonp.core.JsonValue;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * The main class of the CLI application.
 */
@Command(name = "jjsonp", description = "A Java JSON parser", version = "JJson Parser 1.0.0",
    mixinStandardHelpOptions = true, sortOptions = false)
public final class App implements Callable<Integer> {

  @Option(names = {"-e", "--expression"}, description = "Use EXPR as an expression.", paramLabel = "EXPR")
  private String expression;

  @Option(names = "--buffer", description = "Use N bytes as a buffer.", paramLabel = "N", defaultValue = "8192")
  private int buffer;

  @Parameters(description = "JSON files to process.", paramLabel = "FILE")
  private List<Path> files;

  public static void main(String[] args) {
    System.exit(new CommandLine(new App()).execute(args));
  }

  @Override
  public Integer call() throws IOException {
    var inst = Parser.parse(expression);
    var fmt = new Formatter(System.out);
    if (files == null || files.isEmpty()) {
      JJson.parse(System.in, buffer).ifPresent(json -> output(json, inst, fmt));
    } else {
      for (var f : files) {
        JJson.parse(FileChannel.open(f, StandardOpenOption.READ), buffer).ifPresent(json -> output(json, inst, fmt));
      }
    }
    return 0;
  }

  private static void output(JsonValue json, List<Instruction> inst, Formatter fmt) {
    try {
      var value = Evaluator.eval(inst, json);
      fmt.format(value);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
