package com.gxk.minitask.core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class TaskCmdTest {

  @Test
  public void test() throws IOException {
    List<Task> backlog = TaskCmd.tasks("input", "TODO");

    assertTrue(backlog.size() > 0);
  }
}