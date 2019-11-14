package com.gxk.minitask.af;

import com.gxk.minitask.af.output.FeedBack;
import com.gxk.minitask.af.output.Item;

import java.util.ArrayList;
import java.util.List;

public class OutUtils {

  public static FeedBack notFound() {
    List<Item> items = new ArrayList<>();
    Item item = new Item("NOT FOUND", "NOT FOUND", "NOT FOUND", "NOT FOUND", null, null, null);
    items.add(item);
    FeedBack feedBack = new FeedBack(items);
    return feedBack;
  }
}
