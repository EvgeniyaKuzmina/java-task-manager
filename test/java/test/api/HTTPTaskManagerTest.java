package test.api;

import api.HTTPTaskManager;
import api.HttpTaskServer;
import api.KVTaskClient;
import com.google.gson.*;
import manager.Managers;
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
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPTaskManagerTest {
    static Task taskNew;
    static Task taskInProgress;
    static Epic epicWithEmptySubTask;
    static SubTask subTaskNew;
    static SubTask subTaskDone;
    static HTTPTaskManager tm;
    static Gson gson;
    static HttpClient client;
    static HttpTaskServer httpTaskServer;
    static KVTaskClient kvTaskClient;

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
    static void stopServer() {
        httpTaskServer.stop();
    }

    @BeforeEach
    void beforeEach() {
        client = HttpClient.newHttpClient();
        taskNew = new Task(3L, "Задача 1", "описание задачи", Status.NEW, 2023, 3, 25, 9);
        taskInProgress = new Task(2L, "Задача 2", "описание задачи", Status.IN_PROGRESS, 2023, 3, 25, 7);
        subTaskNew = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2023, 3, 25, 10);
        subTaskDone = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.DONE, 2023, 3, 25, 5);
        epicWithEmptySubTask = new Epic(1L, "Эпик 1", "описание задачи", new ArrayList<>(), Status.NEW, 2023, 3, 25, 1);

    }

    // проверяем что метод save() сохраняет все задачи на сервер 8078 и метод load() выгружает все сохранённые задачи с сервера
    @Test
    void shouldSaveAllTasksAndHistoryToTheServerAndThenLoadFromServer() throws IOException, InterruptedException {
        tm.addTask(taskNew);
        tm.addTask(taskInProgress);
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.getTaskById(1L);
        tm.history();
        tm.load();
        assertEquals(List.of(taskInProgress, taskNew), tm.getTasksList());
        assertEquals(List.of(epicWithEmptySubTask), tm.getEpicsList());
        assertEquals(List.of(subTaskNew), tm.getSubTasksList());
        assertEquals(List.of(subTaskNew), tm.getSubTasksList());
        assertEquals(List.of(epicWithEmptySubTask), tm.history());

    }
}
