package test.task;

import org.junit.jupiter.api.Test;
import task.Status;
import task.SubTask;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubTaskTest {
    SubTask subtask;

    //проверяем что возвращает id эпика к которому относится
    @Test
    void shouldReturnEpicId() {
        subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW);
        assertEquals(1L, subtask.getEpicId());
    }

    // проверяем что возвращает подзадачи из строки
    @Test
    void ShouldReturnSubTaskFromString() {
        subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW);
        assertEquals(subtask, SubTask.fromString("3,SUBTASK,Подзадача 1,NEW,описание подзадачи,1"));
    }
}