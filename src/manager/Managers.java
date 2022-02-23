package manager;

import exceptions.ManagerSaveException;

import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTasksManager();
    }

    // мне кажется метод getDefault() уже не нужен. Или у меня что-то работает неправильно.
    // Потому что если я вызываю метод getDefault(), создаётся объект класса InMemoryTasksManager(), и в классе main,
    // если я использую объект класса InMemoryTasksManager() никакие сохранения в файле csv не происходят.
    // Что логично, потому что я вызываю именно методы InMemoryTasksManager, а не FileBackedTasksManager.
    // Может быть смысл станет мне понятен позднее, когда добавится ещё новый функционал в приложение.
    // Пока сложно мне понять, смысл того, что метод больше нужен для тестирования.))
    // Пока я вижу смысл создавать сразу объект через getFileBackedManager, чтобы все задачи и история сразу сохранялись в csv

    public static TaskManager getFileBackedManager(String filePath) {
        try {
            return FileBackedTasksManager.loadFromFile(Paths.get(filePath));
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
            return null;
        }


    }
}
