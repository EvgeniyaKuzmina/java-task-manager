package managementTask;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//управляет задачами
public class InMemoryTasksManager implements managementTask.TaskManager {

    private final HashMap<Long, Epic> epics;
    private final HashMap<Long, Task> tasks;
    private final List<SubTask> subtasks;
    private List<Task> history;

    public InMemoryTasksManager() {
        epics = new HashMap<>();
        tasks = new HashMap<>();
        subtasks = new ArrayList<>();
        history = new ArrayList<>();
    }

    // 2.3 Получение списка всех подзадач определённого эпика.
    @Override
    public List<SubTask> getSubTaskById(Long id) {
        if (id < 0) {
            System.out.println("Ошибка в ID");
        }
        if (!epics.containsKey(id)) {
            System.out.println("Эпика с таким Id нет");
        }
        List<SubTask> subTasksName = new ArrayList<>();
        if (epics.containsKey(id)) {
            subTasksName = epics.get(id).getSubtasks();
        }
        return subTasksName;
    }

    // 2.1 — Получение списка всех задач
    @Override
    public List<Epic> getEpics() {
        List<Epic> tasksName = new ArrayList<>();
        for (Epic epic : epics.values()) {
            tasksName.add(epic);
            history();
            history.add(epic);
        }
        return tasksName;
    }

    // 2.1 — Получение списка всех эпиков
    @Override
    public List<Task> getTasks() {
        List<Task> epicsName = new ArrayList<>();
        for (Task task : tasks.values()) {
            epicsName.add(task);
            history();
            history.add(task);
        }

        return epicsName;
    }

    // 2.1 — Получение списка всех подзадач
    @Override
    public List<SubTask> getSubTasks() {
        for (SubTask subTask : subtasks) {
            history();
            history.add(subTask);
        }
        return subtasks;
    }

    // 2.4 Получение задачи любого типа по идентификатору.
    @Override
    public List<Task> getTaskOrEpicById(Long id) {
        if (TaskID.getId() < 0) {
            System.out.println("Ошибка в ID");
            return null;
        }
        List<Task> tasksName = new ArrayList<>();
        if (epics.containsKey(id)) {
            tasksName.add(epics.get(id));
        } else if (tasks.containsKey(id)) {
            tasksName.add(tasks.get(id));
        } else {
            System.out.println("Нет задач с таким ID");
        }
        return tasksName;
    }

    //2.5 Добавление новой задачи, эпика. Сам объект должен передаваться в качестве параметра.
    @Override
    public void addNewTaskEpic(Object newObject) {
        if (newObject instanceof Epic) {
            Epic newEpic = (Epic) newObject;
            for (Epic epic : epics.values()) {
                if (epic.equals(newEpic)) {
                    System.out.println("Такой эпик уже есть");
                    return;
                }
            }
            epics.put(newEpic.getId(), newEpic);
        } else if (newObject instanceof Task) {
            Task newTask = (Task) newObject;
            for (Epic epic : epics.values()) {
                if (epic.equals(newTask)) {
                    System.out.println("Такая задача уже есть");
                    return;
                }
            }
            tasks.put(newTask.getId(), newTask);
        }
    }

    //2.5 Добавление новой подзадачи.
    @Override
    public void addNewSubtasks(SubTask newSubTask) {
        if (subtasks.isEmpty()) {
            subtasks.add(newSubTask);
            return;
        }
        for (SubTask subTask : subtasks) {
            if (subTask.getName().equals(newSubTask.getName())) {
                System.out.printf("Такая подзадача в эпике %d уже есть", subTask.getId());
                return;
            }
        }
        subtasks.add(newSubTask);
    }

    //2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    @Override
    public void updateTaskEpic(Object object) {
        if (object instanceof Epic) {
            Epic newEpic = (Epic) object;
            if (epics.containsKey(newEpic.getId())) {
                updateEpic(newEpic);
            } else {
                System.out.println("Эпика с таким ID нет. Добавьте эпик в список задач.");
            }
        } else if (object instanceof Task) {
            Task newTask = (Task) object;
            if (tasks.containsKey(newTask.getId())) {
                updateTask(newTask);
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

    // Удаление всех ранее добавленных задач
    @Override
    public void removeAllTask() {
        epics.clear();
        tasks.clear();
    }

    // Удаление ранее добавленных задач по ID
    @Override
    public void removeTaskById(Long id) {
        if (id < 0) {
            System.out.println("Ошибка в ID");
            return;
        }
        if (epics.containsKey(id)) {
            epics.remove(id);
        } else if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задач с таким id нет");
        }
    }

    //создание эпика
    @Override
    public Epic createEpic(Long id, String nameEpic, String description) {
        List<SubTask> subTasksForEpic = new ArrayList<>();
        for (SubTask subTask : subtasks) {
            if (subTask.getId().equals(id)) {
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
