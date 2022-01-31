import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

public class Main {
    public static void main(String[] args) {
        // проверка методов
        TaskManager tasksManager = Managers.getDefault();

        Epic epic = tasksManager.createEpic("Эпик 1", "описание эпика 1");
        tasksManager.addTask(epic); // добавили эпик 1


        System.out.println("проверяем что эпик добавился");
        System.out.println(tasksManager.getEpics());
        long epicId = 1;

        SubTask subTask = tasksManager.createSubTask(epicId, "подзадача 1", "описание подзадачи 1",
                                                     Status.NEW); // добавили подзадачу 1 в эпик 1
        tasksManager.addTask(subTask);
        subTask = tasksManager.createSubTask(epicId, "подзадача 2", "описание подзадачи 2",
                                             Status.NEW); //добавили подзадачу 2 в эпик 1
        tasksManager.addTask(subTask);

        epic = tasksManager.createEpic("Эпик 2", "описание эпика 2");
        tasksManager.addTask(epic); // добавили эпик 2

        System.out.println("\nпроверяем что подзадачи относятся к эпику 1");
        System.out.println(tasksManager.getSubTaskByEpicId(epicId)); // получаем подзадачи определённого эпика

        System.out.println("проверяем что добавился второй эпик");
        System.out.println(tasksManager.getEpics());


        System.out.println("\nПросмотр истории");
        System.out.println(tasksManager.history());

        long epicId2 = 4;

        subTask = tasksManager.createSubTask(epicId2, "подзадача 3", "описание подзадачи 3", Status.NEW);
        tasksManager.addTask(subTask); // добавили подзадачу во второй эпик
        subTask = tasksManager.createSubTask(epicId2, "подзадача 4", "описание подзадачи 4", Status.NEW);
        tasksManager.addTask(subTask); // добавили подзадачу во второй эпик

        System.out.println("проверяем эпики с подзадачами ");
        System.out.println(tasksManager.getEpics());

        System.out.println("\nПросмотр истории");
        System.out.println(tasksManager.history());

        long id3 = 6;
        Task task = tasksManager.createTask("Задача 3", "описание задачи 3", Status.NEW);
        tasksManager.addTask(task); // добавили задачу 3
        tasksManager.addTask(tasksManager.createTask("Задача 4", "описание задачи 4", Status.NEW)); // добавили задачу 4

        System.out.println("\nполучение списка задач:");
        System.out.println(tasksManager.getTasks());// получение списка задач

        System.out.println("\nполучение списка подзадач:");
        System.out.println(tasksManager.getSubTasks());

        System.out.println("\nПолучение списка всех подзадач определённого эпика:");
        System.out.println(tasksManager.getSubTaskByEpicId(
                id3)); // id3 — это ID Таски, а не эпика, поэтому списка подзадач быть не должно

        System.out.println("\nПолучение задачи любого типа по идентификатору:");
        System.out.println(tasksManager.getTaskById(4L)); // получаем эпик с ID 4
        System.out.println(tasksManager.getTaskById(6L)); // получаем сабтаксу с ID 6

        System.out.println("\nПросмотр истории");
        System.out.println(tasksManager.history());
        System.out.println(tasksManager.getTaskById(7L)); // получаем таску с ID 7

        System.out.println("\nПросмотр истории");
        System.out.println(tasksManager.history());

        System.out.println("\nПолучение задачи любого типа по идентификатору:");
        System.out.println(tasksManager.getTaskById(id3));
        System.out.println(tasksManager.getTaskById(epicId2));


        System.out.println("\nОбновление задачи любого типа по идентификатору:");
        subTask = new SubTask(epicId, 3L, "обновили подзадачу  2", "новое описание подзадачи 2", Status.IN_PROGRESS);
        tasksManager.updateAnyTask(subTask); // обновили подзадачу 2 эпика 1;
        System.out.println(tasksManager.getSubTasks());

        task = new Task(7L, "обновили задачу с ИД 7", "новое описание задачи", Status.IN_PROGRESS);
        tasksManager.updateAnyTask(task);
        System.out.println(tasksManager.getTasks());

        epic = new Epic(4L, "Новое название эпика 2", "описание эпика 2", tasksManager.getSubTaskByEpicId(4L),
                        Status.getStatusForEpic(tasksManager.getSubTaskByEpicId(4L)));
        tasksManager.updateAnyTask(epic);
        System.out.println(tasksManager.getEpics());

        System.out.println("проверяем историю просмотра");
        System.out.println(tasksManager.history());


        System.out.println("\nУдаление ранее добавленных задач по ID");

        //tasksManager.removeById(epicId); //удалили эпик 1

        tasksManager.getSubTaskByEpicId(
                epicId); // проверка, что эпика с epicId нет, должен быть выведен ответ: Эпика с таким Id нет

        System.out.println("\nпроверяем историю просмотра, эпика 1 не должно быть в истории");
        System.out.println(tasksManager.history());


        tasksManager.getSubTaskByEpicId(epicId2);

        System.out.println("\nУдаление всех ранее добавленных задач");
        tasksManager.removeAllTask();
        System.out.println(tasksManager.getEpics()); // проверка что ничего нет


        System.out.println("\nПросмотр истории");
        System.out.println(tasksManager.history());

        System.out.println("\n---------------------");
        System.out.println(tasksManager.getSubTaskByEpicId(4L));

        System.out.println("\n---------------------");

        epic = tasksManager.createEpic("Эпик 1", "описание эпика 1");
        tasksManager.addTask(epic); // добавили эпик 1
        System.out.println(tasksManager.getEpics());

        subTask = tasksManager.createSubTask(9L, "подзадача 1", "описание подзадачи 1",
                                             Status.NEW); // добавили подзадачу 1 в эпик 1
        tasksManager.addTask(subTask);

        subTask = tasksManager.createSubTask(9L, "подзадача 2", "описание подзадачи 2",
                                             Status.NEW); //добавили подзадачу 2 в эпик 1
        tasksManager.addTask(subTask);

        subTask = tasksManager.createSubTask(9L, "подзадача 3", "описание подзадачи 3",
                                             Status.NEW); //добавили подзадачу 2 в эпик 1
        tasksManager.addTask(subTask);

        System.out.println("\nпроверяем что подзадачи относятся к эпику 1");
        System.out.println(tasksManager.getSubTaskByEpicId(9L)); // получаем подзадачи определённого эпика


        System.out.println("\nПросмотр истории");
        System.out.println(tasksManager.history());

    }

}
