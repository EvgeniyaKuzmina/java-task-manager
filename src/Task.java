import java.util.List;
import java.util.Objects;

//Данные о задаче. Задача может включать или не включать в себя подзадачи
public class Task {

    private final TaskID id;
    private String name;
    private List<SubTask> description;
    private Status status;

    public Task(TaskID id, String name, List<SubTask> description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = getStatusForEpic();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubTask> getDescription() {
        return description;
    }

    public void setDescription(List<SubTask> description) {
        this.description = description;
    }

    public TaskID getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    //получаем статус для задачи
    public Status getStatusForEpic() {
        boolean statusNew = false;
        boolean statusDone = false;
        List<SubTask> subTasks = getDescription();
        if (subTasks.size() == 0) {
            return Status.NEW;
        }
        for (SubTask subtask : subTasks) {
            if (subtask.getStatus().equals(Status.NEW)) {
                statusNew = true;
            } else if (subtask.getStatus().equals(Status.DONE)) {
                statusDone = true;
            }
        }
        if (statusNew && statusDone){
            return Status.IN_PROGRESS;
        } else if (!statusNew && statusDone) {
            return Status.DONE;
        }else {
            return Status.NEW;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(getId(), task.getId()) &&
                Objects.equals(getName(), task.getName()) &&
                Objects.equals(getDescription(), task.getDescription()) &&
                Objects.equals(getStatus(), task.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName());
    }

    @Override
    public String toString() {
        if (description.isEmpty()) {
            return "Task{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", status=" + status +
                    '}';
        }
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description=" + description +
                ", status=" + status +
                '}';
    }
}
