package test.history;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest {

    static HistoryManager hm;
    static Task task;
    static Epic epic;
    static SubTask subTaskNew;
    static SubTask subTaskDone;


    @BeforeEach
    void beforeEach() {
        hm = new InMemoryHistoryManager();
        task = new Task(2L, "Задача 1", "описание задачи", Status.NEW, 2022, 3, 25, 15);
        subTaskNew = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2022, 3, 25, 15);
        subTaskDone = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.DONE, 2022, 3, 25, 15);
        epic = new Epic(1L, "Эпик 1", "описание задачи", new ArrayList<>(), Status.NEW, 2022, 3, 25, 15);
    }


    // проверяем что задачи добавляется в список историй
    @Test
    void shouldAddToListHistory() {
        hm.add(task);
        hm.add(subTaskNew);
        hm.add(epic);
        assertEquals(List.of(task, subTaskNew, epic), hm.getHistoryList());

    }

    // проверяем что задачи добавляется в список историй в самый конец, а ранее просмотренная эта же задача удаляется из списка
    @Test
    void shouldAddToTheEndListHistory() {
        hm.add(task);
        hm.add(subTaskNew);
        hm.add(epic);
        hm.add(task); // повторный просмотр задачи, она должна оказаться в конце списка
        assertEquals(List.of(subTaskNew, epic, task), hm.getHistoryList());

    }

    // проверяем что при добавлении одной и той же задачи несколько раз, в списки историй не будет дублей
    @Test
    void shouldAddOnlyOneTaskToListHistory() {
        hm.add(task);
        hm.add(task);
        hm.add(task);
        assertEquals(List.of(task), hm.getHistoryList());
    }

    // проверяем что при удалении задачи, она удаляется из истории просмотров
    @Test
    void shouldRemoveTaskFromListHistory() {
        hm.add(task);
        hm.add(subTaskNew);
        hm.add(epic);
        hm.remove(1L);
        assertEquals(List.of(task, subTaskNew), hm.getHistoryList());
    }


    // проверяем что если задачи в список историй не добавлены, при вызове метода возвращается пустой список
    @Test
    void shouldReturnEmptyHistoryList() {
        assertEquals(List.of(), hm.getHistoryList());
    }

    // проверяем если добавили задачи, а потом удалили все, из истории они тоже удаляются
    @Test
    void removeAll() {
        hm.add(task);
        hm.add(subTaskNew);
        hm.add(epic);
        hm.removeAll();
        assertEquals(List.of(), hm.getHistoryList());
    }
}