import api.HTTPTaskManager;
import api.HttpTaskServer;
import api.KVTaskClient;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.URI;

public class Main {

    public static void main(String[] args) {

        try {
            KVTaskClient kvTaskClient = new KVTaskClient(URI.create("http://localhost:8078"));
            TaskManager taskManager = Managers.getServerManager(kvTaskClient);
            HttpTaskServer taskServer = new HttpTaskServer(taskManager);
            taskServer.start();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                       "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}
