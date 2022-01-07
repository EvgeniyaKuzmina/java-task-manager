package managementTask;

public class Managers {

    public static managementTask.InMemoryTasksManager getDefault() {
        return new managementTask.InMemoryTasksManager();
    }
}
