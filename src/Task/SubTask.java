package Task;

//данные о подзадаче
public class SubTask extends Task {

    public SubTask (String name, Status status) {
        super(name, status);
    }

    @Override
    public String toString() {
        return "Task.SubTask{" +
                "descriptionSubTask='" + name + '\'' +
                ", status=" + status +
                "}\n";
    }
}
