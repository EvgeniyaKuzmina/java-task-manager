package test.manager;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest {
    FileBackedTasksManager tm;

    // проверяем, если в файле нет данных, то возвращается объект класс FileBackedTasksManager
    @Test
    void shouldReturnFileBackedTasksManagerIfFileEmpty() {
        assertTrue(FileBackedTasksManager.loadFromFile(
                Paths.get("recources/forTestIfFileEmpty")) instanceof FileBackedTasksManager);

    }

    // проверяем, если в файле данные есть, то они восстанавливаются в соответствующую хешмапу класса InMemoryTasksManager
    @Test
    void shouldReturnFileBackedTasksManagerWithRestoredTasks() {
        SubTask subtask = new SubTask(4L, 5L, "подзадача 1", "описание подзадачи 1", Status.NEW);
        SubTask subtask1 = new SubTask(4L, 6L, "подзадача 2", "описание подзадачи 2", Status.NEW);
        SubTask subtask2 = new SubTask(4L, 7L, "подзадача 3", "описание подзадачи 3", Status.NEW);

        Task task = new Task(1L, "Задача 15", "описание задачи", Status.NEW);
        Task task1 = new Task(2L, "Задача 526", "описание задачи", Status.IN_PROGRESS);
        Task task2 = new Task(3L, "Задача 20", "новое описание задачи", Status.IN_PROGRESS);

        Epic epic = new Epic(4L, "Эпик 15", "описание задачи", List.of(subtask, subtask1, subtask2), Status.NEW);

        tm = FileBackedTasksManager.loadFromFile(Paths.get("recources/forTest"));
        assertEquals(List.of(task, task1, task2), tm.getTasksList());
        assertEquals(List.of(subtask, subtask1, subtask2), tm.getSubTasksList());
        assertEquals(List.of(epic), tm.getEpicsList());
    }

}