package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import task.*;
import utility.Answers;

import java.time.LocalDate;
import java.util.*;

//управляет задачами
public class InMemoryTasksManager implements TaskManager {

    private static final int HOURS_IN_ONE_DAY = 24;
    protected static HistoryManager historyManager;
    private static HashMap<Long, Epic> epics;
    private static HashMap<Long, Task> tasks;
    private static HashMap<Long, SubTask> subtasks;
    private TaskID taskId;


    public InMemoryTasksManager() {
        epics = new HashMap<>();
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        taskId = new TaskID();
        historyManager = new InMemoryHistoryManager();

    }

    public static void setTasks(Long id, Task task) {
        tasks.put(id, task);
    }

    public static void setEpics(Long id, Epic epic) {
        epics.put(id, epic);
    }

    public static void setSubtasks(Long id, SubTask subTask) {
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

    // 2.3 Получение списка всех подзадач определённого эпика.
    @Override
    public List<SubTask> getSubTaskByEpicId(Long epicId) {
        if (epicId <= 0) {
            System.err.println(Answers.WRONG_ID);
            return Collections.emptyList();
        }
        if (!epics.containsKey(epicId)) {
            System.out.println(Answers.NO_EPIC_WITH_ID.getAnswer());
            return Collections.emptyList();
        }
        for (SubTask subTask : epics.get(epicId).getSubtasks()) {
            historyManager.add(subTask);
        }
        return epics.get(epicId).getSubtasks();
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
            System.out.println(Answers.WRONG_ID.getAnswer());
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
            System.out.println(Answers.NO_TASKS_WITH_ID.getAnswer());
            return null;
        }
    }

    //2.5 Добавление новой задачи, эпика, подзадачи. Сам объект должен передаваться в качестве параметра.
    @Override
    public void addTask(Task newTask) {
        getPrioritizedTasks();
        if (newTask instanceof Epic) {
            Epic newEpic = (Epic) newTask;
            if (getMessageAboutIntersection(newEpic)) {
                addNewEpic(newEpic);
            } else {
                System.out.println(Answers.CHANGE_DATA_START_OR_DURATION.getAnswer());
            }
        } else if (newTask instanceof SubTask) {
            SubTask newSubTask = (SubTask) newTask;
            if (getMessageAboutIntersection(newSubTask)) {
                addSubtaskToEpic(newSubTask);
            } else {
                System.out.println(Answers.CHANGE_DATA_START_OR_DURATION.getAnswer());
            }
        } else {
            if (getMessageAboutIntersection(newTask)) {
                addNewTask(newTask);
            } else {
                System.out.println(Answers.CHANGE_DATA_START_OR_DURATION.getAnswer());
            }
        }
    }

