package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient taskClient;
    private final Gson gson;

    public HTTPTaskManager(URI url) throws IOException, InterruptedException {
        super("recources/recources");
        taskClient = new KVTaskClient(url);
        gson = new Gson();
    }

    // сохраняем все задачи и историю на сервере KVServer
    @Override
    public void save() {
        try {
            List<Task> tasks = new ArrayList();
            List<Task> history = new ArrayList(super.history());

            tasks.addAll(super.getTasksList());
            put("task", tasks);
            tasks.clear();

            tasks.addAll(super.getEpicsList());
            put("epic", tasks);
            tasks.clear();

            tasks.addAll((super.getSubTasksList()));
            put("subtask", tasks);
            tasks.clear();

            put("history", history);
        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    private void put(String key, List<Task> tasks) throws IOException, InterruptedException {
        StringBuilder jsonString = new StringBuilder();
        for (Task task : tasks) {
            jsonString.append(gson.toJson(task)).append("\n");
        }
        taskClient.put(key, jsonString.toString());
    }

    // загружаем все задачи и историю с сервера KVServer
    public void load() throws IOException, InterruptedException {
        String jsonTask = taskClient.load("task");
        List<Task> tasks = gson.fromJson(jsonTask, new TypeToken<ArrayList<Task>>() {
        }.getType());
        tasks.forEach(t -> setTasks(t.getId(), t));

        String jsonEpic = taskClient.load("epic");
        List<Epic> epics = gson.fromJson(jsonEpic, new TypeToken<ArrayList<Epic>>() {
        }.getType());
        epics.forEach(t -> setEpics(t.getId(), t));

        String jsonSubTask = taskClient.load("subtask");
        List<SubTask> subtasks = gson.fromJson(jsonSubTask, new TypeToken<ArrayList<SubTask>>() {
        }.getType());
        subtasks.forEach(t -> setSubtasks(t.getId(), t));

        String jsonHistory = taskClient.load("history");
        List<Task> history = gson.fromJson(jsonHistory, new TypeToken<ArrayList<Task>>() {
        }.getType());
        history.forEach(t -> loadHistory(t.getId()));

    }

    //2.5 Добавление новой задачи, эпика, подзадачи. Сохранение задачи в файл.
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    // 2.3 Получение списка всех подзадач определённого эпика.
    @Override
    public List<SubTask> getSubTaskByEpicId(Long id) {
        List<SubTask> subTasks = super.getSubTaskByEpicId(id);
        save();
        return subTasks;
    }

    // 2.4 Получение задачи любого типа по идентификатору.
    @Override
    public Task getTaskById(Long id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    //2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра. Сохранение  в файл.
    @Override
    public void updateAnyTask(Task task) {
        super.updateAnyTask(task);
        save();
    }

    // Удаление всех ранее добавленных задач
    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    // Удаление ранее добавленных задач по ID
    @Override
    public String removeById(Long id) {
        String result = super.removeById(id);
        save();
        return result;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

}
