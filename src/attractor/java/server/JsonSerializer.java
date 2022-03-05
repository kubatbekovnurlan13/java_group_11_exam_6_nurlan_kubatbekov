package attractor.java.server;

import attractor.java.dataModels.Day;
import attractor.java.dataModels.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JsonSerializer {
    private final static Gson gson = new Gson();

    public static List<Task> getTasksOfDay(int idDay) {
        Type listType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        try (Reader reader = new FileReader("data/jsonFiles/Tasks.json")) {
            List<Task> tasks = gson.fromJson(reader, listType);
            return tasks.stream().filter(x -> x.getIdDay() == idDay).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Task> getTasks() {
        Type listType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        try (Reader reader = new FileReader("data/jsonFiles/Tasks.json")) {
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Day> getDays() {
        Type listType = new TypeToken<ArrayList<Day>>() {
        }.getType();
        try (Reader reader = new FileReader("data/jsonFiles/Days.json")) {
            List<Day> days = gson.fromJson(reader, listType);
            setDate(days);
            return days;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setDate(List<Day> days) {
        AtomicInteger y = new AtomicInteger(1);
        LocalDate todaydate = LocalDate.now();

        days.forEach(
                x -> {
                    x.setDate(todaydate.withDayOfMonth(y.get()));
                    x.setDayOfWeek(DayOfWeek.from(todaydate.withDayOfMonth(y.get())));
                    x.setToday(todaydate == x.getDate());
                    y.getAndIncrement();
                });
    }

    public static void deleteTask(int idOfTask) {
        List<Task> tasks = getTasks();
        assert tasks != null;
        tasks.remove(idOfTask - 1);

        Task[] tasksArray = tasks.toArray(new Task[0]);
        Gson gson = new Gson();
        try (Writer writer = new FileWriter("data/jsonFiles/Tasks.json")) {
            String json = gson.toJson(tasksArray);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeTask(Task task) {
        List<Task> tasks = getTasks();
        assert tasks != null;
        task.setIdTask(tasks.size()+1);
        tasks.add(task);

        Task[] taskArr = tasks.toArray(new Task[0]);
        Gson gson = new Gson();
        try (Writer writer = new FileWriter("data/jsonFiles/Tasks.json")) {
            String json = gson.toJson(taskArr);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
