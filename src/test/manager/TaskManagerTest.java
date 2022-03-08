package test.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;
import manager.InMemoryTasksManager;
import manager.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerTest {
    static TaskManager tm;
    static Task taskNew;
    static Task taskInProgress;
    static Epic epicWithEmptySubTask;
    static SubTask subTaskNew;
    static SubTask subTaskDone;
    static Epic epicInProgress;
    static Epic epicDone;
    static Epic epicNew;

    @BeforeEach
    void beforeEach() {
        tm = new InMemoryTasksManager();
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW);
        taskInProgress = new Task(2L, "Задача 2", "описание задачи", Status.IN_PROGRESS);
        subTaskNew = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW);
        subTaskDone = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.DONE);
        epicWithEmptySubTask = new Epic(1L, "Эпик 1", "описание задачи", new ArrayList<>(), Status.NEW);
        epicInProgress = new Epic(1L, "Задача 15", "описание задачи", List.of(subTaskNew, subTaskDone), Status.IN_PROGRESS);
        epicDone = new Epic(6L, "Задача 15", "описание задачи", List.of(subTaskDone), Status.DONE);
        epicNew = new Epic(7L, "Задача 25", "описание задачи", List.of(subTaskNew), Status.DONE);


    }

    // тест, что возвращается список подзадач конкретного эпика
    @Test
    void shouldReturnSubTaskByEpicId() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        Assertions.assertEquals(List.of(subTaskNew, subTaskDone), tm.getSubTaskByEpicId(1L));
    }

    // проверка на добавление эпика и возврат списка эпиков после добавления
    @Test
    void shouldReturnEpicsList() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(epicDone);
        tm.addTask(epicNew);
        Assertions.assertEquals(List.of(epicWithEmptySubTask, epicDone, epicNew), tm.getEpicsList());
    }

    // проверка на добавление эпика и возврат списка задач после добавления
    @Test
    public void shouldReturnTaskList() {
        tm.addTask(taskNew);
        Assertions.assertEquals(List.of(taskNew), tm.getTasksList());
    }

    // проверка на добавление подзадачи и возврат списка подзадач после добавления
    @Test
    void shouldReturnSubTaskList() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        Assertions.assertEquals(List.of(subTaskNew),tm.getSubTasksList());
    }

 // проверка, что нельзя добавить подзадачу если указанного эпика не существует
    @Test
    void shouldReturnTrueIfEpicDoesNotExist() {
        tm.addTask(subTaskNew);
        Assertions.assertEquals(List.of(), tm.getSubTasksList());
    }

    // проверяем, что нельзя добавить две одинаковые подзадачи в один и тот же эпик
    @Test
    void shouldReturnTrueIfSubTaskAlreadyExist() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskNew);
        Assertions.assertEquals(List.of(subTaskNew), tm.getSubTaskByEpicId(1L));
    }


    //проверка, что возвращает задачу по ID
    @Test
    void shouldReturnTaskById() {
        tm.addTask(taskNew);
        tm.addTask(taskInProgress);
        Assertions.assertTrue(tm.getTaskById(1L) instanceof Task);
    }

    //проверка, что возвращает эпик по ID
    @Test
    void shouldReturnEpicById() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(epicDone);
        tm.addTask(epicNew);
        Assertions.assertTrue(tm.getTaskById(6L) instanceof Epic);
    }

    //проверка, что возвращает подзадачу по ID
    @Test
    void shouldReturnSubTaskById() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        Assertions.assertTrue(tm.getTaskById(3L) instanceof SubTask);
    }

   // проверка, что эпик обновляется если передать в метод изменённый объект
    @Test
    void shouldReturnUpdateEpic() {
        tm.addTask(epicWithEmptySubTask);
        Epic epic = new Epic(1L, "Эпик 1", "описание эпика после обновления", new ArrayList<>(), Status.DONE);
        tm.updateAnyTask(epic);
        Assertions.assertEquals(epic, tm.getTaskById(1L));
        Assertions.assertEquals(Status.DONE, tm.getTaskById(1L).getStatus());
        Assertions.assertEquals("описание эпика после обновления", tm.getTaskById(1L).getDescription());
        Assertions.assertEquals(1L, tm.getTaskById(1L).getId());
    }

    // проверка, что эпик обновляется если передать в метод изменённый c новыми подзадачами
    @Test
    void shouldReturnUpdateEpicWithSubTasks() {
        tm.addTask(epicWithEmptySubTask);
        Epic epic = new Epic(1L, "Эпик 1", "описание эпика после обновления", List.of(subTaskNew,subTaskDone), Status.IN_PROGRESS);
        tm.updateAnyTask(epic);
        Assertions.assertEquals(List.of(subTaskNew,subTaskDone), tm.getSubTaskByEpicId(1L));
        Assertions.assertEquals(Status.IN_PROGRESS, tm.getTaskById(1L).getStatus());
        Assertions.assertEquals("описание эпика после обновления", tm.getTaskById(1L).getDescription());
        Assertions.assertEquals(1L, tm.getTaskById(1L).getId());
    }

    // проверка, что задача обновляется если передать в метод изменённый объект
    @Test
    void shouldReturnUpdateTask() {
        tm.addTask(taskNew);
        Task task = new Task(1L, "Эпик 1", "описание задачи после обновления", Status.DONE);
        tm.updateAnyTask(task);
        Assertions.assertEquals(task, tm.getTaskById(1L));
        Assertions.assertEquals(Status.DONE, tm.getTaskById(1L).getStatus());
        Assertions.assertEquals("описание задачи после обновления", tm.getTaskById(1L).getDescription());
        Assertions.assertEquals(1L, tm.getTaskById(1L).getId());
    }

    // проверка, что подзадача обновляется если передать в метод изменённый объект
    @Test
    void shouldReturnUpdateSubTask() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        SubTask subTask = new SubTask(1L, 3L,"Подзадача 1", "описание подзадачи после обновления", Status.DONE);
        tm.updateAnyTask(subTask);
        Assertions.assertEquals(subTask, tm.getTaskById(3L));
        Assertions.assertEquals(Status.DONE, tm.getTaskById(3L).getStatus());
        Assertions.assertEquals("описание подзадачи после обновления", tm.getTaskById(3L).getDescription());
        Assertions.assertEquals(3L, tm.getTaskById(3L).getId());
    }


    //проверяем что все задачи удаляются корректно
    @Test
    void removeAllTask() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.addTask(taskNew);
        tm.removeAllTask();
        Assertions.assertEquals(List.of(), tm.getTasksList());
        Assertions.assertEquals(List.of(), tm.getSubTasksList());
        Assertions.assertEquals(List.of(), tm.getEpicsList());
    }


    //проверяем что подзадача удаляется по id
    @Test
    void shouldReturnNullAfterRemoveSubTaskById() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.removeById(3L);
        Assertions.assertNull(tm.getTaskById(3L));
    }

    //проверяем что при удалении подзадачи по id она удаляется из эпика
    @Test
    void shouldReturnListWithoutSubtaskAfterRemoveSubTaskById() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.removeById(3L);
        Assertions.assertEquals(List.of(subTaskDone), tm.getSubTaskByEpicId(1L));
    }

    // проверяем что эпик удаляется по id
    @Test
    void shouldReturnNullAfterRemoveEpicById() {
        tm.addTask(epicWithEmptySubTask);
        tm.removeById(1L);
        Assertions.assertNull(tm.getTaskById(1L));
    }

    // проверяем, что при удалении эпика по id удаляются и подзадачи эпика
    @Test
    void shouldReturnEmptyListAfterRemoveEpicById() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.removeById(1L);
        Assertions.assertEquals(List.of(), tm.getSubTasksList());
    }

    // проверяем что задача удаляется по id
    @Test
    void shouldReturnNullAfterRemoveTaskById() {
        tm.addTask(taskNew);
        tm.removeById(1L);
        Assertions.assertNull(tm.getTaskById(1L));
    }

    // проверяем что получаем верную историю просмотров
    @Test
    void shouldReturnListHistory() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.addTask(taskInProgress);
        tm.getSubTaskByEpicId(1L); // просмотр подзадачи эпика с id 1 subTaskNew и subTaskDone
        tm.getTaskById(2L); // просмотри задачи taskStatusInProgress
        tm.getTaskById(4L); // просмотри подзадачи subTaskDone, предыдущий просмотр этой подзадачи должен удалиться из истории
        Assertions.assertEquals(List.of(subTaskNew, taskInProgress, subTaskDone), tm.history());
    }

    // проверяем что получаем верную историю просмотров
    @Test
    void shouldReturnListHistoryWithSubTaskForEpic() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.addTask(taskInProgress);
        tm.getTaskById(1L); // просмотри эпика epicWithEmptySubTask
        Assertions.assertEquals(List.of(epicWithEmptySubTask), tm.history());
    }

    // проверка созданий новой задачи со статусом NEW
    @Test
    void shouldCreateTaskWithStatusNew() {
        Task task1 = tm.createTask("Задача 1", "описание задачи", Status.NEW);
        Assertions.assertEquals(taskNew, task1);
        Assertions.assertEquals(Status.NEW, task1.getStatus());
        Assertions.assertEquals(1L, task1.getId());
    }

    // тест на создание задачи со статусом IN_PROGRESS
    @Test
    void shouldCreateTaskWithStatusInProgress() {
        Task task = tm.createTask("Задача 2", "описание задачи", Status.IN_PROGRESS);
        Assertions.assertEquals(task, taskInProgress);
        Assertions.assertEquals(Status.IN_PROGRESS, task.getStatus());
        Assertions.assertEquals(1L, task.getId());
    }

    // тест на создание подзадачи со статусом NEW
    @Test
    void shouldCreateSubTaskWithStatusNew() {
        SubTask subTask = tm.createSubTask(1L, "Подзадача 1", "описание подзадачи", Status.NEW);
        Assertions.assertEquals(subTask, subTaskNew);
        Assertions.assertEquals(Status.NEW, subTask.getStatus());
        Assertions.assertEquals(1L, subTask.getId());
    }

    // тест на создание подзадачи со статусом DONE
    @Test
    void shouldCreateSubTaskWithStatusDone() {
        SubTask subTask = tm.createSubTask(1L, "Подзадача 2", "описание подзадачи", Status.DONE);
        Assertions.assertEquals(subTask, subTaskDone);
        Assertions.assertEquals(Status.DONE, subTask.getStatus());
        Assertions.assertEquals(1L, subTask.getId());
    }

    // тест на создание эпика если пустой список подзадач.
    @Test
    void shouldCreateEpicWithStatusNewIfSubTaskEmpty() {
        Epic epic = tm.createEpic("Эпик 1", "описание задачи");
        Assertions.assertEquals(epic, epicWithEmptySubTask);
        Assertions.assertEquals(Status.NEW, epic.getStatus());
        Assertions.assertEquals(1L, epic.getId());
    }

    // тест на создание эпика если все подзадачи со статусом NEW и DONE.
    @Test
    void shouldCreateEpicWithStatusInProgressIfSubTaskNewDone() {
        InMemoryTasksManager.setSubtasks(1L, new SubTask(3L, 1L, "Подзадача 1", "описание подзадачи", Status.NEW));
        InMemoryTasksManager.setSubtasks(2L, new SubTask(3L, 2L, "Подзадача 2", "описание подзадачи", Status.DONE));
        Epic epic = tm.createEpic("Задача 15", "описание задачи");
        Assertions.assertEquals(epic, epicInProgress);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
        Assertions.assertEquals(3L, epic.getId());
    }

    // тест на создание эпика если все подзадачи со статусом IN_PROGRESS
    @Test
    void shouldCreateEpicWithStatusInProgressIfSubTaskInProgress() {
        InMemoryTasksManager.setSubtasks(1L,
                                         new SubTask(3L, 1L, "Подзадача 1", "описание подзадачи", Status.IN_PROGRESS));
        InMemoryTasksManager.setSubtasks(2L,
                                         new SubTask(3L, 2L, "Подзадача 2", "описание подзадачи", Status.IN_PROGRESS));
        Epic epic = tm.createEpic("Задача 15", "описание задачи");
        Assertions.assertEquals(epic, epicInProgress);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
        Assertions.assertEquals(3L, epic.getId());
    }

    // тест на создание эпика если все подзадачи со статусом DONE.
    @Test
    void shouldCreateEpicWithStatusInDone() {
        InMemoryTasksManager.setSubtasks(1L, new SubTask(3L, 1L, "Подзадача 1", "описание подзадачи", Status.DONE));
        InMemoryTasksManager.setSubtasks(2L, new SubTask(3L, 2L, "Подзадача 2", "описание подзадачи", Status.DONE));
        Epic epic = tm.createEpic("Задача 15", "описание задачи");
        Assertions.assertEquals(epic, epicDone);
        Assertions.assertEquals(Status.DONE, epic.getStatus());
        Assertions.assertEquals(3L, epic.getId());
    }

    // тест на создание эпика если список подзадач имеет статус new.
    @Test
    void shouldCreateEpicWithStatusNewIfSubTaskNew() {
        InMemoryTasksManager.setSubtasks(1L, new SubTask(3L, 1L, "Подзадача 1", "описание подзадачи", Status.NEW));
        InMemoryTasksManager.setSubtasks(2L, new SubTask(3L, 2L, "Подзадача 2", "описание подзадачи", Status.NEW));
        Epic epic = tm.createEpic("Задача 25", "описание задачи");
        Assertions.assertEquals(epic, epicNew);
        Assertions.assertEquals(Status.NEW, epic.getStatus());
        Assertions.assertEquals(3L, epic.getId());
    }
}