package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.List;

public interface TaskManager {

    // 2.3 Получение списка всех подзадач определённого эпика.
    List<SubTask> getSubTaskByEpicId(Long id);

    // 2.1 — Получение списка всех задач
    List<Epic> getEpics();

    // 2.1 — Получение списка всех эпиков
    List<Task> getTasks();

    // 2.1 — Получение списка всех подзадач
    List<SubTask> getSubTasks();

    // 2.4 Получение задачи любого типа по идентификатору.
    Task getTaskById(Long id);

    //2.5 Добавление новой задачи, эпика. Сам объект должен передаваться в качестве параметра.
    void addTask(Task newObject);

      //2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    void updateAnyTask(Task task);

    // Удаление всех ранее добавленных задач
    void removeAllTask();

    // Удаление ранее добавленных задач по ID
    void removeById(Long id);

    //создание эпика
    Epic createEpic(Long id, String nameEpic, String description);

    //показывает историю просмотра задач и подзадач
    List<Task> history();
}
