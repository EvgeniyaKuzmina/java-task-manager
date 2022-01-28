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
        return Objects.equals(epicId, subTask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", name=" + name +
                ", id=" + id +
                ", description=" + description +
                ", status=" + status +
                "}\n";
    }
}
