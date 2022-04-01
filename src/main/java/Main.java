import api.HTTPTaskManager;
import api.HttpTaskServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {
            TaskManager taskManager = Managers.getServerManager("http://localhost:8078");

            HttpTaskServer taskServer = new HttpTaskServer(taskManager);
            taskServer.start();
            //taskManager.load();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                       "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}
