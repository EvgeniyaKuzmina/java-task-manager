package task;

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
