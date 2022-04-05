package manager;

import api.HTTPTaskManager;
import api.KVTaskClient;
import exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class Managers {

    // на вебинаре нам сказали, что всё же должен быть только метод getDefault, который из спринта в спринт менял внутри себя класс который он вызывает.
    // сперва мы должны были в нём вызвать InMemoryTasksManager
    // затем должны были вызывать в нём FileBackedTasksManager
    // и сейчас должны вызывать HTTPTaskManager
    //поэтому у меня два метода. :D
    // один как нам сказали getDefault, внутри него вызываю HTTPTaskManager
    // и есть второй метод getServerManager() для текущего спринта, на примере getFileBackedManager, который делала
    // в прошлом спринте для FileBackedTasksManager
    public static HTTPTaskManager getDefault(KVTaskClient kvTaskClient) throws IOException, InterruptedException { // использую этот метод для тестов
        // 1. return new InMemoryTasksManager();
        //2.
        /* try {
                    return FileBackedTasksManager.loadFromFile(Paths.get(filePath));
               } catch (ManagerSaveException e) {
                    System.out.println(e.getMessage());
                   return null;
                }*/
        //3.
        return new HTTPTaskManager(kvTaskClient);
    }

    public static TaskManager getFileBackedManager(String filePath) {
        try {
            return FileBackedTasksManager.loadFromFile(Paths.get(filePath));
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static TaskManager getServerManager(KVTaskClient kvTaskClient) throws IOException, InterruptedException {
        return new HTTPTaskManager(kvTaskClient);
    }
}
