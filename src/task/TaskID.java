package task;

// формирует идентификационный номер для задачи
public class TaskID {

    private long id;

    public TaskID() {
        id = 0;
    }

    public  long getId() {
        return ++id;
    }

    @Override
    public String toString() {
        return "TaskID{id=" + id + "}";
    }
}
