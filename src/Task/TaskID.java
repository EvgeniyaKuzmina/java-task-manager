package Task;

import java.util.Random;

// формирует идентификационный номер для задачи.
public class TaskID {

    private final Integer id;
    private final Random random = new Random();

    public TaskID() {
        id = Math.abs(random.nextInt());
    }

    @Override
    public String toString() {
        return "Task.TaskID{" +
                "id=" + id +
                '}';
    }
}
