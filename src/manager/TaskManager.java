package manager;

import task.Epic;
import task.SubTask;
import task.TaskID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//управляет задачами
public class TaskManager {

    private List<Epic> epic = new ArrayList<>();

    // добавляем новую задачу и подзадачу
    public void addNewTask(Epic newEpic) {
        for (Epic epic : epic) {
            if (epic.equals(newEpic)) {
                System.out.println("Такая задача уже есть");
            }
        }
        epic.add(newEpic);
    }

    // получаем список всех задач
    public List<String> getTasksNames() {
        List<String> taskName = new ArrayList<>();
        for (Epic epic : epic) {
            taskName.add(epic.getName());
        }
        return taskName;
    }

    // Получение списка всех подзадач
    public List<String> getSubtasksName() {
        List<String> subTaskName = new ArrayList<>();
        for (Epic epic : epic) {
            List<SubTask> subTasks = epic.getSubTasks();
            for (SubTask subTask : subTasks) {
                subTaskName.add(subTask.getName());
            }
        }
        return subTaskName;
    }

    // Получение списка всех подзадач определённого эпика.
    public List<SubTask> getSubtasksByEpicName(String epicName) {
        List<SubTask> getSubTask = new ArrayList<>();
        for (Epic epic : epic) {
            if (epic.getName().equals(epicName)) {
                getSubTask = epic.getSubTasks();
            }
        }
        return getSubTask;
    }

    //Получение эпика и подзадачи по идентификатору.
    public HashMap getTaskById(TaskID id) {
        HashMap<String, List<String>> tasks = new HashMap<>();
        for (Epic epic : epic) {
            if (epic.getId().equals(id)) {
                List<String> subtasks = new ArrayList<>();
                for (SubTask subtask : epic.getSubTasks()) {
                    subtasks.add(subtask.getName());
                }
                tasks.put(epic.getName(), subtasks);
            }
        }
        return tasks;
    }

    //Обновление эпика и подзадачи по идентификатору. Новая версия объекта передаётся в виде параметра.
    public void updateEpic(Epic epicUpdate) {
        for (Epic epic : epic) {
            if (epic.getId().equals(epicUpdate.getId())) {
                epic.setName(epicUpdate.getName());
                epic.setSubTasks(epicUpdate.getSubTasks());
                epic.setStatus(epicUpdate.getStatus());
            }
        }
    }

    // Удаление всех ранее добавленных задач
    public void removeAllTask() {
        epic.clear();
    }

    // Удаление ранее добавленных задач по ID
    public void removeTaskById(TaskID id) {
        for (int i = 0; i < epic.size(); i++) {
            if (epic.get(i).getId().equals(id)) {
                epic.remove(i);
            }
        }
    }
}
