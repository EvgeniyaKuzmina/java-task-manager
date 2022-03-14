package manager;

import exceptions.ManagerSaveException;

import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTasksManager();
    }

    public static TaskManager getFileBackedManager(String filePath) {
        try {
            return FileBackedTasksManager.loadFromFile(Paths.get(filePath));
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
            return null;
        }


    }
}
