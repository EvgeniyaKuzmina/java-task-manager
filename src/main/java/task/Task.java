package task;

import exceptions.WrongArgumentException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Task {

    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    protected final Long id;
    protected String name;
    protected String description;
    protected Status status;
    protected LocalDate startTime;
    protected Duration duration;

    public Task(Long id, String name, String description, Status status, int year, int months, int day, int durationInHours) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        if (durationInHours >= 0 && year > 0 && (months > 0 && months <= 12) && (day > 0 && day <= 31)) {
            if (LocalDate.of(year, months, day).isBefore(LocalDate.now())) {
                throw new WrongArgumentException(
                        "неверно ведённые значения для даты старта. Дата старта должна быть " +
                                "такая же или позднее текущей даты " + LocalDate.now().format(FORMATTER));
            } else {
                startTime = LocalDate.of(year, months, day);
                duration = Duration.ofHours(durationInHours);
            }
        } else {
            throw new WrongArgumentException(
                    "неверно ведённые значения для даты старта или продолжительности задачи");
        }

    }

    // получаем список года, месяца и дня из даты старта
    public static List<Integer> getYearMonthDatFromLocalDate(LocalDate startDate) {
        int year = Integer.parseInt(startDate.format(FORMATTER).split("\\.")[2]);
        int month = Integer.parseInt(startDate.format(FORMATTER).split("\\.")[1]);
        int day = Integer.parseInt(startDate.format(FORMATTER).split("\\.")[0]);
        return List.of(year, month, day);
    }

    public static Task fromString(String value) {
        String[] splitedLine = value.trim().split(",");
        Long id = Long.parseLong(splitedLine[0]);
        String name = splitedLine[2];
        String description = splitedLine[4];
        String status = splitedLine[3];
        String[] time = splitedLine[5].split("\\.");
        int year = Integer.parseInt(time[2]);
        int month = Integer.parseInt(time[1]);
        int day = Integer.parseInt(time[0]);
        int duration = Integer.parseInt(splitedLine[6]);
        return new Task(id, name, description, Status.toStatus(status), year, month, day, duration);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = Duration.ofHours(duration);
    }

    protected String formatStartTimeToString() {
        return startTime.format(FORMATTER);
    }

    protected String formatDurationToString() {
        return String.format("%s", duration.toHours());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s\n", id, TasksType.TASK, name, status, description,
                             formatStartTimeToString(), formatDurationToString());

    }
}