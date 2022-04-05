package api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


//реализация менеджера для сохранения задачи на сервере и загрузки задач с сервера
public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient taskClient;
    private final Gson gson;

    public HTTPTaskManager(KVTaskClient kvTaskClient) throws IOException, InterruptedException {
        super(null);
        taskClient = kvTaskClient;
        gson = new GsonBuilder()
                .registerTypeAdapter(
                        LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, type, context) -> LocalDate.parse(json.getAsString())
                )
                .registerTypeAdapter(
                        LocalDate.class,
                        (JsonSerializer<LocalDate>) (srs, typeOfSrs, context) -> new JsonPrimitive(srs.toString())
                )
                .registerTypeAdapter(
                        Duration.class,
                        (JsonDeserializer<Duration>) (json, type, context) -> Duration.parse(json.getAsString())
                )
                .registerTypeAdapter(
                        Duration.class,
                        (JsonSerializer<Duration>) (srs, typeOfSrs, context) -> new JsonPrimitive(srs.toString())
                )
                .create();
        load();
    }

    // сохраняем все задачи и историю на сервере KVServer
    @Override
    public void save() {
        try {
            List<Task> tasks = new ArrayList(super.getTasksList());
            taskClient.put("task", gson.toJson(tasks));

            List<Epic> epic = new ArrayList(super.getEpicsList());
            taskClient.put("epic", gson.toJson(epic));

            List<SubTask> subTask = new ArrayList(super.getSubTasksList());
            taskClient.put("subtask", gson.toJson(subTask));

            List<Task> history = new ArrayList(super.history());
            taskClient.put("history", gson.toJson(history));

        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    // загружаем все задачи и историю с сервера KVServer
    public void load() throws IOException, InterruptedException {
        String json = taskClient.load("task");
        if (notJsonOrEmpty(json)) {
            return;
        }
        ArrayList<Task> tasks = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {
        }.getType());
        if (!tasks.isEmpty()) {
            tasks.forEach(t -> setTasks(t.getId(), t));
        }

        json = taskClient.load("epic");
        if (notJsonOrEmpty(json)) {
            return;
        }
        ArrayList<Epic> epics = gson.fromJson(json, new TypeToken<ArrayList<Epic>>() {
        }.getType());
        if (!epics.isEmpty()) {
            epics.forEach(t -> setEpics(t.getId(), t));
        }

        json = taskClient.load("subtask");
        if (notJsonOrEmpty(json)) {
            return;
        }
        ArrayList<SubTask> subtasks = gson.fromJson(json, new TypeToken<ArrayList<SubTask>>() {
        }.getType());
        if (!subtasks.isEmpty()) {
            subtasks.forEach(t -> setSubtasks(t.getId(), t));
        }

        json = taskClient.load("history");
        if (notJsonOrEmpty(json)) {
            return;
        }
        ArrayList<Task> history = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {
        }.getType());
        if (!history.isEmpty()) {
            history.forEach(t -> loadHistory(t.getId()));
        }
    }

    private boolean notJsonOrEmpty(String json) {
        if (json.contains("Что-то пошло не так. Сервер вернул код состояния:")) {
            System.out.println("Получили не json");
            return true;
        }
        if (json.contains("Задач с ключом")) {
            System.out.println("Получили не json");
            return true;
        }
        return false;
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
