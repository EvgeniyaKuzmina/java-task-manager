package test.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Task;

class TaskTest {

    static Task taskNew;

    // проверяем что возвращает тип TASK из переданной строки
    @Test
    void shouldReturnTaskFromString() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW);
        Assertions.assertEquals(taskNew, Task.fromString("1,TASK,Задача 1,NEW,описание задачи"));
    }

    // проверяем что возвращает строку из таски
    @Test
    void shouldReturnStringFromTask() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW);
        Assertions.assertEquals("1,TASK,Задача 1,NEW,описание задачи\n", taskNew.toString());
    }

    // проверяем что возвращает статус задачи
    @Test
    void shouldReturnStatusNewFromTask() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW);
        Assertions.assertEquals(Status.NEW, taskNew.getStatus());
    }

    // проверяем что возвращает описание задачи
    @Test
    void shouldReturnDescriptionFromTask() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW);
        Assertions.assertEquals("описание задачи", taskNew.getDescription());
    }

    // проверяем что возвращает id задачи
    @Test
    void shouldReturnIdFromTask() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW);
        Assertions.assertEquals(1L, taskNew.getId());
    }

    // проверяем что возвращает название задачи
    @Test
    void shouldReturnNameFromTask() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW);
        Assertions.assertEquals("Задача 1", taskNew.getName());
    }



}