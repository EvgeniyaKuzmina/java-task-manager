package api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import task.Status;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HttpTaskServer {


    TaskManager tasksManager;
    HttpServer httpServer;
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static void main(String[] args) throws IOException {
        new HttpTaskServer();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public HttpTaskServer() throws IOException {
        tasksManager = Managers.getFileBackedManager("recources/recources");
        httpServer =  HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.createContext("/tasks/subtask", new SubtaskHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
        httpServer.start();

    }
    //System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

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
                case "POST":
                    response = "Метод должен быть GET или DELETE";
                    break;
                case "DELETE":
                    if  (query != null) {
                        Long id = Long.parseLong(query.split("=")[1]);
                        tasksManager.removeById(id);
                        response = String.format("Задача id %d удалена", id);
                    } else {
                        tasksManager.removeAllTask();
                        response = "Все задачи удалены";
                    }
                    break;
                default:
                    response = "Вы использовали какой-то другой метод!";
            }
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

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
                    tasksManager.addTask(createTaskFromJson(jsonObject));
                    response = tasksManager.getTasksList().toString();
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
    }

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
                    tasksManager.addTask(createTaskFromJson(jsonObject));
                    response = tasksManager.getEpicsList().toString();
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
    }

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
                    tasksManager.addTask(createTaskFromJson(jsonObject));
                    response = tasksManager.getEpicsList().toString();
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
    }

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

    private Task createTaskFromJson(JsonObject jsonObject ) {
        String name = jsonObject.get("nameTask").toString();
        String description = jsonObject.get("description").toString();
        if (jsonObject.keySet().contains("epicId")) {
            Long epicID = Long.parseLong(jsonObject.get("epicId").toString());
            String statusString = jsonObject.get("status").toString().toUpperCase();
            Status status = Status.setStatus(statusString);
            int year = Integer.parseInt(jsonObject.get("year").toString());
            int month = Integer.parseInt(jsonObject.get("month").toString());
            int day = Integer.parseInt(jsonObject.get("day").toString());
            int duration = Integer.parseInt(jsonObject.get("duration").toString());
            return tasksManager.createSubTask(epicID, name, description, status, year, month, day, duration);
        } else if (jsonObject.size() == 2) {
            return tasksManager.createEpic(name, description);
        } else {
            String statusString = jsonObject.get("status").toString().toUpperCase();
            Status status = Status.setStatus(statusString);
            int year = Integer.parseInt(jsonObject.get("year").toString());
            int month = Integer.parseInt(jsonObject.get("month").toString());
            int day = Integer.parseInt(jsonObject.get("day").toString());
            int duration = Integer.parseInt(jsonObject.get("duration").toString());
            return tasksManager.createTask(name, description, status, year, month, day, duration);
        }
    }
}
