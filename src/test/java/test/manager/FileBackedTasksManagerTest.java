package test.manager;

import exceptions.ManagerSaveException;
import manager.FileBackedTasksManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest {
    static Task taskNew;
    static Task taskInProgress;
    static Epic epicWithEmptySubTask;
    static SubTask subTaskNew;
    static SubTask subTaskDone;
    static Epic epicInProgress;
    static Epic epicDone;
    static Epic epicNew;
    FileBackedTasksManager tm;

    // у меня были все зелённые тесты кроме одного shouldThrowExceptionIfPathToFileWrong,
    // потому что я никак не могла понять что я делаю не так.
    //У вас два теста упало, потому что файлы, которые я подготовила для этих тестов не загрузились в гит.
    // Не знаю почему так вышло. Сейчас я их подгрузила, и всё должно быть в порядке.))
    // Ещё в гит подгрузилось почему-то так что, класс InMemoryHistoryManager оказался в отдельном пакете history, не внутри
    // main → java. Как так вышло — осталось для меня загадкой.

    // с maven мы ещё не работали, не изучали. На вебинаре наставник просто рассказал о нём, я решила попробовать.
    //Но что-то пошло не так и в файле pom были ошибки. В итоге я запускала тесты не через maven.
    // я исправила как вы сказали, спасибо. Сейчас ошибок нет, maven запускается, но в результате отчёт о запуске выглядит так:
    // Results :
    //
    //Tests run: 0, Failures: 0, Errors: 0, Skipped: 0
    //То есть ни один тест не был запущен. В результате я оставила так как есть)) кажется далее на курсе будет тема про maven
    @BeforeEach
    void beforeEach() {
        taskNew = new Task(1L, "Задача 1", "описание задачи", Status.NEW, 2023, 3, 25, 5);
        taskInProgress = new Task(2L, "Задача 2", "описание задачи", Status.IN_PROGRESS, 2023, 3, 25, 5);
        subTaskNew = new SubTask(1L, 3L, "Подзадача 1", "описание подзадачи", Status.NEW, 2023, 3, 25, 15);
        subTaskDone = new SubTask(1L, 4L, "Подзадача 2", "описание подзадачи", Status.DONE, 2023, 3, 25, 7);
        epicWithEmptySubTask = new Epic(1L, "Эпик 1", "описание задачи", new ArrayList<>(), Status.NEW, 2023, 3, 25,
                                        15);
        epicInProgress = new Epic(1L, "Задача 15", "описание задачи", List.of(subTaskNew, subTaskDone),
                                  Status.IN_PROGRESS, 2023, 3, 25, 15);
        epicDone = new Epic(6L, "Задача 15", "описание задачи", List.of(subTaskDone), Status.DONE, 2023, 3, 25, 15);
        epicNew = new Epic(7L, "Задача 25", "описание задачи", List.of(subTaskNew), Status.DONE, 2023, 3, 25, 15);


    }

    // проверяем, если в файле нет данных, то возвращается объект класс FileBackedTasksManager
    @Test
    void shouldReturnFileBackedTasksManagerIfFileEmpty() {
        assertTrue(FileBackedTasksManager.loadFromFile(
                Paths.get("recources/forTestIfFileEmpty")) instanceof FileBackedTasksManager);
        try {
            assertEquals("",
                         Files.readString(Paths.get("recources/forTestIfFileEmpty"), StandardCharsets.UTF_8).trim());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в чтении файла " + "recources/forTestIfFileEmpty");
        }

    }

    // проверяем что из файла корректно загружаются в систему задачи, подзадачи, эпики и история
    @Test
    void shouldReturnFileBackedTasksManagerWithRestoredTasksAndHistory() {
        SubTask subtask = new SubTask(4L, 5L, "подзадача 1", "описание подзадачи 1",
                                      Status.NEW, 2023, 3, 25, 15);
        SubTask subtask1 = new SubTask(4L, 6L, "подзадача 2", "описание подзадачи 2",
                                       Status.NEW, 2023, 3, 25, 15);
        SubTask subtask2 = new SubTask(4L, 7L, "подзадача 3", "описание подзадачи 3",
                                       Status.NEW, 2023, 3, 25, 15);

        Task task = new Task(1L, "Задача 15", "описание задачи",
                             Status.NEW, 2023, 3, 25, 15);
        Task task1 = new Task(2L, "Задача 526", "описание задачи",
                              Status.IN_PROGRESS, 2023, 3, 25, 15);
        Task task2 = new Task(3L, "Задача 20", "новое описание задачи",
                              Status.IN_PROGRESS, 2023, 3, 25, 15);

        Epic epic = new Epic(4L, "Эпик 15", "описание задачи", List.of(subtask, subtask1, subtask2),
                             Status.NEW, 2023, 3, 25, 15);

        tm = FileBackedTasksManager.loadFromFile(Paths.get("recources/forTestIfFileContainsEpicTaskSubtask"));
        assertEquals(List.of(task, task1, task2), tm.getTasksList());
        assertEquals(List.of(subtask, subtask1, subtask2), tm.getSubTasksList());
        assertEquals(List.of(epic), tm.getEpicsList());
        assertEquals(List.of(task, epic, subtask, task2), tm.history());
    }

    // проверка, если путь к файлу указан неверно
    @Test
    void shouldThrowExceptionIfPathToFileWrong() {
        tm = new FileBackedTasksManager("recources/");
        Throwable ex = Assertions.assertThrows(
                ManagerSaveException.class,
                () -> tm.getSaveForTest()
        );

        assertEquals("В указанной директории файла нет или процесс не может получить доступ к файлу," +
                             "так как этот файл занят другим процессом", ex.getMessage());
    }

    // проверяем, если в файле данные есть, то они восстанавливаются в соответствующую хешмапу класса InMemoryTasksManager,
    // эпики без подзадач
    @Test
    void shouldReturnFileBackedTasksManagerWithRestoredEpicsWithoutSubTask() {
        Task task = new Task(1L, "Задача 15", "описание задачи", Status.NEW, 2023, 3, 25, 15);
        Task task1 = new Task(2L, "Задача 526", "описание задачи", Status.IN_PROGRESS, 2023, 3, 25, 15);
        Task task2 = new Task(3L, "Задача 20", "новое описание задачи", Status.IN_PROGRESS, 2023, 3, 25, 15);

        Epic epic = new Epic(4L, "Эпик 15", "описание задачи", List.of(), Status.NEW, 2023, 3, 25, 15);

        tm = FileBackedTasksManager.loadFromFile(Paths.get("recources/forTestEpicWithoutSubTask"));
        assertEquals(List.of(task, task1, task2), tm.getTasksList());
        assertEquals(List.of(), tm.getSubTasksList());
        assertEquals(List.of(epic), tm.getEpicsList());
    }

    // проверка, что данные сохранились в файл csv после добавления новых задач
    @Test
    void shouldSaveToFileSCV() throws IOException {
        tm = new FileBackedTasksManager("recources/forTest");
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(taskInProgress);
        //tm.save();
        try {
            assertEquals("id,type,name,status,description,startTime,duration,epic\n" +
                                 "2,TASK,Задача 2,IN_PROGRESS,описание задачи,25.03.2023,5\n" +
                                 "1,EPIC,Эпик 1,NEW,описание задачи,25.03.2023,15\n" +
                                 "3,SUBTASK,Подзадача 1,NEW,описание подзадачи,25.03.2023,15,1",
                         Files.readString(Paths.get("recources/forTest"), StandardCharsets.UTF_8).trim());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в чтении файла " + "recources/forTest");
        }
    }

    // проверка, что после удаления задачи, она удаляется из файла
    @Test
    void shouldSaveToFileSCVWithoutRemovedTask() {
        tm = new FileBackedTasksManager("recources/forTest");
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(taskInProgress);
        tm.removeById(2L);
       // tm.save();
        try {
            assertEquals("id,type,name,status,description,startTime,duration,epic\n" +
                                 "1,EPIC,Эпик 1,NEW,описание задачи,25.03.2023,15\n" +
                                 "3,SUBTASK,Подзадача 1,NEW,описание подзадачи,25.03.2023,15,1",
                         Files.readString(Paths.get("recources/forTest"), StandardCharsets.UTF_8).trim());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в чтении файла " + "recources/forTest");
        }
    }

    // проверка, что после удаления эпика, он удаляется из файла вместе с подзадачами
    @Test
    void shouldSaveToFileSCVWithoutRemovedEpic() {
        tm = new FileBackedTasksManager("recources/forTest");
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(taskInProgress);
        tm.removeById(1L);
       // tm.save();
        try {
            assertEquals("id,type,name,status,description,startTime,duration,epic\n" +
                                 "2,TASK,Задача 2,IN_PROGRESS,описание задачи,25.03.2023,5",
                         Files.readString(Paths.get("recources/forTest"), StandardCharsets.UTF_8).trim());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в чтении файла " + "recources/forTest");
        }
    }

    // проверка, что после изменения задачи, изменения сохраняются в файл
    @Test
    void shouldChangeTaskToCSV() {
        tm = new FileBackedTasksManager("recources/forTest");
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(taskInProgress);
        Epic epic = new Epic(1L, "Эпик 1", "описание эпика после обновления", List.of(subTaskNew),
                             Status.DONE, 2023, 3, 25, 0);
        tm.updateAnyTask(epic);
       // tm.save();
        try {
            assertEquals("id,type,name,status,description,startTime,duration,epic\n" +
                                 "2,TASK,Задача 2,IN_PROGRESS,описание задачи,25.03.2023,5\n" +
                                 "1,EPIC,Эпик 1,NEW,описание эпика после обновления,25.03.2023,15\n" +
                                 "3,SUBTASK,Подзадача 1,NEW,описание подзадачи,25.03.2023,15,1",
                         Files.readString(Paths.get("recources/forTest"), StandardCharsets.UTF_8).trim());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в чтении файла " + "recources/forTest");
        }
    }

    // проверка, что при просмотре задач по id, история добавляется в файл
    @Test
    void shouldAddHistoryToCSV() {
        tm = new FileBackedTasksManager("recources/forTest");
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(taskInProgress);
        tm.getTaskById(1L);
       // tm.save();
        try {
            assertEquals("id,type,name,status,description,startTime,duration,epic\n" +
                                 "2,TASK,Задача 2,IN_PROGRESS,описание задачи,25.03.2023,5\n" +
                                 "1,EPIC,Эпик 1,NEW,описание задачи,25.03.2023,15\n" +
                                 "3,SUBTASK,Подзадача 1,NEW,описание подзадачи,25.03.2023,15,1\n\n1,",
                         Files.readString(Paths.get("recources/forTest"), StandardCharsets.UTF_8).trim());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в чтении файла " + "recources/forTest");
        }
    }

    // проверка, что при просмотре задач по id, и после удаляется задачи по id, задача удаляется из истории просмотра  в файле csv
    @Test
    void shouldAddAndRemoveHistoryFromCSV() {
        tm = new FileBackedTasksManager("recources/forTest");
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(taskInProgress);
        tm.getTaskById(1L);
      //  tm.save();
        tm.removeById(1L);
      //  tm.save();
        try {
            assertEquals("id,type,name,status,description,startTime,duration,epic\n" +
                                 "2,TASK,Задача 2,IN_PROGRESS,описание задачи,25.03.2023,5",
                         Files.readString(Paths.get("recources/forTest"), StandardCharsets.UTF_8).trim());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в чтении файла " + "recources/forTest");
        }
    }

    // проверка, что после удаления всех задач, файл пустой
    @Test
    void shouldRemoveAllTaskFromCSV() {
        tm = new FileBackedTasksManager("recources/forTest");
        tm.addTask(epicWithEmptySubTask);
        tm.addTask(subTaskNew);
        tm.addTask(taskInProgress);
        tm.removeAllTask();
       // tm.save();
        try {
            assertEquals("id,type,name,status,description,startTime,duration,epic",
                         Files.readString(Paths.get("recources/forTest"), StandardCharsets.UTF_8).trim());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в чтении файла " + "recources/forTest");
        }
    }
}