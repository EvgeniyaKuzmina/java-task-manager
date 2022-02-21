package manager;

import exceptions.ManagerSaveException;
import history.HistoryManager;
import task.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTasksManager {
    static String history;
    static List<Long> idTasks;
    String filePath;

    public FileBackedTasksManager(String filePath) {
        this.filePath = filePath;
        loadFromFile(Paths.get(filePath));
    }

    // получает список id просмотренных задач из строки
    static List<Long> fromString(String value) {
        List<Long> id = new ArrayList<>();
        String[] idList = value.split(",");
        for (String next : idList) {
            id.add(Long.parseLong(next));
        }
        return id;
    }

    // из менеджера истории получает историю просмотров и сохраняет id просмотренных задач в строку
    static String toString(HistoryManager manager) {
        StringBuilder result =  new StringBuilder();
        List<Task> historyList = manager.getHistoryList();
        if (!historyList.isEmpty()) {
            for (Task task : historyList) {
                result.append(String.format("%d,", task.getId()));
            }
        }
        return result.toString();
    }

    // загружает в систему все задачи/эпики/подзадачи и историю просмотров
    public void loadFromFile(Path file) {
        try {
            String[] tasksLine = Files.readString(file, StandardCharsets.UTF_8).split("\n");
            if (tasksLine.length <= 1) {
                return;
            }
            for (String nextLine : tasksLine) { // через цикл восстанавливаем все задачи, таски и подзадачи из файла csv
                String[] words = nextLine.split(",");
                if (nextLine.isBlank()) { // прекращаем цикл, если дошли до пустой строки, которая отделяет все задачи от истории просмотра
                    break;
                }
                String taskType = words[1];
                String id = words[0];
                if (taskType.equals(TasksType.TASK.getTaskType())) { // проверяем, если в csv строка хранит TASK,
                                                                    // сохраняем задачу в соответствующую хешмапу
                    Task task = Task.fromString(nextLine);
                    setTasks(task.getId(), task);
                } else if (taskType.equals(TasksType.EPIC.getTaskType())) { // проверяем, если в csv строка хранит EPIC,
                                                                            // сохраняем задачу в соответствующую хешмапу
                    List<SubTask> subtasks = new ArrayList<>();
                    for (String nextSubTask : tasksLine) {
                        if (nextSubTask.isBlank()) { // прекращаем цикл, если дошли до пустой строки,
                                                     // которая отделяет все задачи от истории просмотра
                            break;
                        }
                        String subTaskType = nextSubTask.split(",")[1];
                        if (subTaskType.equals(TasksType.SUBTASK.getTaskType()) && id.equals(
                                nextSubTask.split(",")[5])) { // проверяем, если в csv строка хранит SUBTASK и
                                                                            // epicID равен id текущего эпика,
                            SubTask subtask = SubTask.fromString(nextSubTask);
                            subtasks.add(subtask);
                        }
                    }
                    Epic epic = Epic.fromString(nextLine, subtasks);
                    setEpics(epic.getId(), epic);
                } else if (taskType.equals(TasksType.SUBTASK.getTaskType())) { // проверяем, если в csv строка хранит
                                                                // SUBTASK, сохраняем задачу в соответствующую хешмапу
                    SubTask subtask = SubTask.fromString(nextLine);
                    setSubtasks(subtask.getId(), subtask);
                }
            }
            if (tasksLine[tasksLine.length - 2].isBlank()) { // проверяем что предпоследняя строка — это пустая строка,
                                                            // которая отделяет все задачи от истории просмотра
                idTasks = fromString(tasksLine[tasksLine.length - 1]); // получаем список id задач из
                                                                         // сохранённой истории csv
                for (Long id : idTasks) {
                    loadHistory(id); // восстанавливаем историю просмотров
                }
            }
        } catch (IOException e) {
            System.out.printf(
                    "Невозможно прочитать файл с отчётом. Возможно, файл не находится в нужной директории или путь к файлу указан с ошибкой: %s\n",
                    file.getFileName());
        }
    }

    // сохраняет все задачи, такси и подзадачи и историю просмотров в файл csv
    private void save() throws ManagerSaveException { // по ТЗ просят что метод save должен выбрасывать исключение ManagerSaveException, но я не очень понимаю смысл этого исключения
        String line;
        String firstLine = "id,type,name,status,description,epic\n";
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
            fileWriter.write("\n");
            if (history != null) {
                fileWriter.write(history);
            }

        } catch (FileNotFoundException e) {
            System.out.println(
                    "В указанной директории файла нет или процесс не может получить доступ к файлу, так как этот файл занят другим процессом");
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ManagerSaveException("Ошибка в сохранении файла");
    }

    //2.5 Добавление новой задачи, эпика, подзадачи. Сохранение задачи в файл.
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    // восстанавливает историю просмотра по id
    private void loadHistory(Long id) {
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

    // 2.3 Получение списка всех подзадач определённого эпика.
    @Override
    public List<SubTask> getSubTaskByEpicId(Long id) {
        List<SubTask> subTasks = super.getSubTaskByEpicId(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
        return subTasks;
    }

    // 2.1 — Получение списка всех задач
    @Override
    public List<Epic> getEpicsList() {
        return super.getEpicsList();
    }

    // 2.1 — Получение списка всех эпиков
    @Override
    public List<Task> getTasksList() {
        return super.getTasksList();
    }

    // 2.1 — Получение списка всех подзадач
    @Override
    public List<SubTask> getSubTasksList() {
        return super.getSubTasksList();
    }

    // 2.4 Получение задачи любого типа по идентификатору.
    @Override
    public Task getTaskById(Long id) {
        Task task = super.getTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
        return task;
    }

    //2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра. Сохранение  в файл.
    @Override
    public void updateAnyTask(Task task) {
        super.updateAnyTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    // Удаление всех ранее добавленных задач
    @Override
    public void removeAllTask() {
        super.removeAllTask();
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    // Удаление ранее добавленных задач по ID
    @Override
    public void removeById(Long id) {
        super.removeById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    //создание эпика
    @Override
    public Epic createEpic(String nameEpic, String description) {
        return super.createEpic(nameEpic, description);
    }

    //создание эпика
    @Override
    public Task createTask(String nameTask, String description, Status status) {
        return super.createTask(nameTask, description, status);
    }

    //создание эпика
    @Override
    public SubTask createSubTask(long epicId, String nameTask, String description, Status status) {
        return super.createSubTask(epicId, nameTask, description, status);
    }

    //показывает историю просмотра задач и подзадач
    @Override
    public List<Task> history() {
        return super.history();

    }

}
