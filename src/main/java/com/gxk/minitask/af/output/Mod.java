package com.gxk.minitask.af.output;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Mod {

  private boolean valid;
  private String arg;
  private String subtitle;

  public Mod(String arg, String subtitle) {
    this.valid = true;
    this.arg = arg;
    this.subtitle = subtitle;
  }

  @Override
  public String toString() {
    List<String> temps = new ArrayList<>();
    temps.add(String.format("\"%s\": %s", "valid", valid));
    temps.add(String.format("\"%s\": \"%s\"", "arg", arg));
    temps.add(String.format("\"%s\": \"%s\"", "subtitle", subtitle));
    return temps.stream().collect(Collectors.joining(",", "{", "}"));
  }
}
