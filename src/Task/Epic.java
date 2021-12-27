package Task;

import java.util.List;
import java.util.Objects;

//Данные о задаче. Задача может включать или не включать в себя подзадачи
public class Epic extends Task {

    private final TaskID id;
    private List<SubTask> subtaskOfEpic;


    public Epic(TaskID id, String name, List<SubTask> subtaskOfEpic, Status status) {
        super(name, status);
        this.id = id;
        this.subtaskOfEpic = subtaskOfEpic;

    }

    public List<SubTask> getSubtaskOfEpic() {
        return subtaskOfEpic;
    }

    public void setSubtaskOfEpic(List<SubTask> subtaskOfEpic) {
        this.subtaskOfEpic = subtaskOfEpic;
    }

    public TaskID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getId(), epic.getId()) &&
                Objects.equals(getName(), epic.getName()) &&
                Objects.equals(getSubtaskOfEpic(), epic.getSubtaskOfEpic()) &&
                Objects.equals(getStatus(), epic.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName());
    }

    @Override
    public String toString() {
        if (subtaskOfEpic.isEmpty()) {
            return "Task.Epic{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", status=" + status +
                    '}';
        }
        return "Task.Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description=" + subtaskOfEpic +
                ", status=" + status +
                '}';
    }
}
