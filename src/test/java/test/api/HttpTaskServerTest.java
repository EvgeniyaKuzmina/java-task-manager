package test.api;

import api.HttpTaskServer;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import manager.InMemoryTasksManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    static TaskManager tm;
    static Task taskNew;
    static Task taskInProgress;
    static Epic epicWithEmptySubTask;
    static SubTask subTaskNew;
    static SubTask subTaskDone;
    static SubTask subTaskInProgress;
    static Epic epicInProgress;
    static Epic epicDone;
    static Epic epicNew;
    static Gson gson;
    static HttpClient client;
    static URI url;
    static HttpTaskServer httpTaskServer;


    @BeforeAll
    static void beforeAll() throws IOException {
        httpTaskServer = new HttpTaskServer(tm);
        httpTaskServer.start();
        gson = new Gson();
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        tm = Managers.getServerManager("http://localhost:8078");
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW, 2023, 3, 25, 9);
        taskInProgress = new Task(2L, "Задача 2", "описание задачи", Status.IN_PROGRESS, 2023, 3, 25, 7);
        subTaskNew = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2023, 3, 25, 10);
        subTaskDone = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.DONE, 2023, 3, 25, 5);
        subTaskInProgress = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.IN_PROGRESS, 2023, 3, 25,
                                        10);
        epicWithEmptySubTask = new Epic(1L, "Эпик 1", "описание задачи", new ArrayList<>(), Status.NEW, 2023, 3, 25, 1);
        epicInProgress = new Epic(1L, "Задача 15", "описание задачи", List.of(subTaskNew, subTaskDone),
                                  Status.IN_PROGRESS, 2023, 3, 25, 15);
        epicDone = new Epic(6L, "Задача 15", "описание задачи", List.of(subTaskDone), Status.DONE, 2023, 3, 25, 15);
        epicNew = new Epic(7L, "Задача 25", "описание задачи", List.of(subTaskNew), Status.DONE, 2023, 3, 25, 15);
    }


    @Test
    void shouldReturnTrueCreateTaskOnTheServer() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(taskNew);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest requestPost = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(json, response.body());
    }
    @Test
    void shouldReturnTrueGetTaskFromServer() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JsonElement jsonElement = JsonParser.parseString(response.body());
            if (!jsonElement.isJsonObject()) {
                System.out.println("Ответ от сервера не соответствует ожидаемому.");
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            System.out.println(jsonObject.toString());
           // return jsonObject.get(key).getAsString();
        } else {
            System.out.println( "Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
        }

        tm.addTask(taskNew);
        assertEquals(List.of(taskNew), tm.getTasksList());
    }



    @AfterAll
    static void stop() {
        HttpTaskServer.stop();
    }


}