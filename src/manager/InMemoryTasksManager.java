package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//управляет задачами
public class InMemoryTasksManager implements TaskManager {

    private static HashMap<Long, Epic> epics;
    private static HashMap<Long, Task> tasks;
    private static HashMap<Long, SubTask> subtasks;
    protected static HistoryManager historyManager;
    private TaskID taskId;


    public InMemoryTasksManager() {
        epics = new HashMap<>();
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        taskId = new TaskID();
        historyManager = new InMemoryHistoryManager();

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
        for (SubTask subTask : epics.get(epicId).getSubtasks()) {
            historyManager.add(subTask);
        }
        return epics.get(epicId).getSubtasks();
    }



    protected static void setTasks(Long id, Task task) {
        tasks.put(id, task);
    }

    protected static void setEpics(Long id, Epic epic) {
        epics.put(id, epic);
    }

    protected static void setSubtasks(Long id, SubTask subTask) {
        subtasks.put(id, subTask);
    }

    protected static HashMap<Long, Epic> getEpics() {
        return epics;
    }

    protected static HashMap<Long, Task> getTasks() {
        return tasks;
    }

    protected static HashMap<Long, SubTask> getSubtasks() {
        return subtasks;
    }

    // 2.1 — Получение списка всех эпиков
    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    // 2.1 — Получение списка всех задач
    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    // 2.1 — Получение списка всех подзадач
    @Override
    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(subtasks.values());
    }

    // 2.4 Получение задачи любого типа по идентификатору.
    @Override
    public Task getTaskById(Long id) {
        if (id < 0) {
            System.out.println("Ошибка в ID");
            return null;
        }
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            System.out.println("Нет задач с таким ID");
            return null;
        }
    }

    //2.5 Добавление новой задачи, эпика, подзадачи. Сам объект должен передаваться в качестве параметра.
    @Override
    public void addTask(Task newTask) {
        if (newTask instanceof Epic) {
            Epic newEpic = (Epic) newTask;
            addNewEpic(newEpic);
        } else if (newTask instanceof SubTask) {
            SubTask newSubTask = (SubTask) newTask;
            addSubtaskToEpic(newSubTask);
        } else {
            addNewTask(newTask);
        }
    }

    //2.5 Добавление новой подзадачи.
    private void addSubtaskToEpic(SubTask newSubTask) {
        Long epicId = newSubTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпика с таким ID нет");
            return;
        }
        for (SubTask sabTask : subtasks. values()) {
            if (newSubTask.equals(sabTask)) {
                System.out.printf("Такая подзадача в эпике %s уже есть\n", newSubTask.getEpicId());
                return;
            }
        }
        epics.get(epicId).getSubtasks().add(newSubTask);
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

    //2.5 Добавление новой задачи
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
    public void updateAnyTask(Task task) {
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
        historyManager.removeAll();
    }

    // Удаление ранее добавленных задач по ID
    @Override
    public void removeById(Long id) {
        if (id < 0) {
            System.out.println("Ошибка в ID");
            return;
        }
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (SubTask subTask : epic.getSubtasks()) {
                subtasks.remove(subTask.getId());
                historyManager.remove(subTask.getId());
            }
            epics.remove(id);
            historyManager.remove(id);
        } else if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        } else if (subtasks.containsKey(id)) {
            subtasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задач с таким id нет");
        }
    }

    //создание эпика
    @Override
    public Epic createEpic(String nameEpic, String description) {
        taskId.setId(getLastId());
        Long id = taskId.getId();
        List<SubTask> subTasksForEpic = new ArrayList<>();
        for (SubTask subTask : subtasks.values()) {
            if (subTask.getEpicId().equals(id)) {
                subTasksForEpic.add(subTask);
            }
        }
        return new Epic(id, nameEpic, description, subTasksForEpic, Status.getStatusForEpic(subTasksForEpic));
    }

    //создание задачи
    @Override
    public Task createTask(String nameTask, String description, Status status) {
        taskId.setId(getLastId());
        long id = taskId.getId();
        return new Task(id, nameTask, description, status);
    }

    //создание подзадачи
    @Override
    public SubTask createSubTask(long epicId, String nameTask, String description, Status status) {
        taskId.setId(getLastId());
        long id = taskId.getId();
        return new SubTask(epicId, id, nameTask, description, status);
    }

    // получение истории просмотра
    @Override
    public List<Task> history() {
        return historyManager.getHistoryList();
    }

    private long getLastId(){
        long lastId = 0;
        for (Long id : epics.keySet()) {
            if (lastId < id) {
                lastId = id;
            }
        }
        for (Long id : tasks.keySet()) {
            if (lastId < id) {
                lastId = id;
            }
        }
        for (Long id : subtasks.keySet()) {
            if (lastId < id) {
                lastId = id;
            }
        }
        return lastId;

    }

}
