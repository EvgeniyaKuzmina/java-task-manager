package task;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

//данные о главной задаче
public class Epic extends Task {

    private List<SubTask> subtasks;

    public Epic(Long id, String name, String description, List<SubTask> subtasks, Status status, int year,
                int months, int day, int durationInHours) {

        super(id, name, description, status, year, months, day, durationInHours);
        this.subtasks = subtasks;

    }

    public static Epic fromString(String value, List<SubTask> subtasks) {
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
        return new Epic(id, name, description, subtasks, Status.toStatus(status),year, month, day, duration);

    }

    public List<SubTask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<SubTask> subtasks) {
        this.subtasks = subtasks;
    }

    public static int getDurationForEpic(List<SubTask> subtasks) {
        if (subtasks.isEmpty()) {
            return 0;
        }
        int duration = 0;
        for (SubTask subTask : subtasks) {
            duration += subTask.getDuration().toHours();
        }
        return duration;
    }

    public static LocalDate getDataStartForEpic(List<SubTask> subtasks) {
        if (subtasks.isEmpty()) {
            return LocalDate.now(); // если список задач пусть, указывается дата старта сегодня.
            // По ТЗ не указано, какая должна быть дата, если подзадач нет, поэтому реализовала таким образом
        }
        LocalDate startTime = subtasks.get(0).getStartTime();
        for (SubTask subtask : subtasks) {
            if (startTime.isAfter(subtask.getStartTime()))
                startTime = subtask.getStartTime();
        }
        return startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return  Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s\n", id, TasksType.EPIC, name, status, description,
                             formatStartTimeToString(), formatDurationToString());
    }

}
