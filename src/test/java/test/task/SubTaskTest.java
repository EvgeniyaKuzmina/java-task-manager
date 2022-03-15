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
        subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2022, 3, 25, 15);
        assertEquals(1L, subtask.getEpicId());
    }

    // проверяем что возвращает подзадачи из строки
    @Test
    void ShouldReturnSubTaskFromString() {
        subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2022, 3, 25, 15);
        assertEquals(subtask, SubTask.fromString("3,SUBTASK,Подзадача 1,NEW,описание подзадачи,25.3.2022,15,1"));
    }
}