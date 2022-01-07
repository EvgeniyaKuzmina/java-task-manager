import managementTask.InMemoryTasksManager;
import managementTask.Managers;
import task.Status;
import task.SubTask;
import task.Task;
import task.TaskID;

public class Main {
    public static void main(String[] args) {
        // проверка методов
        InMemoryTasksManager inMemoryTasksManager = Managers.getDefault();

        TaskID taskId = new TaskID();
        long id = taskId.getId();

        inMemoryTasksManager.addNewSubtasks(new SubTask(id, "подзадача 1", "описание подзадачи 1", Status.NEW));
        inMemoryTasksManager.addNewSubtasks(new SubTask(id, "подзадача 2", "описание подзадачи 2", Status.NEW));
        inMemoryTasksManager.addNewTaskEpic(
                inMemoryTasksManager.createEpic(id, "Эпик 1", "описание эпика 1")); // добавили эпик 1

        taskId = new TaskID();
        long id2 = taskId.getId();

        inMemoryTasksManager.addNewSubtasks(new SubTask(id2, "подзадача 3", "описание подзадачи 3", Status.NEW));
        inMemoryTasksManager.addNewSubtasks(
                new SubTask(id2, "подзадача 4", "описание подзадачи 4", Status.IN_PROGRESS));
        inMemoryTasksManager.addNewTaskEpic(
                inMemoryTasksManager.createEpic(id2, "Эпик 2", "описание эпика 2")); // добавили эпик 2

        taskId = new TaskID();
        long id3 = taskId.getId();
        inMemoryTasksManager.addNewTaskEpic(
                new Task(id3, "Задача 3", "описание задачи 3", Status.NEW)); // добавили задачу 3

        System.out.println("получение списка эпиков:");
        System.out.println(inMemoryTasksManager.getEpics()); // получение списка эпиков

        System.out.println("\nполучение списка задач:");
        System.out.println(inMemoryTasksManager.getTasks());// получение списка задач

        System.out.println("\nполучение списка подзадач:");
        System.out.println(inMemoryTasksManager.getSubTasks());

        System.out.println("\nПолучение списка всех подзадач определённого эпика:");
        System.out.println(inMemoryTasksManager.getSubTaskById(id3));

        System.out.println("\nПолучение задачи любого типа по идентификатору:");
        System.out.println(inMemoryTasksManager.getTaskOrEpicById(id3));
        System.out.println(inMemoryTasksManager.getTaskOrEpicById(id2));

        System.out.println("\nОбновление задачи любого типа по идентификатору:");
        inMemoryTasksManager.addNewSubtasks(new SubTask(id2, "добавили подзадачу 5", "описание подзадачи 5",
                                                        Status.IN_PROGRESS)); // добавили подзадачу
        inMemoryTasksManager.updateTaskEpic(inMemoryTasksManager.createEpic(id2, "Эпик 2",
                                                                            "Новое описание эпика 2")); // обновили эпик 2, в нём появилась новая подзадача 5
        System.out.println(inMemoryTasksManager.getEpics());

        System.out.println("\nУдаление ранее добавленных задач по ID");
        inMemoryTasksManager.removeTaskById(id); //удалили эпик 1
        System.out.println(inMemoryTasksManager.getEpics());

        System.out.println("\nПросмотр истории");
        System.out.println(inMemoryTasksManager.history());

        System.out.println("\nУдаление всех ранее добавленных задач");
        inMemoryTasksManager.removeAllTask();

        System.out.println("\nПросмотр истории");
        System.out.println(inMemoryTasksManager.history());


    }

}
