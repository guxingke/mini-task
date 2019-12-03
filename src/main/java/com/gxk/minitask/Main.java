package com.gxk.minitask;

import static com.gxk.minitask.core.TaskCmd.newTask;

import com.gxk.minitask.af.OutUtils;
import com.gxk.minitask.af.output.FeedBack;
import com.gxk.minitask.af.output.Icon;
import com.gxk.minitask.af.output.Item;
import com.gxk.minitask.af.output.Mod;
import com.gxk.minitask.af.output.Mods;
import com.gxk.minitask.af.output.Variables;
import com.gxk.minitask.core.Task;
import com.gxk.minitask.core.TaskCmd;
import com.gxk.minitask.util.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

  public static void main(String[] args) throws Exception {
    Main main = new Main();
    System.out.print(main.exec(args));
  }

  public FeedBack exec(String... args) throws IOException {
    // args parser
    String action = "inbox";
    if (args.length >= 1) {
      action = args[0];
    }

    List<Task> tasks = new ArrayList<>();
    boolean needFilter = true;
    switch (action) {
      case "inbox":
        tasks = TaskCmd.tasks("inbox");
        break;
      case "week":
        String name = Utils.currentWeekFormat();
        tasks = TaskCmd.tasks(name);
        break;
      case "new-int":
        tasks = Arrays.asList(TaskCmd.newTaskInteractive());
        needFilter = false;
        break;
      case "new":
        String line = Arrays.stream(args).collect(Collectors.joining(" "));
        String raw = line.substring(4);
        tasks = Arrays.asList(newTask(raw));
        needFilter = false;
        break;
      case "mig":
        // INBOX -> TODO
        // TODO -> DOING
        // DOING -> DONE
        if (args.length != 3) {
          break;
        }
        switch (args[1]) {
          case "INBOX":
            tasks = Arrays.asList(TaskCmd.migTaskFromInbox(Integer.parseInt(args[2])));
            break;
          case "TODO":
            tasks = Arrays.asList(TaskCmd.migTaskFromTODO(Integer.parseInt(args[2])));
            break;
          case "DOING":
            tasks = Arrays.asList(TaskCmd.migTaskFromDOING(Integer.parseInt(args[2])));
            break;
          case "DONE":
            tasks = Arrays.asList(TaskCmd.migTaskFromDONE(Integer.parseInt(args[2])));
            break;
        }

        needFilter = false;
        break;
      case "shift-mig":
        if (args.length != 3) {
          break;
        }
        switch (args[1]) {
          case "DONE":
            tasks = Arrays.asList(TaskCmd.shiftmigTaskFromDONE(Integer.parseInt(args[2])));
            break;
          case "DOING":
            tasks = Arrays.asList(TaskCmd.shiftmigTaskFromDOING(Integer.parseInt(args[2])));
            break;
          case "TODO":
            tasks = Arrays.asList(TaskCmd.shiftmigTaskFromTODO(Integer.parseInt(args[2])));
            break;
          case "INBOX":
            tasks = Arrays.asList(TaskCmd.shiftmigTaskFromINBOX(Integer.parseInt(args[2])));
            break;
        }
        break;
      default:
        break;
    }

    if (tasks.isEmpty()) {
      return OutUtils.notFound();
    }

//    2
    List<Item> items = getItems(tasks);
    if (items.isEmpty()) {
      return OutUtils.notFound();
    }

    if (!needFilter) {
      return new FeedBack(items);
    }

//   3
    String arg = null;
    if (args.length >= 2) {
      arg = args[1];
    }

    if (arg == null || arg.length() < 2) {
      return new FeedBack(items);
    }

    List<Item> rets = filterItems(arg, items);
    if (rets.isEmpty()) {
      return OutUtils.notFound();
    }
    return new FeedBack(rets);
  }

  private List<Item> filterItems(String arg, List<Item> items) {
    return items.stream()
        .map(it -> new ScoreHolder(it, fuzzyScore(it.getTitle(), arg)))
        .filter(it -> it.getScore() > 3)
        .sorted((pre, next) -> next.getScore().compareTo(pre.getScore()))
        .map(ScoreHolder::getItem)
        .limit(2)
        .collect(Collectors.toList());
  }

  private List<Item> getItems(List<Task> tasks) {
    int whichDay = Utils.whichDayInCurrentWeek();
    return tasks.stream()
        .map(it -> {
          Icon icon = new Icon("static/images/todo.png");
          switch (it.status) {
            case "TODO":
              if (it.priority <= whichDay) {
                icon = new Icon("static/images/todo2.png");
              }
              break;
            case "INBOX":
              icon = new Icon("static/images/inbox.png");
              break;
            case "DOING":
              icon = new Icon("static/images/doing.png");
              break;
            case "DONE":
              icon = new Icon("static/images/done.png");
              break;
          }
          Variables vars = new Variables();
          vars.put("name", it.name);
//          vars.put("note", it.note.replaceAll("\n", "\\\\n"));
          String st = it.priority + " " + String.join(" ", it.tags);
          Mods mods = new Mods(new Mod("./minitask shift-mig " + it.status + " " + it.id, "shift mig"));
          Item item = new Item(String.valueOf(it.id), it.name, st, "./minitask mig " + it.status + " " + it.id, icon, vars, mods);
          return item;
        }).collect(Collectors.toList());
  }

  public Integer fuzzyScore(final CharSequence term, final CharSequence query) {
    if (term == null || query == null) {
      return -1;
    }

    // fuzzy logic is case insensitive. We normalize the Strings to lower
    // case right from the start. Turning characters to lower case
    // via Character.toLowerCase(char) is unfortunately insufficient
    // as it does not accept a locale.
    final String termLowerCase = term.toString().toLowerCase();
    final String queryLowerCase = query.toString().toLowerCase();

    // the resulting score
    int score = 0;

    // the position in the term which will be scanned next for potential
    // query character matches
    int termIndex = 0;

    // index of the previously matched character in the term
    int previousMatchingCharacterIndex = Integer.MIN_VALUE;

    for (int queryIndex = 0; queryIndex < queryLowerCase.length(); queryIndex++) {
      final char queryChar = queryLowerCase.charAt(queryIndex);

      boolean termCharacterMatchFound = false;
      for (; termIndex < termLowerCase.length()
          && !termCharacterMatchFound; termIndex++) {
        final char termChar = termLowerCase.charAt(termIndex);

        if (queryChar == termChar) {
          // simple character matches result in one point
          score++;

          // subsequent character matches further improve
          // the score.
          if (previousMatchingCharacterIndex + 1 == termIndex) {
            score += 2;
          }

          previousMatchingCharacterIndex = termIndex;

          // we can leave the nested loop. Every character in the
          // query can match at most one character in the term.
          termCharacterMatchFound = true;
        }
      }
    }

    return score;
  }

  static class ScoreHolder {

    private final Item item;
    private final Integer score;

    ScoreHolder(Item item, Integer score) {
      this.item = item;
      this.score = score;
    }

    public Item getItem() {
      return item;
    }

    public Integer getScore() {
      return score;
    }
  }
}
