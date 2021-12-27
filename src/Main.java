import Manager.TaskManager;
import Task.Epic;
import Task.Status;
import Task.SubTask;
import Task.TaskID;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //проверка методов
        TaskManager taskManager = new TaskManager();
        List<SubTask> description = new ArrayList<>();
        List<SubTask> description2 = new ArrayList<>();

        String nameEpic = "Задача 1";
        description.add(new SubTask("подзадача 1", Status.NEW));
        description.add(new SubTask("подзадача 2", Status.NEW));
        System.out.println(description);
        TaskID id = new TaskID();
        Epic epic = new Epic(id, nameEpic, description, Status.getStatusForEpic(description));
        taskManager.addNewTask(epic); // добавили задачу 1'

        String nameEpic2 = "Задача 2";
     //   description2.add(new SubTask("подзадача 3", Status.NEW));
     //   description2.add(new SubTask("подзадача 4", Status.DONE));
        TaskID id2 = new TaskID();
        epic = new Epic(id2, nameEpic2, description2, Status.getStatusForEpic(description));
        taskManager.addNewTask(epic); // добавили задачу 2

        System.out.println(taskManager.getTasksNames()); // получаем список всех задач
        System.out.println(taskManager.getSubtasksName()); // получаем список всех подзадач

        System.out.println("—————————");
        System.out.println(
        taskManager.getSubtasksByEpicName(nameEpic)); //получение списка подзадач определённого эпика
        System.out.println(taskManager.getTaskById(id));
        System.out.println(taskManager.getTaskById(id2)); //получение задачи по ID

        System.out.println("—————————");
        description2.clear(); // удалили подзадачи у задачи 2
        epic = new Epic(id, "Задача 2 изменилась", description2, Status.getStatusForEpic(description));
        taskManager.updateEpicById(epic); // добавили изменения
        System.out.println(epic); // проверяем что изменения сохранились в задаче

        System.out.println("—————————");
        taskManager.removeTaskById(id); // удалили задачу по ID
        System.out.println(taskManager.getTasksNames()); //проверяем что изменения сохранились
        taskManager.removeAllTask(); // удаляем все задачи
        System.out.println(taskManager.getTasksNames());  //проверяем что изменения сохранились в задаче

    }
}
