package task;

//данные о подзадаче
public class SubTask extends Task {
    private final TaskID epicId;

    public SubTask(String name, Status status, TaskID epicId) {
        super(name, status);
        this.epicId = epicId;
    }

    public TaskID getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
