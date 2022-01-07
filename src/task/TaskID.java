package task;

// формирует идентификационный номер для задачи
public class TaskID {

    private static long id = 0;

    public TaskID() {
        id++;
    }

    public static long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TaskID{id=" + id + "}";
    }
}
