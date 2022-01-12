package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

//управляет задачами
public class InMemoryTasksManager implements TaskManager {

    private final HashMap<Long, Epic> epics;
    private final HashMap<Long, Task> tasks;
    private final HashMap<Long, SubTask> subtasks;
    private final List<Task> history;

    public InMemoryTasksManager() {
        epics = new HashMap<>();
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        history = new LinkedList<>();
    }

    // 2.3 Получение списка всех подзадач определённого эпика.
    @Override
    public List<SubTask> getSubTaskByEpicId(Long epicId) {
        if (epicId < 0) {
            System.out.println("Ошибка в ID");
            return null;
        }
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпика с таким Id нет");
            return null;
        }
        history();
        history.add(epics.get(epicId));
        return epics.get(epicId).getSubtasks();
    }

    // 2.1 — Получение списка всех эпиков
    @Override
    public List<Epic> getEpics() {
        List<Epic> tasksName = new ArrayList<>();
        tasksName.addAll(epics.values());
        return tasksName;
    }

    // 2.1 — Получение списка всех задач
    @Override
    public List<Task> getTasks() {
        List<Task> epicsName = new ArrayList<>();
        epicsName.addAll(tasks.values());
        return epicsName;
    }

    // 2.1 — Получение списка всех подзадач
    @Override
    public List<SubTask> getSubTasks() {
        List<SubTask> subTasksName = new ArrayList<>();
        subTasksName.addAll(subtasks.values());
        return subTasksName;
    }

    // 2.4 Получение задачи любого типа по идентификатору.
    @Override
    public Task getTaskEpicSubtaskById(Long id) {
        if (id < 0) {
            System.out.println("Ошибка в ID");
            return null;
        }
        if (epics.containsKey(id)) {
            history();
            history.add(epics.get(id));
            return epics.get(id);
        } else if (tasks.containsKey(id)) {
            history();
            history.add(tasks.get(id));
            return tasks.get(id);
        } else if (subtasks.containsKey(id)) {
            history();
            history.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            System.out.println("Нет задач с таким ID");
            return null;
        }
    }

    //2.5 Добавление новой задачи, эпика, подзадачи. Сам объект должен передаваться в качестве параметра.
    @Override
    public void addNewTaskEpicSubTask(Task newTask) {
        if (newTask instanceof Epic) {
            Epic newEpic = (Epic) newTask;
            addNewEpic(newEpic);
        } else if (newTask instanceof SubTask) {
            SubTask newSubTask = (SubTask) newTask;
            addNewSubtasks(newSubTask);
        } else {
            addNewTask(newTask);
        }
    }

    //2.5 Добавление новой подзадачи.
    private void addNewSubtasks(SubTask newSubTask) {
        if (epics.containsKey(newSubTask.getEpicId())) {
            for (SubTask subTask : epics.get(newSubTask.getEpicId()).getSubtasks()) {
                if (newSubTask.equals(subTask)) {
                    System.out.printf("Такая подзадача в эпике %s уже есть",
                                      epics.get(newSubTask.getEpicId()).getName());
                    return;
                }
            }
        }
        subtasks.put(newSubTask.getId(), newSubTask);
    }

    //2.5 Добавление нового эпика
    private void addNewEpic(Epic newEpic) {
        for (Epic epic : epics.values()) {
            if (epic.equals(newEpic)) {
                System.out.println("Такой эпик уже есть");
                return;
            }
        }
        epics.put(newEpic.getId(), newEpic);
    }

    //2.5 Добавление новой задача
    private void addNewTask(Task newTask) {
        for (Task task : tasks.values()) {
            if (task.equals(newTask)) {
                System.out.println("Такая задача уже есть");
                return;
            }
        }
        tasks.put(newTask.getId(), newTask);

    }

    //2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    @Override
    public void updateTaskEpicSubtask(Task task) {
        if (task instanceof Epic) {
            Epic newEpic = (Epic) task;
            if (epics.containsKey(newEpic.getId())) {
                updateEpic(newEpic);
            } else {
                System.out.println("Эпика с таким ID нет. Добавьте эпик в список задач.");
            }
        } else if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            if (subtasks.containsKey(subTask.getId())) {
                updateSubTask(subTask);
                updateEpic(epics.get(subTask.getEpicId()));
            }
        } else {
            if (tasks.containsKey(task.getId())) {
                updateTask(task);
            } else {
                System.out.println("Задачи с таким ID нет. Добавьте задачу в список задач.");
            }
        }
    }

    //обновление Эпика
    private void updateEpic(Epic newEpic) {
        epics.get(newEpic.getId()).setName(newEpic.getName());
        epics.get(newEpic.getId()).setStatus(newEpic.getStatus());
        epics.get(newEpic.getId()).setDescription(newEpic.getDescription());
        epics.get(newEpic.getId()).setSubtasks(newEpic.getSubtasks());
    }

    //обновление Задачи
    private void updateTask(Task newTask) {
        tasks.get(newTask.getId()).setName(newTask.getName());
        tasks.get(newTask.getId()).setStatus(newTask.getStatus());
        tasks.get(newTask.getId()).setDescription(newTask.getDescription());
    }

    //обновление подзадачи
    private void updateSubTask(SubTask newSubTask) {
        subtasks.get(newSubTask.getId()).setName(newSubTask.getName());
        subtasks.get(newSubTask.getId()).setStatus(newSubTask.getStatus());
        subtasks.get(newSubTask.getId()).setDescription(newSubTask.getDescription());
    }

    // Удаление всех ранее добавленных задач
    @Override
    public void removeAllTask() {
        epics.clear();
        tasks.clear();
        subtasks.clear();
    }

    // Удаление ранее добавленных задач по ID
    @Override
    public void removeById(Long id) {
        if (id < 0) {
            System.out.println("Ошибка в ID");
            return;
        }
        if (epics.containsKey(id)) {
            epics.remove(id);
        } else if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            subtasks.remove(id);
        } else {
            System.out.println("Задач с таким id нет");
        }
    }

    //создание эпика
    @Override
    public Epic createEpic(Long id, String nameEpic, String description) {
        List<SubTask> subTasksForEpic = new ArrayList<>();
        for (SubTask subTask : subtasks.values()) {
            if (subTask.getEpicId().equals(id)) {
                subTasksForEpic.add(subTask);
            }
        }
        return new Epic(id, nameEpic, description, subTasksForEpic, Status.getStatusForEpic(subTasksForEpic));
    }

    @Override
    public List<Task> history() {
        if (history.size() > 10) {
            history.remove(0);
        }
        return history;
    }

}
