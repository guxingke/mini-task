package com.gxk.minitask;

import com.gxk.minitask.af.output.FeedBack;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {

  private Main main;

  @Before
  public void setup() {
    main = new Main();
  }

  @Test
  public void test() throws IOException {
    FeedBack back = main.exec();
    assertTrue(back.toString().contains("TODO"));
  }
}
