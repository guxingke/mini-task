package com.gxk.minitask.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class TaskTest {

  String input = "# TODO this is a title. ||| :due_at: <2019-11-11T11:11:11> @test\n"
      + "asdafd \n"
      + "```bash\n"
      + "echo 'xxx'\n"
      + "```";

  @Test
  public void readObject() {
    Task task = Task.readObject(input);

    assertEquals(0, (int) task.priority);

    assertTrue(task.status.endsWith("TODO"));
  }

  @Test
  public void writeObject() {
    Task task = Task.readObject(input);

    String output = Task.writeObject(task);

    System.out.println(output);
  }
}