package com.gxk.minitask.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskParser {

  public static List<Task> parse(Path path) throws IOException {
    if (!path.toFile().exists()) {
      return new ArrayList<>();
    }
    byte[] bytes = Files.readAllBytes(path);
    String str = new String(bytes);

    if (str.trim().length() == 0) {
      return new ArrayList<>();
    }
    // split by \n\n---\n\n
    String[] raws = str.split("\n\n---\n\n");

    List<Task> tasks = Arrays.stream(raws)
        .map(Task::readObject)
        .collect(Collectors.toList());

    return tasks;
  }
}
