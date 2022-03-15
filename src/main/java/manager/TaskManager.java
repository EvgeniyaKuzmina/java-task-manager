package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    // 2.3 Получение списка всех подзадач определённого эпика.
    List<SubTask> getSubTaskByEpicId(Long id);

    // 2.1 — Получение списка всех эпиков
    List<Epic> getEpicsList();

    // 2.1 — Получение списка всех задач
    List<Task> getTasksList();

    // 2.1 — Получение списка всех подзадач
    List<SubTask> getSubTasksList();

    // 2.4 Получение задачи любого типа по идентификатору.
    Task getTaskById(Long id);

    //2.5 Добавление новой задачи, эпика. Сам объект должен передаваться в качестве параметра.
    void addTask(Task newObject);

    //2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    void updateAnyTask(Task task);

    // Удаление всех ранее добавленных задач
    void removeAllTask();

    // Удаление ранее добавленных задач по ID
    String removeById(Long id);

    //создание эпика
    Epic createEpic(String nameEpic, String description);

    //создание эпика
    Task createTask(String nameTask, String description, Status status, int year, int months, int day, int durationInHours);

    //создание эпика
    SubTask createSubTask(long epicId, String nameTask, String description, Status status, int year, int months,
                          int day, int durationInHours);

    //показывает историю просмотра задач и подзадач
    List<Task> history();

    // возвращает список отсортированных задач по дате старта
    TreeSet<Task> getPrioritizedTasks();


}