    //2.5 Добавление новой подзадачи.
    private void addSubtaskToEpic(SubTask newSubTask) {
        Long epicId = newSubTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            System.err.println(Answers.NO_EPIC_WITH_ID);
            return;
        }
        for (SubTask sabTask : subtasks.values()) {
            if (newSubTask.equals(sabTask)) {
                System.out.printf("Такая подзадача в эпике %s уже есть\n", newSubTask.getEpicId());
                return;
            }
        }
        subtasks.put(newSubTask.getId(), newSubTask);
        epics.get(epicId).getSubtasks().add(newSubTask);
        updateEpic(epics.get(epicId));
        updateEpicStatusDurationDataStart(epics.get(epicId));
    }

    //2.5 Добавление нового эпика
    private void addNewEpic(Epic newEpic) {
        for (Epic epic : epics.values()) {
            if (epic.equals(newEpic)) {
                System.out.println(Answers.TASKS_ALREADY_EXIST.getAnswer());
                return;
            }
        }
        epics.put(newEpic.getId(), newEpic);
    }

    //2.5 Добавление новой задачи
    private void addNewTask(Task newTask) {
        for (Task task : tasks.values()) {
            if (task.equals(newTask)) {
                System.out.println(Answers.TASKS_ALREADY_EXIST.getAnswer());
                return;
            }
        }
        tasks.put(newTask.getId(), newTask);
    }

    //2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    @Override
    public void updateAnyTask(Task task) {
        getPrioritizedTasks();
        if (task instanceof Epic) {
            Epic newEpic = (Epic) task;
            if (epics.containsKey(newEpic.getId())) {
                if (getMessageAboutIntersection(newEpic)) {
                    updateEpic(newEpic);
                } else {
                    System.out.println(Answers.CHANGE_DATA_START_OR_DURATION.getAnswer());
                }
            } else {
                System.out.println(Answers.NO_EPIC_WITH_ID.getAnswer());
            }
        } else if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            if (subtasks.containsKey(subTask.getId())) {
                if (getMessageAboutIntersection(subTask)) {
                    updateSubTask(subTask);
                    updateEpic(epics.get(subTask.getEpicId()));
                } else {
                    System.out.println(Answers.CHANGE_DATA_START_OR_DURATION.getAnswer());
                }

            } else {
                System.out.println(Answers.NO_SUBTASK_WITH_ID.getAnswer());
            }
        } else {
            if (tasks.containsKey(task.getId())) {
                if (getMessageAboutIntersection(task)) {
                    updateTask(task);
                } else {
                    System.out.println(Answers.CHANGE_DATA_START_OR_DURATION.getAnswer());
                }
            } else {
                System.out.println(Answers.NO_TASKS_WITH_ID.getAnswer());
            }
        }
    }

    //обновление Эпика
    private void updateEpic(Epic newEpic) {
        epics.get(newEpic.getId()).setName(newEpic.getName());
        epics.get(newEpic.getId()).setDescription(newEpic.getDescription());
        epics.get(newEpic.getId()).setSubtasks(newEpic.getSubtasks());
        updateEpicStatusDurationDataStart(newEpic);
    }

    //обновление статуса эпика, продолжительности и даты старта
    private void updateEpicStatusDurationDataStart(Epic newEpic) {
        epics.get(newEpic.getId()).setStatus(Status.getStatusForEpic(newEpic.getSubtasks()));
        epics.get(newEpic.getId()).setDuration(Epic.getDurationForEpic(newEpic.getSubtasks()));
        epics.get(newEpic.getId()).setStartTime(Epic.getDataStartForEpic(newEpic.getSubtasks()));
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
    public String removeById(Long id) {
        String result;
        if (id <= 0) {
            return Answers.WRONG_ID.getAnswer();
        }
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (SubTask subTask : epic.getSubtasks()) {
                subtasks.remove(subTask.getId());
                historyManager.remove(subTask.getId());
            }
            epics.remove(id);
            historyManager.remove(id);
            result = Answers.REMOVE_EPIC.getAnswer();
        } else if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
            result = Answers.REMOVE_TASK.getAnswer();
        } else if (subtasks.containsKey(id)) {
            historyManager.remove(id);
            epics.get(subtasks.get(id).getEpicId()).getSubtasks().remove(subtasks.get(id));
            subtasks.remove(id);
            result = Answers.REMOVE_SUBTASK.getAnswer();
        } else {
            result = Answers.NO_TASKS_WITH_ID.getAnswer();
        }
        return result;
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
        LocalDate startDate = Epic.getDataStartForEpic(subTasksForEpic);
        List<Integer> date = Task.getYearMonthDatFromLocalDate(startDate);
        return new Epic(id, nameEpic, description, subTasksForEpic, Status.getStatusForEpic(subTasksForEpic),
                        date.get(0), date.get(1), date.get(2), Epic.getDurationForEpic(subTasksForEpic));
    }

    //создание задачи
    @Override
    public Task createTask(String nameTask, String description, Status status, int year, int month, int day,
                           int durationInHours) {
        taskId.setId(getLastId());
        long id = taskId.getId();
        return new Task(id, nameTask, description, status, year, month, day, durationInHours);
    }

    //создание подзадачи
    @Override
    public SubTask createSubTask(long epicId, String nameTask, String description, Status status, int year, int months,
                                 int day, int durationInHours) {
        taskId.setId(getLastId());
        long id = taskId.getId();
        return new SubTask(epicId, id, nameTask, description, status, year, months, day, durationInHours);
    }

    // получение истории просмотра
    @Override
    public List<Task> history() {
        return historyManager.getHistoryList();
    }

    // получаем последний id из всех задач
    private long getLastId() {
        Comparator<Long> comparator = (id1, id2) -> (int) (id1 - id2);
        LinkedList <Long> allId = new LinkedList<>();
        allId.addAll(epics.keySet());
        allId.addAll(tasks.keySet());
        allId.addAll(subtasks.keySet());
        if (allId.isEmpty()) return 0;
        allId.sort(comparator);
        return allId.getLast();
    }

    // метод собирает задачи из всех мап в один лист и сортирует их по дате начала startTime
    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        Comparator<Task> comparator = (task1, task2) -> {
            if (task1.getStartTime().isBefore(task2.getStartTime())) {
                return -1;
            } else if (task1.getStartTime().isAfter(task2.getStartTime())) {
                return 1;
            } else {
                return (int) (task1.getId() - (task2.getId()));

            }
        };
        TreeSet<Task>  sortedTasks = new TreeSet<>(comparator);
        sortedTasks.addAll(tasks.values());
        sortedTasks.addAll(subtasks.values());
        sortedTasks.addAll(epics.values());
        return sortedTasks;
    }

    private Boolean getMessageAboutIntersection(Task newTask) {
        LocalDate startDateForNewTask = newTask.getStartTime();
        int duration = (int) newTask.getDuration().toHours();
        TreeSet<Task> sortedTasks = getPrioritizedTasks();
        for (Task task : sortedTasks) {
            if (task instanceof Epic) {
                continue;
            }
            if (startDateForNewTask.equals(task.getStartTime())) {
                duration += task.getDuration().toHours();
            }
            if (newTask.getId().equals(task.getId())) {
                duration -= task.getDuration().toHours();
            }
        }
        if (duration > HOURS_IN_ONE_DAY) {
            return false;
        }
        System.out.println("Задача с текущими значениями даты старта и продолжительностью добавлена");
        return true;
    }

}
