import manager.Managers;
import manager.TaskManager;
import task.Status;
import task.SubTask;
import task.Task;
import task.TaskID;

public class Main {
    public static void main(String[] args) {
        // проверка методов
        TaskManager inMemoryTasksManager = Managers.getDefault();

        TaskID taskId = new TaskID();
        long epicId = taskId.getId();

        inMemoryTasksManager.addNewTaskEpicSubTask(
                new SubTask(epicId, taskId.getId(), "подзадача 1", "описание подзадачи 1", Status.NEW));
        inMemoryTasksManager.addNewTaskEpicSubTask(
                new SubTask(epicId, taskId.getId(), "подзадача 2", "описание подзадачи 2", Status.NEW));
        inMemoryTasksManager.addNewTaskEpicSubTask(
                inMemoryTasksManager.createEpic(epicId, "Эпик 1", "описание эпика 1")); // добавили эпик 1

        long epicId2 = taskId.getId();

        inMemoryTasksManager.addNewTaskEpicSubTask(
                new SubTask(epicId2, taskId.getId(), "подзадача 3", "описание подзадачи 3", Status.NEW));
        inMemoryTasksManager.addNewTaskEpicSubTask(
                new SubTask(epicId2, taskId.getId(), "подзадача 4", "описание подзадачи 4", Status.IN_PROGRESS));
        inMemoryTasksManager.addNewTaskEpicSubTask(
                inMemoryTasksManager.createEpic(epicId2, "Эпик 2", "описание эпика 2")); // добавили эпик 2

        long id3 = taskId.getId();
        inMemoryTasksManager.addNewTaskEpicSubTask(
                new Task(id3, "Задача 3", "описание задачи 3", Status.NEW));// добавили задачу 3
        inMemoryTasksManager.addNewTaskEpicSubTask(new Task (taskId.getId(), "Задача 4", "описание задачи 4", Status.NEW)); // добавили задачу 4

        System.out.println("получение списка эпиков:");
        System.out.println(inMemoryTasksManager.getEpics()); // получение списка эпиков

        System.out.println("\nполучение списка задач:");
        System.out.println(inMemoryTasksManager.getTasks());// получение списка задач

        System.out.println("\nполучение списка подзадач:");
        System.out.println(inMemoryTasksManager.getSubTasks());

        System.out.println("\nПолучение списка всех подзадач определённого эпика:");
        System.out.println(inMemoryTasksManager.getSubTaskByEpicId(id3));

        System.out.println("\nПолучение задачи любого типа по идентификатору:");
        System.out.println(inMemoryTasksManager.getTaskEpicSubtaskById(id3));
        System.out.println(inMemoryTasksManager.getTaskEpicSubtaskById(epicId2));


        System.out.println("\nОбновление задачи любого типа по идентификатору:");
        inMemoryTasksManager.addNewTaskEpicSubTask(
                new SubTask(epicId2, taskId.getId(), "добавили подзадачу 5", "описание подзадачи 5",
                            Status.IN_PROGRESS)); // добавили подзадачу
        inMemoryTasksManager.updateTaskEpicSubtask(
                new SubTask(epicId, taskId.getId(), "обновили подзадачу  2", "новое описание подзадачи 2",
                            Status.IN_PROGRESS)); // обновили подзадачу 2 эпика 1;
        inMemoryTasksManager.updateTaskEpicSubtask(inMemoryTasksManager.createEpic(epicId2, "Эпик 2",
                                                                                   "Новое описание эпика 2")); // обновили эпик 2, в нём появилась новая подзадача 5
        System.out.println(inMemoryTasksManager.getEpics());

        System.out.println("\nУдаление ранее добавленных задач по ID");
        inMemoryTasksManager.removeById(epicId); //удалили эпик 1
        System.out.println(inMemoryTasksManager.getEpics());
        inMemoryTasksManager.getSubTaskByEpicId(epicId);

        System.out.println("\nПросмотр истории");
        System.out.println(inMemoryTasksManager.history());

        inMemoryTasksManager.getSubTaskByEpicId(epicId2);

        System.out.println("\nУдаление всех ранее добавленных задач");
        inMemoryTasksManager.removeAllTask();


        System.out.println("\nПросмотр истории");
        System.out.println(inMemoryTasksManager.history());




    }

}
