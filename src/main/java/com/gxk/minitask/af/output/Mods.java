package com.gxk.minitask.af.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mods {

  private Mod cmd;

  @Override
  public String toString() {
    return "{" +
        "\"cmd\" : " + cmd +
        '}';
  }
}
