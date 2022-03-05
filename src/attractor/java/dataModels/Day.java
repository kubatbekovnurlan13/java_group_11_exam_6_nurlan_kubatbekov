package attractor.java.dataModels;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Day {
    private int idDay;
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private boolean today;
    private List<Map<String, Integer>> tasks;

    public Day(int idDay, LocalDate date, DayOfWeek dayOfWeek, boolean today, List<Map<String, Integer>> tasks) {
        this.idDay = idDay;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.today = today;
        this.tasks = tasks;
    }

    public int getIdDay() {
        return idDay;
    }

    public LocalDate getDate() {
        return date;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public boolean isToday() {
        return today;
    }

    public List<Map<String, Integer>> getTasks() {
        return tasks;
    }

    public void setIdDay(int idDay) {
        this.idDay = idDay;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setToday(boolean today) {
        this.today = today;
    }

    public void setTasks(List<Map<String, Integer>> tasks) {
        this.tasks = tasks;
    }
}

