import java.util.ArrayList;
import java.util.List;

//управляет задачами
public class ManagerOfTasks {

    private List<Task> taskManager = new ArrayList<>();


    // добавляем новую задачу и подзадачу
    public Object addNewTask(Task newTask) {
        for (Task task : taskManager) {
            if (task.equals(newTask)) {
                System.out.println("Такая задача уже есть");
                return taskManager;
            }
        }
        return taskManager.add(newTask);
    }

    // получаем список всех задач
    public List<String> getListOfTask() {
        List<String> listOfTAsk = new ArrayList<>();
        for (Task task : taskManager) {
            listOfTAsk.add(task.getName());
        }
        return listOfTAsk;
    }

    // Получение списка всех подзадач
    public List<String> getListOfSubTask() {
        List<String> listOfSubTask = new ArrayList<>();
        for (Task task : taskManager) {
            List<SubTask> subTasks = task.getDescription();
            for (SubTask subTask : subTasks) {
                listOfSubTask.add(subTask.getDescriptionSubTask());
            }
        }
        return listOfSubTask;
    }

    // Получение списка всех подзадач определённого эпика.
    public List<SubTask> getListOfSubTaskByEpic(String nameEpic) {
        List<SubTask> getSubTask = new ArrayList<>();
        for (Task task : taskManager) {
            if (task.getName().equals(nameEpic)) {
                getSubTask = task.getDescription();
            }
        }
        return getSubTask;
    }

    //Получение эпика и подзадачи по идентификатору.
    public Object getTaskById(TaskID id) {
        Object getTask = new Object();
        for (Task task : taskManager) {
            if (task.getId().equals(id)) {
                getTask = task;
            }
        }
        return getTask;
    }

    //Обновление эпика и подзадачи по идентификатору. Новая версия объекта передаётся в виде параметра.
    public void updateTaskById(Task updateTask) {
        for (Task task : taskManager) {
            if (task.getId().equals(updateTask.getId())) {
                task.setName(updateTask.getName());
                task.setDescription(updateTask.getDescription());
                task.setStatus(updateTask.getStatus());
            }
        }
    }

    // Удаление всех ранее добавленных задач
    public void removeAllTask() {
        taskManager.clear();
    }

    // Удаление ранее добавленных задач по ID
    public void removeTaskById(TaskID id) {
        for (int i = 0; i < taskManager.size(); i++) {
            if (taskManager.get(i).getId().equals(id)) {
                taskManager.remove(i);
            }
        }
    }
}
