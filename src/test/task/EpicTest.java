package test.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;

    // проверяем что возвращает тип Epic из переданной строки
    @Test
    void shouldReturnTrueEpicFromString() {
        epic = new Epic(1L, "Эпик 1", "описание задачи", List.of(), Status.NEW);
        Assertions.assertEquals(epic, Epic.fromString("1,EPIC,Эпик 1,NEW,описание задачи", List.of()));

    }

    // проверяем что получаем все подзадачи эпика
    @Test
    void shouldReturnSubtasks() {
        SubTask subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW);
        SubTask subtask1 = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.DONE);
        epic = new Epic(1L, "Эпик 1", "описание задачи", List.of(subtask, subtask1), Status.NEW);
        Assertions.assertEquals(List.of(subtask, subtask1), epic.getSubtasks());
    }

    // проверяем что возвращает строку из Эпика
    @Test
    void shouldReturnStringFromEpic() {
        epic = new Epic(1L, "Эпик 1", "описание задачи", List.of(), Status.NEW);
        Assertions.assertEquals("1,EPIC,Эпик 1,NEW,описание задачи\n",epic.toString());
    }
}