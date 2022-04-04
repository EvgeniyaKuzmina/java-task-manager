package test.task;

import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    static Task taskNew;

    // проверяем что возвращает тип TASK из переданной строки
    @Test
    void shouldReturnTaskFromString() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW, 2023, 3, 25, 15);
        assertEquals(taskNew, Task.fromString("1,TASK,Задача 1,NEW,описание задачи,25.3.2023,2022"));
    }

    // проверяем что возвращает строку из таски
    @Test
    void shouldReturnStringFromTask() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW, 2023, 3, 25, 15);
        assertEquals("1,TASK,Задача 1,NEW,описание задачи,25.03.2023,15\n", taskNew.toString());
    }

    // проверяем что возвращает статус задачи
    @Test
    void shouldReturnStatusNewFromTask() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW, 2023, 3, 25, 15);
        assertEquals(Status.NEW, taskNew.getStatus());
    }

    // проверяем что возвращает описание задачи
    @Test
    void shouldReturnDescriptionFromTask() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW, 2023, 3, 25, 15);
        assertEquals("описание задачи", taskNew.getDescription());
    }

    // проверяем что возвращает id задачи
    @Test
    void shouldReturnIdFromTask() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW, 2023, 3, 25, 15);
        assertEquals(1L, taskNew.getId());
    }

    // проверяем что возвращает название задачи
    @Test
    void shouldReturnNameFromTask() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW, 2023, 3, 25, 15);
        assertEquals("Задача 1", taskNew.getName());
    }


}