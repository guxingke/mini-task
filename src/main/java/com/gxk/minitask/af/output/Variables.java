package com.gxk.minitask.af.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class Variables {

  Map<String, String> map = new HashMap<>();

  public void put(String key, String val) {
    this.map.put(key, val);
  }

  @Override
  public String toString() {
    List<String> temps = new ArrayList<>();

    map.forEach((key,val)-> {
      temps.add(String.format("\"%s\": \"%s\"", key, val));
    });
    return temps.stream().collect(Collectors.joining(",", "{", "}"));
  }
}
