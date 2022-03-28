package test.task;

import org.junit.jupiter.api.Test;
import task.Status;
import task.SubTask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusTest {


    // проверяем что возвращает статус NEW для Эпика если список подзадач пустой
    @Test
    void shouldReturnStatusNewForEpic() {
        assertEquals(Status.NEW, Status.getStatusForEpic(List.of()));
    }

    // проверяем что возвращает статус NEW для Эпика если подзадачи в статусе NEW
    @Test
    void shouldReturnStatusNewForEpicIfAllSubTasksNew() {
        SubTask subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2023, 3, 25, 15);
        SubTask subtask1 = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.NEW, 2023, 3, 25, 15);
        assertEquals(Status.NEW, Status.getStatusForEpic(List.of(subtask, subtask1)));
    }

    // проверяем что возвращает статус InProgress для Эпика если подзадачи в статусе NEW и DONE
    @Test
    void shouldReturnStatusInProgressForEpicIfSubTasksNewDone() {
        SubTask subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.DONE, 2023, 3, 25, 15);
        SubTask subtask1 = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.NEW, 2023, 3, 25, 15);
        assertEquals(Status.IN_PROGRESS, Status.getStatusForEpic(List.of(subtask, subtask1)));
    }

    // проверяем что возвращает статус InProgress для Эпика если подзадачи в статусе IN_PROGRESS
    @Test
    void shouldReturnStatusInProgressForEpicIfSubTasksInProgress() {
        SubTask subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.IN_PROGRESS, 2023, 3, 25, 15);
        SubTask subtask1 = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.IN_PROGRESS, 2023, 3, 25,
                                       15);
        assertEquals(Status.IN_PROGRESS, Status.getStatusForEpic(List.of(subtask, subtask1)));
    }

    // проверяем что возвращает статус InProgress для Эпика если подзадачи в статусе NEW и IN_PROGRESS
    @Test
    void shouldReturnStatusInProgressForEpicIfSubTasksInProgressNew() {
        SubTask subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2023, 3, 25, 15);
        SubTask subtask1 = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.IN_PROGRESS, 2023, 3, 25,
                                       15);
        assertEquals(Status.IN_PROGRESS, Status.getStatusForEpic(List.of(subtask, subtask1)));
    }

    // проверяем что возвращает статус InProgress для Эпика если подзадачи в статусе NEW и IN_PROGRESS и DONE
    @Test
    void shouldReturnStatusInProgressForEpicIfSubTasksInProgressNewDone() {
        SubTask subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2023, 3, 25, 15);
        SubTask subtask1 = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.IN_PROGRESS, 2023, 3, 25,
                                       15);
        SubTask subtask2 = new SubTask(1L, 5L, "Подзадача 2", "описание подзадачи", Status.DONE, 2023, 3, 25, 15);
        assertEquals(Status.IN_PROGRESS, Status.getStatusForEpic(List.of(subtask, subtask1, subtask2)));
    }

    // проверяем что возвращает статус DONE для Эпика если подзадачи в статусе DONE
    @Test
    void shouldReturnStatusDoneForEpicIfSubTasksDone() {
        SubTask subtask = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.DONE, 2023, 3, 25, 15);
        SubTask subtask1 = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.DONE, 2023, 3, 25, 15);
        SubTask subtask2 = new SubTask(1L, 5L, "Подзадача 2", "описание подзадачи", Status.DONE, 2023, 3, 25, 15);
        assertEquals(Status.DONE, Status.getStatusForEpic(List.of(subtask, subtask1, subtask2)));
    }

    // проверяем что возвращает статус NEW из строки в любом регистре
    @Test
    void shouldReturnStatusNew() {
        assertEquals(Status.NEW, Status.toStatus("New"));
    }

    // проверяем что возвращает статус IN_PROGRESS из строки в любом регистре
    @Test
    void shouldReturnStatusInProgress() {
        assertEquals(Status.IN_PROGRESS, Status.toStatus("IN_prOGRESS"));
    }

    // проверяем что возвращает статус DONE из строки в любом регистре
    @Test
    void shouldReturnStatusDone() {
        assertEquals(Status.DONE, Status.toStatus("done"));
    }
}