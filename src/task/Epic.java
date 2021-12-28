package task;

import java.util.List;
import java.util.Objects;

//Данные о задаче. Задача может включать или не включать в себя подзадачи
public class Epic extends Task {

    private final TaskID id;
    private List<SubTask> subTasks;

    public Epic(TaskID id, String name, List<SubTask> subTasks, Status status) {
        super(name, status);
        this.id = id;
        this.subTasks = subTasks;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
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
                Objects.equals(getSubTasks(), epic.getSubTasks()) &&
                Objects.equals(getStatus(), epic.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName());
    }

    @Override
    public String toString() {
        if (subTasks.isEmpty()) {
            return "Task.Epic{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", status=" + status +
                    '}';
        }
        return "Task.Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description=" + subTasks +
                ", status=" + status +
                '}';
    }
}
