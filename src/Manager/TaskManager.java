package Manager;

import Task.Epic;
import Task.SubTask;
import Task.TaskID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//управляет задачами
public class TaskManager {

    private List<Epic> task = new ArrayList<>();


    // добавляем новую задачу и подзадачу
    public void addNewTask(Epic newEpic) {
        for (Epic epic : task) {
            if (epic.equals(newEpic)) {
                System.out.println("Такая задача уже есть");
            }
        }
        task.add(newEpic);
    }

    // получаем список всех задач
    public List<String> getTasksNames() {
        List<String> taskList = new ArrayList<>();
        for (Epic epic : task) {
            taskList.add(epic.getName());
        }
        return taskList;
    }

    // Получение списка всех подзадач
    public List<String> getSubtasksName() {
        List<String> listOfSubTask = new ArrayList<>();
        for (Epic epic : task) {
            List<SubTask> subTasks = epic.getSubtaskOfEpic();
            for (SubTask subTask : subTasks) {
                listOfSubTask.add(subTask.getName());
            }
        }
        return listOfSubTask;
    }

    // Получение списка всех подзадач определённого эпика.
    public List<SubTask> getSubtasksByEpicName(String epicName) {
        List<SubTask> getSubTask = new ArrayList<>();
        for (Epic epic : task) {
            if (epic.getName().equals(epicName)) {
                getSubTask = epic.getSubtaskOfEpic();
            }
        }
        return getSubTask;
    }

    //Получение эпика и подзадачи по идентификатору.
    public HashMap getTaskById(TaskID id) {
        HashMap<String, List<String>> getTask = new HashMap<>();
        for (Epic epic : task) {
            if (epic.getId().equals(id)) {
                List<String> subtasks = new ArrayList<>();
                for (SubTask subtask : epic.getSubtaskOfEpic()) {
                    subtasks.add(subtask.getName());
                }
                getTask.put(epic.getName(), subtasks);
            }
        }
        return getTask;
    }

    //Обновление эпика и подзадачи по идентификатору. Новая версия объекта передаётся в виде параметра.
    public void updateEpicById(Epic epicUpdate) {
        for (Epic epic : task) {
            if (epic.getId().equals(epicUpdate.getId())) {
                epic.setName(epicUpdate.getName());
                epic.setSubtaskOfEpic(epicUpdate.getSubtaskOfEpic());
                epic.setStatus(epicUpdate.getStatus());
            }
        }
    }

    // Удаление всех ранее добавленных задач
    public void removeAllTask() {
        task.clear();
    }

    // Удаление ранее добавленных задач по ID
    public void removeTaskById(TaskID id) {
        for (int i = 0; i < task.size(); i++) {
            if (task.get(i).getId().equals(id)) {
                task.remove(i);
            }
        }
    }
}
