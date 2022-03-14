package task;

import java.util.Objects;

//данные о подзадаче
public class SubTask extends Task {
    private final Long epicId;

    public SubTask(Long epicId, Long id, String name, String description, Status status, int year, int months,
                   int day, int durationInHours) {
        super(id, name, description, status, year, months, day, durationInHours);
        this.epicId = epicId;
    }

    public static SubTask fromString(String value) {
        String[] splitedLine = value.trim().split(",");
        Long id = Long.parseLong(splitedLine[0]);
        String name = splitedLine[2];
        String description = splitedLine[4];
        String status = splitedLine[3];
        Long idEpic = Long.parseLong(splitedLine[7]);
        String[] time = splitedLine[5].split("\\.");
        int year = Integer.parseInt(time[2]);
        int month = Integer.parseInt(time[1]);
        int day = Integer.parseInt(time[0]);
        int duration = Integer.parseInt(splitedLine[6]);
        return new SubTask(idEpic, id, name, description, Status.toStatus(status), year, month, day, duration);
    }

    public Long getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof SubTask)) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(epicId, subTask.epicId) &&
                Objects.equals(name, subTask.name) &&
                Objects.equals(description, subTask.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%d\n", id, TasksType.SUBTASK, name, status, description,
                             formatStartTimeToString(), formatDurationToString(), epicId);
    }
}
