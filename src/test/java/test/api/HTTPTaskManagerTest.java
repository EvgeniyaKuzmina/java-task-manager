
package test.api;

import api.HTTPTaskManager;
import api.HttpTaskServer;
import com.google.gson.*;
import manager.Managers;
import manager.TaskManager;
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

import static org.junit.jupiter.api.Assertions.*;

class HTTPTaskManagerTest {
    static Task taskNew;
    static Task taskInProgress;
    static Epic epicWithEmptySubTask;
    static SubTask subTaskNew;
    static SubTask subTaskDone;
    static SubTask subTaskInProgress;
    static Epic epicInProgress;
    static Epic epicDone;
    static Epic epicNew;
    static HTTPTaskManager tm;
    static Gson gson;
    static HttpClient client;
    static URI url;
    static HttpTaskServer httpTaskServer;

    @BeforeAll
    static void createServer() throws IOException, InterruptedException {
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
        client = HttpClient.newHttpClient();
        tm = Managers.getDefault();
    }

    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {

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


    // здесь я просто пытаюсь понять как работает метод load() и что он работает корректно. Но он зависает на выполнении программы.
    //Если я запускаю код и дебажу его по старинке, своим любимым println, то метод load(String key) класса KVTaskClient возвращает мне String json как надо
    // То есть я получаю вот такое что-то например
    // [{"id":1,"name":"Задача 1","description":"описание задачи","status":"NEW","startTime":"2023-03-25","duration":"PT9H"},
    // {"id":2,"name":"Задача 2","description":"описание задачи","status":"IN_PROGRESS","startTime":"2023-03-25","duration":"PT7H"}]
    // А в следующей строке ArrayList<Task> tasks = gson.fromJson(taskClient.load("task"), new TypeToken<ArrayList<Task>>() {
    //        }.getType()); выполнение кода зависает
    @Test
    void save() throws IOException, InterruptedException {
        tm.addTask(taskNew);
        tm.addTask(taskInProgress);
        tm.load();
        assertEquals(List.of(taskNew, taskInProgress), tm.getTasksList());
        assertEquals(List.of(taskNew, taskInProgress), tm.getTasksList());
    }

    @Test
    void load2() {
    }
}
