package task;

//данные о подзадаче
public class SubTask extends Task {


    public SubTask(Long id, String name, String description, Status status) {
        super(id, name, description, status);
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
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                "}\n";
    }
}
