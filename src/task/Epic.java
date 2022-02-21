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

    public static Epic fromString(String value, List<SubTask> subtasks) {
        String[] splitedLine = value.split(",");
        Long id = Long.parseLong(splitedLine[0]);
        String name = splitedLine[2];
        String description = splitedLine[4];
        String status = splitedLine[3];
        return new Epic(id, name, description, subtasks, Status.toStatus(status));

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
        return String.format("%d,%s,%s,%s,%s\n", id, TasksType.EPIC, name, status, description);
    }


}
