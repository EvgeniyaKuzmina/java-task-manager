package manager;


import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault(String filePath) {
        TaskManager tm = new FileBackedTasksManager(filePath);

        return tm;
    }
}
