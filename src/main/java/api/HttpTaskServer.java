package api;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {


    TaskManager tasksManager;
    HttpServer httpServer;
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    Gson gson;

    public static void main(String[] args) throws IOException {
        new HttpTaskServer();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public HttpTaskServer() throws IOException {
        tasksManager = Managers.getFileBackedManager("recources/recources");
        gson = new GsonBuilder()
               /* .registerTypeAdapter(
                        LocalDate.class,
                        (JsonDeserializer<LocalDate>)  (json, type, JsonDeserializationContext) -> LocalDate.parse(json.getAsString())
                )
                .registerTypeAdapter(
                        LocalDate.class,
                        (JsonSerializer<LocalDate>)  (srs, typeOfSrs, context) -> new JsonPrimitive(srs.toString())
                )
                .registerTypeAdapter(
                        Duration.class,
                        (JsonDeserializer<Duration>)  (json, type, JsonDeserializationContext) -> Duration.parse(json.getAsString())
                )
                .registerTypeAdapter(
                        Duration.class,
                        (JsonSerializer<Duration>)  (srs, typeOfSrs, context) -> new JsonPrimitive(srs.toString())
                )*/
                // .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                //.registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        // не могу с помощью gson сделать десериализацию из строки в объект Task, SubTask или Epic.
        // Task task = gson.fromJson(body, Task.class) — не работает у меня.
        // Если использовать метод fromJson не получается получить дату старта и продолжительность. Возникает ошибка NullPointer
        // возможно я неверно реализовала классы DurationAdapter и LocalDateAdapter. Пробовала и через создание классов, и через лямбды, не работает
        // в результате написала костыльное решение. Создала методы getSubtaskFromJson, getTaskFromJson и getEpicFromJson
        // которые возвращают нужные объекты
        httpServer =  HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.createContext("/tasks/subtask", new SubtaskHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
        httpServer.start();
    }

    // реализация дла эндпоинта "/tasks"
    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " запроса от клиента.");
            String query = httpExchange.getRequestURI().getQuery();

            String response;
            switch(method) {
                case "GET":
                    if  (query == null) {
                        response = tasksManager.getPrioritizedTasks().toString();
                    } else {
                        Long id = Long.parseLong(query.split("=")[1]);
                        response = tasksManager.getTaskById(id).toString();
                    }
                    break;
                case "DELETE":
                    if  (query != null) {
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
            switch(method) {
                case "GET":
                    List<Task> allTasks = new ArrayList<>(tasksManager.getTasksList());
                    response = allTasks.toString();
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    JsonElement jsonElement = JsonParser.parseString(body);
                    if(!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                        System.out.println("Ответ от сервера не соответствует ожидаемому.");
                        return;
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.keySet().contains("id")) {
                        Task task = gson.fromJson(body, Task.class); // установила флаг --add-opens java.base/java.time=ALL-UNNAMED
                        // при запуске не работает, получаю ответ: Error: Server returned nothing (no headers, no data)
                        tasksManager.updateAnyTask(task);
                        response = task.toString();
                       // tasksManager.updateAnyTask(getTaskFromJson(jsonObject)); — решение с моим методом getTaskFromJson
                      //  response = getTaskFromJson(jsonObject).toString(); — код работает, получаю объект Task
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
        private Task createTaskFromJson(JsonObject jsonObject ) {
            String name = jsonObject.get("name").toString();
            String description = jsonObject.get("description").toString();
            String statusString = jsonObject.get("status").toString().toUpperCase();
            Status status = Status.setStatus(statusString);
            int year = Integer.parseInt(jsonObject.get("year").toString());
            int month = Integer.parseInt(jsonObject.get("month").toString());
            int day = Integer.parseInt(jsonObject.get("day").toString());
            int duration = Integer.parseInt(jsonObject.get("duration").toString());
            return tasksManager.createTask(name, description, status, year, month, day, duration);

        }

        // получаем объект Task из jsonObject
        private Task getTaskFromJson(JsonObject jsonObject ) {
            Long id = Long.parseLong(jsonObject.get("id").toString());
            String name = jsonObject.get("name").toString();
            String description = jsonObject.get("description").toString();
            String statusString = jsonObject.get("status").toString().toUpperCase();
            Status status = Status.setStatus(statusString);
            int year = Integer.parseInt(jsonObject.get("year").toString());
            int month = Integer.parseInt(jsonObject.get("month").toString());
            int day = Integer.parseInt(jsonObject.get("day").toString());
            int duration = Integer.parseInt(jsonObject.get("duration").toString());
            return new Task(id, name, description, status, year, month, day, duration);
        }
    }

    // реализация дла эндпоинта "/tasks/epic"
    class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " запроса от клиента.");

            String response;
            switch(method) {
                case "GET":
                    List<Task> allTasks = new ArrayList<>(tasksManager.getEpicsList());
                    response = allTasks.toString();
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    JsonElement jsonElement = JsonParser.parseString(body);

                    if(!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                        System.out.println("Ответ от сервера не соответствует ожидаемому.");
                        return;
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.keySet().contains("id")) {
                        Epic epic = gson.fromJson(body, Epic.class);
                        tasksManager.updateAnyTask(epic);
                        response = epic.toString();
                       // tasksManager.updateAnyTask(updateEpicFromJson(jsonObject));
                       // response = updateEpicFromJson(jsonObject).toString();
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
        private Epic createEpicFromJson(JsonObject jsonObject ) {
            String name = jsonObject.get("name").toString();
            String description = jsonObject.get("description").toString();
            return tasksManager.createEpic(name, description);
        }

        // получаем объект Task из jsonObject
        private Epic updateEpicFromJson(JsonObject jsonObject) {
            Long id = Long.parseLong(jsonObject.get("id").toString());
            String name = jsonObject.get("name").toString();
            String description = jsonObject.get("description").toString();
            StringBuilder sb = new StringBuilder(jsonObject.get("subtasks").toString());
            sb.deleteCharAt(0).deleteCharAt(sb.length() - 1);
            String subtasks = sb.toString();
            String[] listSubtask = subtasks.split(",\\{");
            List<SubTask> list = new ArrayList<>();
            for (int i = 0; i <  listSubtask.length; i++) {
                if (i >= 1 ) {
                    JsonElement jsonElement = JsonParser.parseString("{" + listSubtask[i]);
                    JsonObject jsonObjectSubtask = jsonElement.getAsJsonObject();
                    list.add(getSubtaskFromJson(jsonObjectSubtask));
                } else {
                    JsonElement jsonElement = JsonParser.parseString(listSubtask[i]);
                    JsonObject jsonObjectSubtask = jsonElement.getAsJsonObject();
                    list.add(getSubtaskFromJson(jsonObjectSubtask));
                }
            }
            String statusString = jsonObject.get("status").toString().toUpperCase();
            Status status = Status.setStatus(statusString);
            int year = Integer.parseInt(jsonObject.get("year").toString());
            int month = Integer.parseInt(jsonObject.get("month").toString());
            int day = Integer.parseInt(jsonObject.get("day").toString());
            int duration = Integer.parseInt(jsonObject.get("duration").toString());
            return new Epic(id, name, description, list, status, year, month, day, duration);
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
            switch(method) {
                case "GET":
                    if  (query == null) {
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

                    if(!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                        System.out.println("Ответ от сервера не соответствует ожидаемому.");
                        return;
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.keySet().contains("id")) {
                        SubTask subtask = gson.fromJson(body, SubTask.class);
                        tasksManager.updateAnyTask(subtask);
                        response = subtask.toString();
                        //tasksManager.updateAnyTask(getSubtaskFromJson(jsonObject));
                        //response = getSubtaskFromJson(jsonObject).toString();
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
            String name = jsonObject.get("name").toString();
            String description = jsonObject.get("description").toString();
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

    // возвращает SubTask из jsonObject
    private SubTask getSubtaskFromJson(JsonObject jsonObject) {
        Long id = Long.parseLong(jsonObject.get("id").toString());
        String name = jsonObject.get("name").toString();
        String description = jsonObject.get("description").toString();
        Long epicID = Long.parseLong(jsonObject.get("epicId").toString());
        String statusString = jsonObject.get("status").toString().toUpperCase();
        Status status = Status.setStatus(statusString);
        int year = Integer.parseInt(jsonObject.get("year").toString());
        int month = Integer.parseInt(jsonObject.get("month").toString());
        int day = Integer.parseInt(jsonObject.get("day").toString());
        int duration = Integer.parseInt(jsonObject.get("duration").toString());
        return new SubTask(epicID, id, name, description, status, year, month, day, duration);
    }

    static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        private static final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        private static final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
            jsonWriter.value(localDate.format(formatterWriter));
        }

        @Override
        public LocalDate read(final JsonReader jsonReader) throws IOException {
            return LocalDate.parse(jsonReader.nextString(), formatterReader);
        }
    }

    static class DurationAdapter extends TypeAdapter<Duration> {
       // private static final Duration formatterWriter = Duration.parse("HH");
       // private static final Duration formatterReader = Duration.ofPattern("PT10H");

        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            jsonWriter.value(String.valueOf(Duration.parse("HH")));
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws DateTimeParseException {
            return Duration.parse("HH");
        }
    }

}
