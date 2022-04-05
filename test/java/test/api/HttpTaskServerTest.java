package test.api;

import api.HttpTaskServer;
import api.KVTaskClient;
import com.google.gson.*;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// проверка работы сервера для создания, удаления задачи и просмотра истории
class HttpTaskServerTest {
    static TaskManager tm;
    static Task taskNew;
    static Epic epicWithEmptySubTask;
    static SubTask subTaskNew;
    static SubTask subTaskDone;
    static Epic epicInProgress;
    static Gson gson;
    static KVTaskClient kvTaskClient;
    static URI url;
    static HttpTaskServer httpTaskServer;

    @BeforeAll
    static void createServer() throws IOException, InterruptedException {
        kvTaskClient = new KVTaskClient(URI.create("http://localhost:8078"));
        tm = Managers.getDefault(kvTaskClient);
        httpTaskServer = new HttpTaskServer(tm);
        httpTaskServer.start();
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
    }

    @AfterAll
    static void stop() {
        httpTaskServer.stop();
    }

    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        taskNew = new Task(2L, "Задача 1", "описание задачи", Status.NEW, 2023, 3, 25, 9);
        subTaskNew = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2023, 3, 25, 10);
        subTaskDone = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.DONE, 2023, 3, 25, 5);
        epicWithEmptySubTask = new Epic(1L, "Эпик 1", "описание задачи", new ArrayList<>(), Status.NEW, 2023, 3, 25, 1);
        epicInProgress = new Epic(1L, "Эпик 1", "описание задачи", List.of(subTaskNew, subTaskDone),
                                  Status.IN_PROGRESS, 2023, 3, 25, 15);
    }

    // проверяем на несуществующий эндпоинт, что вернётся статус 400
    @Test
    void shouldReturnStatus400IfURLWrong() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/tas/");
        String json = gson.toJson(taskNew);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest requestPost = HttpRequest.newBuilder()
                                             .uri(url)
                                             .version(HttpClient.Version.HTTP_1_1)
                                             .POST(body)
                                             .build();
        System.out.println(requestPost.toString());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(requestPost, handler);
        assertEquals(400, response.statusCode());
    }

    // проверяем эндпоинт tasks/ метод GET
    @Test
    void shouldReturnStatus200ForMethodGETasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals("[]", response.body());
        assertEquals(200, response.statusCode());
    }

    // проверяем эндпоинт tasks/?id= метод GET
    @Test
    void shouldReturnStatus200ForMethodGETasksWithId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задач с таким id нет", response.body());
        assertEquals(200, response.statusCode());
    }

    // проверяем эндпоинт tasks/?id= метод DELETE — удаление по ID
    @Test
    void shouldReturnStatus200ForMethodDELETETasksById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/?id=1");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Задача с id 1 удалена", response.body());
    }

    // проверяем эндпоинт tasks/?id= метод DELETE — удаление всех задач
    @Test
    void shouldReturnStatus200ForMethodDELETEAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все задачи удалены", response.body());
    }

    // проверяем эндпоинт tasks/ метод POST
    @Test
    void shouldReturnStatus400ForMethodPOSTTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/");
        String json = gson.toJson(taskNew);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest requestPost = HttpRequest.newBuilder()
                                             .uri(url)
                                             .version(HttpClient.Version.HTTP_1_1)
                                             .POST(body)
                                             .build();
        System.out.println(requestPost.toString());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(requestPost, handler);
        assertEquals(400, response.statusCode());
        assertEquals("Метод должен быть GET или DELETE. Вы использовали какой-то другой метод или ошибка в url адресе!",
                     response.body());
    }

    // проверяем эндпоинт tasks/task/ метод GET
    @Test
    void shouldReturnStatus200ForMethodGetTasksTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals("[]", response.body());
        assertEquals(200, response.statusCode());
    }

    // проверяем эндпоинт tasks/task/ метод POST
    @Test
    void shouldReturnStatus200ForMethodPOSTTasksTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(taskNew);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest requestPost = HttpRequest.newBuilder()
                                             .uri(url)
                                             .version(HttpClient.Version.HTTP_1_1)
                                             .POST(body)
                                             .build();
        System.out.println(requestPost.toString());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(requestPost, handler);
        assertEquals(200, response.statusCode());
        assertEquals(taskNew.toString(), response.body());
    }

    // проверяем эндпоинт tasks/task/ метод DELETE
    @Test
    void shouldReturnStatus400ForMethodDeleteTasksTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals("Метод должен быть GET или POST. Вы использовали какой-то другой метод или ошибка в url адресе!",
                     response.body());
    }

    // проверяем эндпоинт tasks/epic/ метод GET
    @Test
    void shouldReturnStatus200ForMethodGetTasksEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals("[]", response.body());
        assertEquals(200, response.statusCode());
    }

    // проверяем эндпоинт tasks/epic/ метод POST
    @Test
    void shouldReturnStatus200ForMethodPOSTTasksEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epicWithEmptySubTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest requestPost = HttpRequest.newBuilder()
                                             .uri(url)
                                             .version(HttpClient.Version.HTTP_1_1)
                                             .POST(body)
                                             .build();
        System.out.println(requestPost.toString());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(requestPost, handler);
        assertEquals(200, response.statusCode());
        assertEquals(epicWithEmptySubTask.toString(), response.body());
    }

    // проверяем эндпоинт tasks/epic/ метод DELETE
    @Test
    void shouldReturnStatus400ForMethodDeleteTasksEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals("Метод должен быть GET или POST. Вы использовали какой-то другой метод или ошибка в url адресе!",
                     response.body());
    }

    // проверяем эндпоинт tasks/subtask/ метод GET получение всех подзадач
    @Test
    void shouldReturnStatus200ForMethodGetTasksSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals("[]", response.body());
        assertEquals(200, response.statusCode());
    }

    // проверяем эндпоинт tasks/subtask/epic/?id= метод GET получение подзадачи по epicId
    @Test
    void shouldReturnStatus200ForMethodGETasksSubtaskByEpicId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Эпика с таким id нет", response.body());
        assertEquals(200, response.statusCode());
    }

    // проверяем эндпоинт tasks/subtask/ метод POST
    @Test
    void shouldReturnStatus200ForMethodPOSTTasksSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/subtask/");
        String json = gson.toJson(subTaskNew);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest requestPost = HttpRequest.newBuilder()
                                             .uri(url)
                                             .version(HttpClient.Version.HTTP_1_1)
                                             .POST(body)
                                             .build();
        System.out.println(requestPost.toString());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(requestPost, handler);
        assertEquals(200, response.statusCode());
        assertEquals(subTaskNew.toString(), response.body());
    }

    // проверяем эндпоинт tasks/subtask/ метод DELETE
    @Test
    void shouldReturnStatus400ForMethodDeleteTasksSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals("Метод должен быть GET или POST. Вы использовали какой-то другой метод или ошибка в url адресе!",
                     response.body());
    }

    // проверяем эндпоинт tasks/history/ метод GET
    @Test
    void shouldReturnStatus200ForMethodGetTasksHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals("[]", response.body());
        assertEquals(200, response.statusCode());
    }

    // проверяем эндпоинт tasks/history/ метод POST
    @Test
    void shouldReturnStatus400ForMethodPOSTTasksHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/history/");
        String json = gson.toJson(subTaskNew);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest requestPost = HttpRequest.newBuilder()
                                             .uri(url)
                                             .version(HttpClient.Version.HTTP_1_1)
                                             .POST(body)
                                             .build();
        System.out.println(requestPost.toString());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(requestPost, handler);
        assertEquals(400, response.statusCode());
        assertEquals("Метод должен быть GET или POST. Вы использовали какой-то другой метод или ошибка в url адресе!",
                     response.body());
    }

    // проверяем эндпоинт tasks/history/ метод DELETE
    @Test
    void shouldReturnStatus400ForMethodDeleteTasksHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals("Метод должен быть GET или POST. Вы использовали какой-то другой метод или ошибка в url адресе!",
                     response.body());
    }


}