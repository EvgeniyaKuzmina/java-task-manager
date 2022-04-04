package test.task;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    Epic epic;

    // проверяем что возвращает тип Epic из переданной строки
    @Test
    void shouldReturnTrueEpicFromString() {
        epic = new Epic(1L, "Эпик 1", "описание задачи", List.of(), Status.NEW, 2023, 3, 25, 15);
        assertEquals(epic, Epic.fromString("1,EPIC,Эпик 1,NEW,описание задачи,25.3.2023,15", List.of()));

    }

    // проверяем что получаем все подзадачи эпика
    @Test
    void shouldReturnSubtasks() {
        SubTask subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2023, 3, 25, 15);
        SubTask subtask1 = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.DONE, 2023, 3, 25, 15);
        epic = new Epic(1L, "Эпик 1", "описание задачи", List.of(subtask, subtask1), Status.NEW, 2023, 3, 25, 15);
        assertEquals(List.of(subtask, subtask1), epic.getSubtasks());
    }

    // проверяем что возвращает строку из Эпика
    @Test
    void shouldReturnStringFromEpic() {
        epic = new Epic(1L, "Эпик 1", "описание задачи", List.of(), Status.NEW, 2023, 3, 25, 15);
        assertEquals("1,EPIC,Эпик 1,NEW,описание задачи,25.03.2023,15\n", epic.toString());
    }
}