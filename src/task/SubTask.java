package task;

import java.util.Objects;

//данные о подзадаче
public class SubTask extends Task {
    private final Long epicId;

    public SubTask(Long epicId, Long id, String name, String description, Status status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Long getEpicId() {
        return epicId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null) return false;
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


    public static SubTask fromString(String value) {
        String[] splitedLine = value.split(",");
        Long id = Long.parseLong(splitedLine[0]);
        String name = splitedLine[2];
        String description = splitedLine[4];
        String status = splitedLine[3];
        Long idEpic = Long.parseLong(splitedLine[5]);
        return new SubTask(idEpic, id,name,description, Status.toStatus(status));

    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d\n", id, TasksType.SUBTASK, name, status, description, epicId);
    }
}
