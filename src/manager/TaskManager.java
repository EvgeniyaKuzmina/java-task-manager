package manager;

import task.Epic;
import task.SubTask;
import task.TaskID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//управляет задачами
public class TaskManager {

    private List<Epic> epics = new ArrayList<>();

    // добавляем новую задачу и подзадачу
    public void addNewTask(Epic newEpic) {
        for (Epic epic : epics) {
            if (epic.equals(newEpic)) {
                System.out.println("Такая задача уже есть");
            }
        }
        epics.add(newEpic);
    }

    // получаем список всех задач
    public List<String> getTasksNames() {
        List<String> tasksName = new ArrayList<>();
        for (Epic epic : epics) {
            tasksName.add(epic.getName());
        }
        return tasksName;
    }

    // Получение списка всех подзадач
    public List<String> getSubtasksName() {
        List<String> subTasksName = new ArrayList<>();
        for (Epic epic : epics) {
            List<SubTask> subTasks = epic.getSubTasks();
            for (SubTask subTask : subTasks) {
                subTasksName.add(subTask.getName());
            }
        }
        return subTasksName;
    }

    // Получение списка всех подзадач определённого эпика.
    public List<SubTask> getSubtasksByEpicName(String epicName) {
        List<SubTask> subTasks = new ArrayList<>();
        for (Epic epic : epics) {
            if (epic.getName().equals(epicName)) {
                subTasks = epic.getSubTasks();
            }
        }
        return subTasks;
    }

    //Получение эпика и подзадачи по идентификатору.
    public HashMap getTaskById(TaskID id) {
        HashMap<String, List<String>> tasks = new HashMap<>();
        for (Epic epic : epics) {
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
        for (Epic epic : epics) {
            if (epic.getId().equals(epicUpdate.getId())) {
                epic.setName(epicUpdate.getName());
                epic.setSubTasks(epicUpdate.getSubTasks());
                epic.setStatus(epicUpdate.getStatus());
            }
        }
    }

    // Удаление всех ранее добавленных задач
    public void removeAllTask() {
        epics.clear();
    }

    // Удаление ранее добавленных задач по ID
    public void removeTaskById(TaskID id) {
        for (int i = 0; i < epics.size(); i++) {
            if (epics.get(i).getId().equals(id)) {
                epics.remove(i);
            }
        }
    }
}
