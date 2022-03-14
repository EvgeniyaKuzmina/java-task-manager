package manager;

import exceptions.ManagerSaveException;
import history.HistoryManager;
import org.junit.jupiter.api.function.Executable;
import task.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTasksManager {
    private final String filePath;

    public FileBackedTasksManager(String filePath) {
        this.filePath = filePath;
    }

    // получает список id просмотренных задач из строки
    private static List<Long> fromString(String value) {
        List<Long> id = new ArrayList<>();
        String[] idList = value.split(",");
        for (String next : idList) {
            id.add(Long.parseLong(next));
        }
        return id;
    }

    // из менеджера истории получает историю просмотров и сохраняет id просмотренных задач в строку
    private static String toString(HistoryManager manager) {
        StringBuilder result = new StringBuilder();
        List<Task> historyList = manager.getHistoryList();
        if (!historyList.isEmpty()) {
            for (Task task : historyList) {
                result.append(String.format("%d,", task.getId()));
            }
        }
        return result.toString();
    }

    // загружает в систему все задачи/эпики/подзадачи и историю просмотров и создаёт объект TaskManager
    public static FileBackedTasksManager loadFromFile(Path file) {
        FileBackedTasksManager fileBackTM = new FileBackedTasksManager(file.toString());
        try {
            String[] tasksLine = Files.readString(file, StandardCharsets.UTF_8).split("\n");
            if (tasksLine.length <= 1) {
                return fileBackTM;
            }
            for (int i = 1; i < tasksLine.length; i++) { // через цикл восстанавливаем все задачи, таски и подзадачи из файла csv,
                // начинаем числ с 1, а не с 0, чтобы не учитывать шапку таблицы id,type,name,status,description,startTime,duration,epic
                String[] words = tasksLine[i].split(",");
                if (tasksLine[i].isBlank()) { // прекращаем цикл, если дошли до пустой строки, которая отделяет все задачи от истории просмотра
                    break;
                }
                TasksType taskType = TasksType.valueOf(words[1]);
                String id = words[0];
                switch (taskType) {
                    case TASK: // проверяем, если в csv строка хранит TASK, сохраняем задачу в соответствующую хешмапу
                        Task task = Task.fromString(tasksLine[i]);
                        setTasks(task.getId(), task);
                        break;
                    case EPIC: // проверяем, если в csv строка хранит EPIC, сохраняем задачу в соответствующую хешмапу
                        List<SubTask> subtasks = new ArrayList<>();
                        for (String nextSubTask : tasksLine) {
                            if (nextSubTask.isBlank()) { // прекращаем цикл, если дошли до пустой строки,
                                // которая отделяет все задачи от истории просмотра
                                break;
                            }
                            String subTaskType = nextSubTask.split(",")[1];
                            if (subTaskType.equals(TasksType.SUBTASK.getTaskType()) && id.equals(
                                    nextSubTask.split(",")[7])) { // проверяем, если в csv строка хранит SUBTASK и
                                // epicID равен id текущего эпика,
                                SubTask subtask = SubTask.fromString(nextSubTask);
                                subtasks.add(subtask);
                            }
                        }
                        Epic epic = Epic.fromString(tasksLine[i], subtasks);
                        setEpics(epic.getId(), epic);
                        break;
                    case SUBTASK: // проверяем, если в csv строка хранит SUBTASK, сохраняем задачу в соответствующую хешмапу
                        SubTask subtask = SubTask.fromString(tasksLine[i]);
                        setSubtasks(subtask.getId(), subtask);
                        break;
                }
            }
            if (tasksLine[tasksLine.length - 2].isBlank()) { // проверяем что предпоследняя строка — это пустая строка,
                // которая отделяет все задачи от истории просмотра
                List<Long> idTasks = fromString(tasksLine[tasksLine.length - 1]); // получаем список id задач из
                // сохранённой истории csv
                for (Long id : idTasks) {
                    loadHistory(id); // восстанавливаем историю просмотров
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в чтении файла " + file);
        }
        return fileBackTM;
    }

    // восстанавливает историю просмотра по id
    private static void loadHistory(Long id) {
        if (getTasks().containsKey(id)) {
            historyManager.add(getTasks().get(id));
        }
        if (getEpics().containsKey(id)) {
            historyManager.add(getEpics().get(id));
        }
        if (getSubtasks().containsKey(id)) {
            historyManager.add(getSubtasks().get(id));
        }
    }

    // сохраняет все задачи, такси и подзадачи и историю просмотров в файл csv
    public Executable save() {
        String line;
        String firstLine = "id,type,name,status,description,startTime,duration,epic\n";
        try (Writer fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            fileWriter.write(firstLine);
            for (Task task : getTasksList()) {
                line = task.toString();
                fileWriter.write(line);
            }
            for (Epic epic : getEpicsList()) {
                line = epic.toString();
                fileWriter.write(line);
            }
            for (SubTask subTask : getSubTasksList()) {
                line = subTask.toString();
                fileWriter.write(line);
            }
            if (historyManager.getHistoryList() != null) {
                fileWriter.write("\n");
                fileWriter.write(toString(historyManager));
            }
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("В указанной директории файла нет или процесс не может получить доступ к файлу, " +
                            "так как этот файл занят другим процессом");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в сохранении файла по указанному пути" + filePath);
        }
        return null;
    }

    //2.5 Добавление новой задачи, эпика, подзадачи. Сохранение задачи в файл.
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    // 2.3 Получение списка всех подзадач определённого эпика.
    @Override
    public List<SubTask> getSubTaskByEpicId(Long id) {
        List<SubTask> subTasks = super.getSubTaskByEpicId(id);
        save();
        return subTasks;
    }

    // 2.4 Получение задачи любого типа по идентификатору.
    @Override
    public Task getTaskById(Long id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    //2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра. Сохранение  в файл.
    @Override
    public void updateAnyTask(Task task) {
        super.updateAnyTask(task);
        save();
    }

    // Удаление всех ранее добавленных задач
    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    // Удаление ранее добавленных задач по ID
    @Override
    public String removeById(Long id) {
        String result = super.removeById(id);
        save();
        return result;
    }


}
