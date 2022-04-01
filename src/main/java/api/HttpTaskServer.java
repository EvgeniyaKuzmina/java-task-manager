package api;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static HttpServer httpServer;
    private final TaskManager tasksManager;
    Gson gson;


    public HttpTaskServer(TaskManager tasksManager) throws IOException {
        this.tasksManager = tasksManager;
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
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.createContext("/tasks/subtask", new SubtaskHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());

    }

    public static void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        httpServer.start();
    }

    public static void stop() {
        httpServer.stop(0);
    }

    // реализация дла эндпоинта "/tasks"
    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " запроса от клиента.");
            String query = httpExchange.getRequestURI().getQuery();

            String response;
            switch (method) {
                case "GET":
                    if (query == null) {
                        response = tasksManager.getPrioritizedTasks().toString();
                    } else {
                        Long id = Long.parseLong(query.split("=")[1]);
                        response = tasksManager.getTaskById(id).toString();
                    }
                    break;
                case "DELETE":
                    if (query != null) {
                        Long id = Long.parseLong(query.split("=")[1]);
                        tasksManager.removeById(id);
                        response = String.format("Задача с id %d удалена", id);
                    } else {
                        tasksManager.removeAllTask();
                        response = "Все задачи удалены";
                    }
                    break;
                default:
                    response = "Метод должен быть GET или DELETE. Вы использовали какой-то другой метод!";
            }
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    // реализация дла эндпоинта "/tasks/task"
    class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " запроса от клиента.");

            String response;
            switch (method) {
                case "GET":
                    List<Task> allTasks = new ArrayList<>(tasksManager.getTasksList());
                    response = allTasks.toString();
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    JsonElement jsonElement = JsonParser.parseString(body);
                    if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                        System.out.println("Ответ от сервера не соответствует ожидаемому.");
                        return;
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.keySet().contains("id")) {
                        Task task = gson.fromJson(body, Task.class);
                        tasksManager.updateAnyTask(task);
                        response = task.toString();

                    } else {
                        Task task = createTaskFromJson(jsonObject);
                        tasksManager.addTask(task);
                        response = task.toString();
                    }
                    break;
                default:
                    response = "Метод должен быть GET или POST. Вы использовали какой-то другой метод!";
                    break;
            }

            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }


        // создаёт и возвращает новый объект Task
        private Task createTaskFromJson(JsonObject jsonObject) {
            String name = jsonObject.get("name").toString().split("\"")[1];
            String description = jsonObject.get("description").toString().split("\"")[1];
            String statusString = jsonObject.get("status").toString().toUpperCase();
            Status status = Status.setStatus(statusString);
            int year = Integer.parseInt(jsonObject.get("year").toString());
            int month = Integer.parseInt(jsonObject.get("month").toString());
            int day = Integer.parseInt(jsonObject.get("day").toString());
            int duration = Integer.parseInt(jsonObject.get("duration").toString());
            return tasksManager.createTask(name, description, status, year, month, day, duration);

        }
    }

    // реализация дла эндпоинта "/tasks/epic"
    class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " запроса от клиента.");

            String response;
            switch (method) {
                case "GET":
                    List<Task> allTasks = new ArrayList<>(tasksManager.getEpicsList());
                    response = allTasks.toString();
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    JsonElement jsonElement = JsonParser.parseString(body);

                    if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                        System.out.println("Ответ от сервера не соответствует ожидаемому.");
                        return;
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.keySet().contains("id")) {
                        Epic epic = gson.fromJson(body, Epic.class);
                        tasksManager.updateAnyTask(epic);
                        response = epic.toString();
                    } else {
                        Epic epic = createEpicFromJson(jsonObject);
                        tasksManager.addTask(epic);
                        response = epic.toString();
                    }
                    break;
                default:
                    response = "Метод должен быть GET или POST. Вы использовали какой-то другой метод!";
                    break;
            }

            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        // создаёт и возвращает новый объект Epic
        private Epic createEpicFromJson(JsonObject jsonObject) {
            String name = jsonObject.get("name").toString().split("\"")[1];
            String description = jsonObject.get("description").toString().split("\"")[1];
            return tasksManager.createEpic(name, description);
        }

    }

    // реализация дла эндпоинта "/tasks/subtask"
    class SubtaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " запроса от клиента.");
            String query = httpExchange.getRequestURI().getQuery();
            String response;
            switch (method) {
                case "GET":
                    if (query == null) {
                        List<Task> allTasks = new ArrayList<>(tasksManager.getSubTasksList());
                        response = allTasks.toString();
                    } else {
                        Long id = Long.parseLong(query.split("=")[1]);
                        response = tasksManager.getSubTaskByEpicId(id).toString();
                    }
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    JsonElement jsonElement = JsonParser.parseString(body);

                    if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                        System.out.println("Ответ от сервера не соответствует ожидаемому.");
                        return;
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.keySet().contains("id")) {
                        SubTask subtask = gson.fromJson(body, SubTask.class);
                        tasksManager.updateAnyTask(subtask);
                        response = subtask.toString();
                    } else {
                        SubTask subtask = createSubtaskFromJson(jsonObject);
                        tasksManager.addTask(subtask);
                        response = subtask.toString();
                    }
                    break;
                default:
                    response = "Метод должен быть GET или POST.Вы использовали какой-то другой метод!";
                    break;
            }
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        // создаёт и возвращает новый объект Task из jsonObject
        private SubTask createSubtaskFromJson(JsonObject jsonObject) {
            String name = jsonObject.get("name").toString().split("\"")[1];
            String description = jsonObject.get("description").toString().split("\"")[1];
            Long epicID = Long.parseLong(jsonObject.get("epicId").toString());
            String statusString = jsonObject.get("status").toString().toUpperCase();
            Status status = Status.setStatus(statusString);
            int year = Integer.parseInt(jsonObject.get("year").toString());
            int month = Integer.parseInt(jsonObject.get("month").toString());
            int day = Integer.parseInt(jsonObject.get("day").toString());
            int duration = Integer.parseInt(jsonObject.get("duration").toString());
            return tasksManager.createSubTask(epicID, name, description, status, year, month, day, duration);
        }
    }

    // реализация дла эндпоинта "/tasks/history"
    class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " запроса от клиента.");
            String response = tasksManager.history().toString();
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

}
