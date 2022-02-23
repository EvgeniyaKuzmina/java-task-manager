import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;


public class Main {
    public static void main(String[] args) {
        // проверка методов

        //TaskManager tasksManager = Managers.getDefault();
        TaskManager tasksManager  = Managers.getFileBackedManager("recources/recources");


       // создаём задачи. Заполняем файл csv одновременно
        // 1. файл csv пустой, добавляем задачи
        Task task = tasksManager.createTask("Задача 15", "описание задачи", Status.NEW);
        tasksManager.addTask(task); // добавили задачу

       task = tasksManager.createTask("Задача 526", "описание задачи", Status.IN_PROGRESS);
        tasksManager.addTask(task); // добавили задачу

        task = tasksManager.createTask("Задача 20", "описание задачи", Status.NEW);
        tasksManager.addTask(task); // добавили задачу

        Epic epic = tasksManager.createEpic("Эпик 15", "описание задачи");
        tasksManager.addTask(epic); // добавили задачу

        SubTask subTask = tasksManager.createSubTask(4L, "подзадача 1", "описание подзадачи 1", Status.NEW);
        tasksManager.addTask(subTask); // добавили задачу

        subTask = tasksManager.createSubTask(4L, "подзадача 2", "описание подзадачи 2",Status.NEW);
        tasksManager.addTask(subTask); // добавили подзадачу

        tasksManager.updateAnyTask(new Task (3L, "Задача 20", "новое описание задачи", Status.IN_PROGRESS)); // обновление таски 3

        subTask = tasksManager.createSubTask(4L, "подзадача 3", "описание подзадачи 3",Status.NEW);
        tasksManager.addTask(subTask); // добавили подзадачу

        System.out.println(tasksManager.getTasksList());
        System.out.println(tasksManager.getEpicsList());
        System.out.println(tasksManager.getSubTasksList());


        // задачи есть в файл csv, проверяем как они восстанавливаются из файла в системе
       // 2. закомментировать код с 19 по 42 строку, раскомментировать код ниже с 51 по 63 строку

       tasksManager.getTaskById(1L); // получаем задачи из восстановленных файлов, история просмотров должна загрузиться в файл csv
        tasksManager.getTaskById(4L);
        tasksManager.getTaskById(5L);

        //удаляем эпик, и проверяем что данные в csv сохранены корректно

        tasksManager.getTaskById(3L);
        //tasksManager.removeById(3L);
        //tasksManager.removeById(4L);
        task = tasksManager.createTask("Задача 526", "описание задачи", Status.IN_PROGRESS);
        tasksManager.addTask(task); // добавили задачу которая уже есть, она не должна добавить в систему

        System.out.println("---");
        System.out.println(tasksManager.history());


    }

}
