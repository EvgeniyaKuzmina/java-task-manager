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
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class TaskManagerTest {
    static TaskManager tm;
    static Task taskNew;
    static Task taskInProgress;
    static Epic epicWithEmptySubTask;
    static SubTask subTaskNew;
    static SubTask subTaskDone;
    static SubTask subTaskInProgress;
    static Epic epicInProgress;
    static Epic epicDone;
    static Epic epicNew;


    @BeforeEach
    void beforeEach() {
        tm = new InMemoryTasksManager();
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW, 2022, 3,25,9);
        taskInProgress = new Task(2L, "Задача 2", "описание задачи", Status.IN_PROGRESS, 2022, 3,25,7);
        subTaskNew = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2022, 3,25,10);
        subTaskDone = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.DONE, 2022, 3,25,5);
        subTaskInProgress = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.IN_PROGRESS, 2022, 3,25,10);
        epicWithEmptySubTask = new Epic(1L, "Эпик 1", "описание задачи", new ArrayList<>(), Status.NEW, 2022, 3,25,1);
        epicInProgress = new Epic(1L, "Задача 15", "описание задачи", List.of(subTaskNew, subTaskDone), Status.IN_PROGRESS, 2022, 3,25,15);
        epicDone = new Epic(6L, "Задача 15", "описание задачи", List.of(subTaskDone), Status.DONE, 2022, 3,25,15);
        epicNew = new Epic(7L, "Задача 25", "описание задачи", List.of(subTaskNew), Status.DONE, 2022, 3,25,15);


    }

    // тест, что возвращается список подзадач конкретного эпика
    @Test
    void shouldReturnSubTaskByEpicId() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        Assertions.assertEquals(List.of(subTaskNew, subTaskDone), tm.getSubTaskByEpicId(1L));
    }

    // тест, что возвращается null если id неверный
    @Test
    void shouldReturnEmptyListSubTaskByEpicId() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        Assertions.assertEquals(List.of(),tm.getSubTaskByEpicId(0L));
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
    void shouldReturnTaskList() {
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
        Epic epic = new Epic(1L, "Эпик 1", "описание эпика после обновления", new ArrayList<>(), Status.DONE, 2022, 3,25,15);
        tm.updateAnyTask(epic);
        Assertions.assertEquals(epic, tm.getTaskById(1L));
        Assertions.assertEquals(Status.NEW, tm.getTaskById(1L).getStatus()); // нельзя изменить статус у пустого эпика
        // потому что статус эпика зависит от статуса подзадач. Если подзадач нет, эпик имеет статус NEW
        Assertions.assertEquals("описание эпика после обновления", tm.getTaskById(1L).getDescription());
        Assertions.assertEquals(1L, tm.getTaskById(1L).getId());
    }

    // проверка, что эпик обновляется если передать в метод изменённый c новыми подзадачами
    @Test
    void shouldReturnUpdateEpicWithSubTasks() {
        tm.addTask(epicWithEmptySubTask);
        Epic epic = new Epic(1L, "Эпик 1", "описание эпика после обновления", List.of(subTaskNew,subTaskDone), Status.IN_PROGRESS, 2022, 3,25,15);
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
        Task task = new Task(1L, "Эпик 1", "описание задачи после обновления", Status.DONE, 2022, 3,25,15);
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
        SubTask subTask = new SubTask(1L, 3L,"Подзадача 1", "описание подзадачи после обновления", Status.DONE, 2022, 3,25,15);
        tm.updateAnyTask(subTask);
        Assertions.assertEquals(List.of(subTask), tm.getSubTasksList());
        Assertions.assertEquals(subTask, tm.getTaskById(3L));
        Assertions.assertEquals(Status.DONE, tm.getTaskById(3L).getStatus());
        Assertions.assertEquals("описание подзадачи после обновления", tm.getTaskById(3L).getDescription());
        Assertions.assertEquals(3L, tm.getTaskById(3L).getId());
    }

    // проверка, что эпик обновляется если передать в метод изменённую подзадачу
    @Test
    void shouldReturnUpdateEpicsSubTask() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        SubTask subTask = new SubTask(1L, 3L,"Подзадача 1", "описание подзадачи после обновления", Status.DONE, 2022, 3,25,15);
        tm.updateAnyTask(subTask);
        Assertions.assertEquals(List.of(subTask), tm.getSubTaskByEpicId(1L));
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

    // проверяем что при передаче id меньше 0 метод ничего не удаляет
    @Test
    void shouldReturnAnswerIdIncorrect() {
        tm.addTask(epicWithEmptySubTask);
        Assertions.assertEquals("Ошибка в ID", tm.removeById(-1L));
    }

    // проверяем что при передаче id которого нет метод ничего не удаляет
    @Test
    void shouldReturnAnswerIdDoesNotExist() {
        tm.addTask(epicWithEmptySubTask);
        Assertions.assertEquals("Задач с таким id нет", tm.removeById(2L));
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

    // проверяем что получаем верную историю просмотров эпика
    @Test
    void shouldReturnListHistoryWithSubTaskForEpic() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.addTask(taskInProgress);
        tm.getTaskById(1L); // просмотр эпика epicWithEmptySubTask
        Assertions.assertEquals(List.of(epicWithEmptySubTask), tm.history());
    }
    @Test
    // проверяем что получаем пустую историю просмотров
    void shouldReturnEmptyListHistory() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.addTask(taskInProgress);
        tm.getTasksList(); // просмотр всех задач
        tm.getSubTasksList(); // просмотр всех подзадач
        tm.getEpicsList(); // просмотр всех эпиков
        Assertions.assertEquals(List.of(), tm.history());
    }

    // проверка созданий новой задачи со статусом NEW
    @Test
    void shouldCreateTaskWithStatusNew() {
        Task task1 = tm.createTask("Задача 1", "описание задачи", Status.NEW, 2022, 3,25,15);
        Assertions.assertEquals(taskNew, task1);
        Assertions.assertEquals(Status.NEW, task1.getStatus());
        Assertions.assertEquals(1L, task1.getId());
    }

    // тест на создание задачи со статусом IN_PROGRESS
    @Test
    void shouldCreateTaskWithStatusInProgress() {
        Task task = tm.createTask("Задача 2", "описание задачи", Status.IN_PROGRESS, 2022, 3,25,15);
        Assertions.assertEquals(task, taskInProgress);
        Assertions.assertEquals(Status.IN_PROGRESS, task.getStatus());
        Assertions.assertEquals(1L, task.getId());
    }

    // тест на создание подзадачи со статусом NEW
    @Test
    void shouldCreateSubTaskWithStatusNew() {
        SubTask subTask = tm.createSubTask(1L, "Подзадача 1", "описание подзадачи", Status.NEW, 2022, 3,25,15);
        Assertions.assertEquals(subTask, subTaskNew);
        Assertions.assertEquals(Status.NEW, subTask.getStatus());
        Assertions.assertEquals(1L, subTask.getId());
    }

    // тест на создание подзадачи со статусом DONE
    @Test
    void shouldCreateSubTaskWithStatusDone() {
        SubTask subTask = tm.createSubTask(1L, "Подзадача 2", "описание подзадачи", Status.DONE,2022, 3,25,15);
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
        InMemoryTasksManager.setSubtasks(1L, new SubTask(3L, 1L, "Подзадача 1", "описание подзадачи", Status.NEW, 2022, 3,25,15));
        InMemoryTasksManager.setSubtasks(2L, new SubTask(3L, 2L, "Подзадача 2", "описание подзадачи", Status.DONE, 2022, 3,25,15));
        Epic epic = tm.createEpic("Задача 15", "описание задачи");
        Assertions.assertEquals(epic, epicInProgress);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
        Assertions.assertEquals(3L, epic.getId());
    }

    // тест на создание эпика если все подзадачи со статусом IN_PROGRESS
    @Test
    void shouldCreateEpicWithStatusInProgressIfSubTaskInProgress() {
        InMemoryTasksManager.setSubtasks(1L,
                                         new SubTask(3L, 1L, "Подзадача 1", "описание подзадачи", Status.IN_PROGRESS, 2022, 3,25,15));
        InMemoryTasksManager.setSubtasks(2L,
                                         new SubTask(3L, 2L, "Подзадача 2", "описание подзадачи", Status.IN_PROGRESS, 2022, 3,25,15));
        Epic epic = tm.createEpic("Задача 15", "описание задачи");
        Assertions.assertEquals(epic, epicInProgress);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
        Assertions.assertEquals(3L, epic.getId());
    }

    // тест на создание эпика если все подзадачи со статусом DONE.
    @Test
    void shouldCreateEpicWithStatusInDone() {
        InMemoryTasksManager.setSubtasks(1L, new SubTask(3L, 1L, "Подзадача 1", "описание подзадачи", Status.DONE, 2022, 3,25,15));
        InMemoryTasksManager.setSubtasks(2L, new SubTask(3L, 2L, "Подзадача 2", "описание подзадачи", Status.DONE, 2022, 3,25,15));
        Epic epic = tm.createEpic("Задача 15", "описание задачи");
        Assertions.assertEquals(epic, epicDone);
        Assertions.assertEquals(Status.DONE, epic.getStatus());
        Assertions.assertEquals(3L, epic.getId());
    }

    // тест на создание эпика если список подзадач имеет статус new.
    @Test
    void shouldCreateEpicWithStatusNewIfSubTaskNew() {
        InMemoryTasksManager.setSubtasks(1L, new SubTask(3L, 1L, "Подзадача 1", "описание подзадачи", Status.NEW, 2022, 3,25,15));
        InMemoryTasksManager.setSubtasks(2L, new SubTask(3L, 2L, "Подзадача 2", "описание подзадачи", Status.NEW, 2022, 3,25,15));
        Epic epic = tm.createEpic("Задача 25", "описание задачи");
        Assertions.assertEquals(epic, epicNew);
        Assertions.assertEquals(Status.NEW, epic.getStatus());
        Assertions.assertEquals(3L, epic.getId());
    }

    // проверяем что при указании продолжительности выполнения задачи в один день, можно выполнить в общей сложности
    // то количество задач, которое не превышает более 24 часов в день.
    @Test
    void shouldNotAddTasksIfDurationInDayMore22Hours() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.addTask(taskInProgress);
        tm.addTask(subTaskInProgress); // не должен добавить задачу, в задаче subTaskNew, subTaskDone и taskInProgress
        // общая сумма продолжительности будет 22 часа
        Assertions.assertEquals(List.of(subTaskNew, subTaskDone), tm.getSubTasksList());
        Assertions.assertEquals(List.of(epicWithEmptySubTask), tm.getEpicsList());
        Assertions.assertEquals(List.of(taskInProgress), tm.getTasksList());

    }

    @Test
    void shouldNotAddTasksIfDurationInDayMoreThem24() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.addTask(taskNew);
        tm.addTask(subTaskInProgress); // не должен добавить задачу, в задаче subTaskNew, subTaskDone и taskInProgress
        // общая сумма продолжительности будет 24 часа
        Assertions.assertEquals(List.of(subTaskNew, subTaskDone), tm.getSubTasksList());
        Assertions.assertEquals(List.of(epicWithEmptySubTask), tm.getEpicsList());
        Assertions.assertEquals(List.of(taskNew), tm.getTasksList());

    }

    // проверяем что нельзя обновить задач, если продолжительность выполнения превышает 24 часа
    @Test
    void shouldNotUpdateTasksIfDurationInDayMoreThem24() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.addTask(taskNew);
        tm.updateAnyTask(new Task(1L, "Задача 1", "новое описание задачи с новым сроком выполнения",
                                  Status.NEW, 2022, 3,25,15)); // не должен добавить задачу, в задаче subTaskNew, subTaskDone и taskInProgress
                                   // общая сумма продолжительности будет 24 часа
        Assertions.assertEquals(List.of(subTaskNew, subTaskDone), tm.getSubTasksList());
        Assertions.assertEquals(List.of(epicWithEmptySubTask), tm.getEpicsList());
        Assertions.assertEquals(List.of(taskNew), tm.getTasksList());

    }

    // проверяем что можно обновить эпик, независимо от того какая продолжительность указана при обновлении.
    // Продолжительность эпика зависит от продолжительности подзадач
    @Test
    void shouldUpdateEpicIfDurationInDayMoreThem24() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.addTask(taskNew);
        tm.updateAnyTask(new Epic(1L, "Эпик 1", "описание задачи", List.of(subTaskNew, subTaskDone), Status.NEW,
                                  2022, 3,25,25));

        Assertions.assertEquals(List.of(subTaskNew, subTaskDone), tm.getSubTasksList());
        Assertions.assertEquals(List.of(epicWithEmptySubTask), tm.getEpicsList());
        Assertions.assertEquals(List.of(taskNew), tm.getTasksList());

    }
    // проверяем что все задачи при вызове метода getPrioritizedTasks добавляет в отсортированный по дате список
    @Test
    void shouldAddAllTasksToSortedSetAccordingToDataStart() {
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(subTaskDone);
        tm.addTask(taskNew);
        Comparator<Task> comparator = (task1, task2) -> {
            if (task1.getStartTime().isBefore(task2.getStartTime())) {
                return -1;
            } else if (task1.getStartTime().isAfter(task2.getStartTime())) {
                return 1;
            } else {
                return (int) (task1.getId() - (task2.getId()));
            }
        };
        TreeSet<Task> sorted  = new TreeSet<>(comparator);
        sorted.add(epicWithEmptySubTask);
        sorted.add(subTaskNew);
        sorted.add(subTaskDone);
        sorted.add(taskNew);

        Assertions.assertEquals(sorted, tm.getPrioritizedTasks());

    }
}