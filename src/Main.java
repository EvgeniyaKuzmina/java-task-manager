import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //проверка методов
        ManagerOfTasks managerOfTasks = new ManagerOfTasks();
        List<SubTask> description = new ArrayList<>();
        List<SubTask> description2 = new ArrayList<>();

        String nameEpic = "Задача 1";
        description.add(new SubTask("подзадача 1", Status.NEW));
        description.add(new SubTask("подзадача 2", Status.NEW));
        TaskID id = new TaskID();
        Task task = new Task(id, nameEpic, description);
        managerOfTasks.addNewTask(task); // добавили задачу 1

        String nameEpic2 = "Задача 2";
        description2.add(new SubTask("подзадача 3", Status.NEW));
        description2.add(new SubTask("подзадача 4", Status.DONE));
        id = new TaskID();
        task = new Task(id, nameEpic2, description2);
        managerOfTasks.addNewTask(task); // добавили задачу 2

        System.out.println(managerOfTasks.getListOfTask()); // получаем список всех задач
        System.out.println(managerOfTasks.getListOfSubTask()); // получаем список всех подзадач

        System.out.println("—————————");
        System.out.println(
                managerOfTasks.getListOfSubTaskByEpic(nameEpic)); //получение списка подзадач определённого эпика
        System.out.println(managerOfTasks.getTaskById(id)); //получение задачи по ID

        System.out.println("—————————");
        description2.clear(); // удалили подзадачи у задачи 2
        task = new Task(id, "Задача 2 изменилась", description2);
        managerOfTasks.updateTaskById(task); // добавили изменения
        System.out.println(task); // проверяем что изменения сохранились в задаче

        System.out.println("—————————");
        managerOfTasks.removeTaskById(id); // удалили задачу по ID
        System.out.println(managerOfTasks.getListOfTask()); //проверяем что изменения сохранились
        managerOfTasks.removeAllTask(); // удаляем все задачи
        System.out.println(managerOfTasks.getListOfTask());  //проверяем что изменения сохранились в задаче

    }
}
