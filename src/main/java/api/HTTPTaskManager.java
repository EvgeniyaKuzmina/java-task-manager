package api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient taskClient;
    private final Gson gson;

    public HTTPTaskManager(URI url) throws IOException, InterruptedException {
        super(null);
        taskClient = new KVTaskClient(url);
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
        //load();
    }

    // сохраняем все задачи и историю на сервере KVServer
    @Override
    public void save() {
        try {
            List<Task> tasks = new ArrayList();
            List<Task> history = new ArrayList(super.history());

            tasks.addAll(super.getTasksList());
             if (!tasks.isEmpty()) {
                taskClient.put("task", gson.toJson(tasks));
                tasks.clear();
            }

            tasks.addAll(super.getEpicsList());
            if (!tasks.isEmpty()) {
                taskClient.put("epic", gson.toJson(tasks));
                tasks.clear();
            }
            tasks.addAll((super.getSubTasksList()));
            if (!tasks.isEmpty()) {
                taskClient.put("subtask", gson.toJson(tasks));
                tasks.clear();
            }

            if (!history.isEmpty()) {
                taskClient.put("history", gson.toJson(tasks));
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    // загружаем все задачи и историю с сервера KVServer
    public void load() throws IOException, InterruptedException {
        String jsonTask = taskClient.load("task");
        if (notJson(jsonTask)){
            return;
        }
        ArrayList<Task> tasks = gson.fromJson(jsonTask, new TypeToken<ArrayList<Task>>() {
        }.getType());
        if (!tasks.isEmpty()) {
            tasks.forEach(t -> setTasks(t.getId(), t));
        }

        String jsonEpic = taskClient.load("epic");
        if (notJson(jsonEpic)){
            return;
        }
        List<Epic> epics = gson.fromJson(jsonEpic, new TypeToken<ArrayList<Epic>>() {
        }.getType());
        if (!tasks.isEmpty()) {
            epics.forEach(t -> setEpics(t.getId(), t));
        }

        String jsonSubTask = taskClient.load("subtask");
        if (notJson(jsonSubTask)){
            return;
        }
        List<SubTask> subtasks = gson.fromJson(jsonSubTask, new TypeToken<ArrayList<SubTask>>() {
        }.getType());
        if (!tasks.isEmpty()) {
            subtasks.forEach(t -> setSubtasks(t.getId(), t));
        }

        String jsonHistory = taskClient.load("history");
        if (notJson(jsonHistory)){
            return;
        }
        List<Task> history = gson.fromJson(jsonHistory, new TypeToken<ArrayList<Task>>() {
        }.getType());
        if (!history.isEmpty()) {
            history.forEach(t -> loadHistory(t.getId()));
        }
    }
    private boolean notJson(String json) {
        if (json.contains("Что-то пошло не так. Сервер вернул код состояния:")){
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
