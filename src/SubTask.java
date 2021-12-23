import java.util.Objects;

//данные о подзадаче
public class SubTask {

    private final String descriptionSubTask;
    private final Status status;

    public SubTask(String descriptionSubTask, Status status) {
        this.descriptionSubTask = descriptionSubTask;
        this.status = status;
    }

    public String getDescriptionSubTask() {
        return descriptionSubTask;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask task = (SubTask) o;
        return Objects.equals(getDescriptionSubTask(), task.getDescriptionSubTask()) &&
                Objects.equals(getStatus(), task.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescriptionSubTask(), getStatus());
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "descriptionSubTask='" + descriptionSubTask + '\'' +
                ", status=" + status +
                "}\n";
    }
}
