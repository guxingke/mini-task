package com.gxk.minitask.core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;

public class TaskParserTest {

  @Test
  public void parse() throws IOException {
    List<Task> tasks = TaskParser.parse(Paths.get(System.getProperty("user.home"), "config", "minitask", "input.md"));
    assertTrue(tasks.size() > 0);
  }
}