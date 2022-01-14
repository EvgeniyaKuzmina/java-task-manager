package task;

import java.util.List;
import java.util.Objects;

//данные о главной задаче
public class Epic extends Task {

    private List<SubTask> subtasks;

    public Epic(Long id, String name, String description, List<SubTask> subtasks, Status status) {
        super(id, name, description, status);
        this.subtasks = subtasks;

    }

    public List<SubTask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<SubTask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subtasks.equals(epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return "Epic{id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", subtasks=" + subtasks +
                ", status=" + status +
                "}\n";
    }


}
