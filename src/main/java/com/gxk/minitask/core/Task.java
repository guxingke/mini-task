package com.gxk.minitask.core;

import com.gxk.minitask.util.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Task {

  public final Integer id;
  public final Integer priority;
  public final String status;
  public final String name;
  public final List<String> tags;
  public final Map<String,Object> metas;
  public final String note;

  public Task(Integer priority, String status, String name, List<String> tags,
      Map<String, Object> metas, String note) {
    this.id = (name + status).hashCode();
    this.priority = priority;
    this.status = status;
    this.name = name;
    this.tags = tags;
    this.metas = metas;
    this.note = note;
  }

  public static Task readObject(String raw) {
    if (!raw.contains("\n")) {
      return parseNoBody(raw);
    }

    String header = raw.substring(0, raw.indexOf("\n"));
    String body = raw.substring(raw.indexOf("\n") + 1);

    return parse(header, body);
  }

  public static String writeObject(Task task) {
    StringBuilder sb = new StringBuilder();
    sb.append("# ");
    sb.append(task.status);
    sb.append(" ");
    sb.append(task.name);
    sb.append(" ||| ");
    if (task.tags.size() > 0) {
      String tmp = task.tags.stream().collect(Collectors.joining(" "));
      sb.append(tmp).append(" ");
    }
    if (task.metas.size() > 0) {
      task.metas.forEach((key,val) -> {
        if (key.endsWith("_i")) {
          sb.append(":").append(key).append(":");
          sb.append(" ");
          sb.append("<");
          sb.append(val);
          sb.append(">");
          sb.append(" ");
        }
        if (key.endsWith("_at")) {
          sb.append(":").append(key).append(":");
          sb.append(" ");
          sb.append("<");
          sb.append(Utils.format(((Date) val)));
          sb.append(">");
          sb.append(" ");
        }
      });
    }
    if (task.note != null && task.note.trim().length() != 0) {
      sb.append("\n");
      sb.append(task.note);
    }
    return sb.toString();
  }

  private static Task parse(String header, String body) {
    header = header.replaceAll("[' ']+", " ");
    String[] split = header.split("\\|\\|\\|");
    String main = split[0];
    String meta = split[1];

    // ignore #
    int sidx = main.indexOf(" ", 2);
    String status = main.substring(2, sidx);
    String name = main.substring(sidx + 1).trim();

    List<String> tags = parseTags(meta);
    Map<String, Object> metas = parseMetas(meta.trim());

    // 默认周五
    Integer priority = (Integer) metas.getOrDefault("priority_i", 5);

    // 优先级
    return new Task(priority, status, name, tags, metas, body);
  }

  private static Map<String, Object> parseMetas(String meta) {
    List<String> kv = Arrays.stream(meta.split(" "))
        .filter(it -> !it.startsWith("@"))
        .collect(Collectors.toList());

    Map<String, Object> ret = new HashMap<>();
    for (int i = 0; i < kv.size(); i += 2) {
      String key = kv.get(i).substring(1, kv.get(i).length() - 1);
      String val = kv.get(i + 1).substring(1, kv.get(i + 1).length() - 1);

      if (key.endsWith("_i")) {
        ret.put(key, Integer.parseInt(val));
        continue;
      }
      if (key.endsWith("_at")) {
        ret.put(key, parseDate(val));
      }
    }

    return ret;
  }

  private static Date parseDate(String raw) {
    // 2019-11-11
    if (raw.length() < 10) {
      return null;
    }
    if (raw.length() == 10) {
      raw += "T00:00:00";
    }

    if (raw.length() == 13) {
      raw += ":00:00";
    }

    if (raw.length() == 16) {
      raw += ":00";
    }

    if (raw.length() == 19) {
      return Utils.parse(raw);
    }
    return null;
  }

  private static List<String> parseTags(String meta) {
    return Arrays.stream(meta.split(" "))
        .filter(it -> it.startsWith("@"))
        .collect(Collectors.toList());
  }

  private static Task parseNoBody(String raw) {
    return parse(raw, "");
  }

  public static Task newDefault(String name) {
    HashMap<String, Object> metas = new HashMap<>();
    metas.put("priority_i", 5);
    return new Task(5, "INBOX", name, new ArrayList<>(), metas, "");
  }

  public static Task newDefault(String name, List<String> tags) {
    HashMap<String, Object> metas = new HashMap<>();
    metas.put("priority_i", 5);
    return new Task(5, "INBOX", name, tags, metas, "");
  }

  public Task todo() {
    return new Task(this.id, "TODO", this.name, this.tags, this.metas, this.note);
  }

  public Task doing() {
    return new Task(this.id, "DOING", this.name, this.tags, this.metas, this.note);
  }

  public Task done() {
    return new Task(this.id, "DONE", this.name, this.tags, this.metas, this.note);
  }

  public Task inbox() {
    return new Task(this.id, "INBOX", this.name, this.tags, this.metas, this.note);
  }
}
