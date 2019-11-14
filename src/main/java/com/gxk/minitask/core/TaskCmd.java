package com.gxk.minitask.core;

import com.gxk.minitask.util.Utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TaskCmd {

  // input.md
  public static List<Task> tasks(String group, String... status) throws IOException {
    List<Task> tasks = TaskCmd.taskAll(group);
    List<Predicate<Task>> predicates = Arrays.stream(status)
        .map(it -> (Predicate<Task>) test -> Objects.equals(it, test.status)).collect(Collectors.toList());
    // sort
    return sort(tasks, predicates);
  }

  private static List<Task> taskAll(String group) throws IOException {
    Path input = getPath(group);
    List<Task> tasks = TaskParser.parse(input);
    return tasks;
  }

  private static Path getPath(String group) {
    Path path = Paths.get(System.getProperty("user.home"), "config", "minitask");
    return path.resolve(group + ".md");
  }

  private static List<Task> sort(List<Task> tasks) {
    return TaskCmd.sort(tasks, new ArrayList<>());
  }

  private static List<Task> sort(List<Task> tasks, List<Predicate<Task>> predicates) {
    Stream<Task> stream = tasks.stream();
    for (Predicate<Task> predicate : predicates) {
      stream = stream.filter(predicate);
    }
    Map<String, List<Task>> map = stream.collect(Collectors.groupingBy(it -> it.status));
    List<Task> doing = map.getOrDefault("DOING", new ArrayList<>());
    List<Task> todo = map.getOrDefault("TODO", new ArrayList<>());
    List<Task> inbox= map.getOrDefault("INBOX", new ArrayList<>());
    List<Task> done = map.getOrDefault("DONE", new ArrayList<>());

    doing.sort(Comparator.comparingInt(it -> it.priority));
    todo.sort(Comparator.comparingInt(it -> it.priority));
    done.sort(Comparator.comparingInt(it -> it.priority));
    inbox.sort(Comparator.comparingInt(it -> it.priority));

    return Stream.of(doing, todo, inbox, done).flatMap(Collection::stream).collect(Collectors.toList());
  }

  public static Task newTaskInteractive() throws IOException {
    Scanner scanner = new Scanner(System.in);
    System.out.println("λ> enter to use default, or put some word replace it.");
    System.out.println("λ> task name: [default uuid]");
    String name = scanner.nextLine();
    System.out.println("λ> task tags: [default work]");
    String tags = scanner.nextLine();
//    System.out.println("λ> due at: [default this friday, support num{d,w,m}, e.g 2w => 2 week later]");
//    String due = scanner.nextLine();

    if (name.trim().length() == 0) {
      name = UUID.randomUUID().toString();
    }
    List<String> tagList = Arrays.asList("@work");
    if (tags.trim().length() != 0) {
      tagList = Arrays.stream(tags.replaceAll("[' ']+", " ").split(" ")).map(it -> "@" + it)
          .collect(Collectors.toList());
    }

    Task task = Task.newDefault(name, tagList);

    // save
    TaskCmd.save("inbox", task);

    return task;
  }

  private static void save(String group, Task task) throws IOException {
    List<Task> tasks = TaskCmd.taskAll(group);
    tasks.add(task);
    List<Task> sortedTasks = TaskCmd.sort(tasks);

    saveAll(group, sortedTasks);
  }

  private static void saveAll(String group, List<Task> tasks) throws IOException {
    List<Task> sortedTasks = TaskCmd.sort(tasks);

    String listRaw = sortedTasks.stream()
        .map(Task::writeObject)
        .collect(Collectors.joining("\n\n---\n\n"));

    Path path = getPath(group);
    if (!path.toFile().exists()) {
      Files.createFile(path);
    }
    Files.write(path, listRaw.getBytes());
  }

  // xxx asldkfjlaks alksdfjlaksdjfkl ||| @xxx @xxx
  public static Task newTask(String raw) throws IOException {
    if (!raw.contains("|||")) {
      return Task.newDefault(raw);
    }

    String[] split = raw.split("\\|\\|\\|");

    String name = split[0].trim();
    String meta = split[1].trim();

    List<String> tags = parseTags(meta);

    Task task = Task.newDefault(name, tags);
    // save
    TaskCmd.save("inbox", task);
    return task;
  }

  private static Integer parsePriority(String meta) {
    List<String> tmp = Arrays.stream(meta.split(" "))
        .filter(it -> !it.startsWith("@"))
        .collect(Collectors.toList());

    for (int i = 0; i < tmp.size(); i += 2) {
      if (Objects.equals(tmp.get(i), ":p")) {
        try {
          return Integer.parseInt(tmp.get(i + 1));
        } catch (Exception e) {
          return 5;
        }
      }
    }
    return 5;
  }

  private static List<String> parseTags(String meta) {
    return Arrays.stream(meta.split(" "))
        .filter(it -> it.startsWith("@"))
        .collect(Collectors.toList());
  }

  public static Task migTaskFromInbox(int id) throws IOException {
    List<Task> tasks = taskAll("inbox");
    Optional<Task> opt = tasks.stream()
        .filter(it -> it.id == id)
        .findFirst();
    if (!opt.isPresent()) {
      return null;
    }
    Task task = opt.get(); tasks.remove(task);

    List<Task> weekTasks = taskAll(Utils.currentWeekFormat());

    Task newTask = task.todo();
    weekTasks.add(newTask);

    saveAll("inbox", tasks);
    saveAll(Utils.currentWeekFormat(), weekTasks);
    return newTask;
  }

  public static Task migTaskFromTODO(int id) throws IOException {
    List<Task> tasks = taskAll(Utils.currentWeekFormat());
    Optional<Task> optionalTask = tasks.stream()
        .filter(it -> it.id == id)
        .findFirst();
    if (!optionalTask.isPresent()) {
      return null;
    }
    Task task = optionalTask.get();
    tasks.remove(task);

    Task newTask = task.doing();
    tasks.add(newTask);
    saveAll(Utils.currentWeekFormat(), tasks);
    return newTask;
  }

  public static Task migTaskFromDOING(int id) throws IOException {
    List<Task> tasks = taskAll(Utils.currentWeekFormat());
    Optional<Task> optionalTask = tasks.stream()
        .filter(it -> it.id == id)
        .findFirst();
    if (!optionalTask.isPresent()) {
      return null;
    }
    Task task = optionalTask.get();
    tasks.remove(task);

    Task newTask = task.done();
    tasks.add(newTask);
    saveAll(Utils.currentWeekFormat(), tasks);
    return newTask;
  }

  public static Task migTaskFromDONE(int id) throws IOException {
    List<Task> tasks = taskAll(Utils.currentWeekFormat());
    Optional<Task> optionalTask = tasks.stream()
        .filter(it -> it.id == id)
        .findFirst();
    if (!optionalTask.isPresent()) {
      return null;
    }
    return optionalTask.get();
  }

  public static Task shiftmigTaskFromDONE(int id) throws IOException {
    List<Task> tasks = taskAll(Utils.currentWeekFormat());
    Optional<Task> optionalTask = tasks.stream()
        .filter(it -> it.id == id)
        .findFirst();
    if (!optionalTask.isPresent()) {
      return null;
    }

    Task task = optionalTask.get();
    tasks.remove(task);

    Task newTask = task.doing();
    tasks.add(newTask);
    saveAll(Utils.currentWeekFormat(), tasks);
    return newTask;
  }

  public static Task shiftmigTaskFromDOING(int id) throws IOException {
    List<Task> tasks = taskAll(Utils.currentWeekFormat());
    Optional<Task> optionalTask = tasks.stream()
        .filter(it -> it.id == id)
        .findFirst();
    if (!optionalTask.isPresent()) {
      return null;
    }

    Task task = optionalTask.get();
    tasks.remove(task);

    Task newTask = task.todo();
    tasks.add(newTask);
    saveAll(Utils.currentWeekFormat(), tasks);
    return newTask;
  }

  public static Task shiftmigTaskFromTODO(int id) throws IOException {
    List<Task> tasks = taskAll(Utils.currentWeekFormat());
    Optional<Task> optionalTask = tasks.stream()
        .filter(it -> it.id == id)
        .findFirst();
    if (!optionalTask.isPresent()) {
      return null;
    }

    Task task = optionalTask.get();
    tasks.remove(task);
    Task newTask = task.inbox();
    List<Task> inbox= taskAll("inbox");
    inbox.add(newTask);

    saveAll(Utils.currentWeekFormat(), tasks);
    saveAll("inbox", inbox);
    return newTask;
  }

  public static Task shiftmigTaskFromINBOX(int id) throws IOException {
    List<Task> tasks = taskAll("inbox");
    Optional<Task> optionalTask = tasks.stream()
        .filter(it -> it.id == id)
        .findFirst();
    if (!optionalTask.isPresent()) {
      return null;
    }
    return optionalTask.get();
  }
}
