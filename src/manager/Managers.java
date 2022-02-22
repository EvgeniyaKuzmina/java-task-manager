package manager;

import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTasksManager();
    }

    // не совсем понимаю зачем нам дугой метод? И что значит Open/Closed Principle?
    // сделала вот так, как второй вариант того, что метод loadFromFile() теперь у нас возвращает объект FileBackedTasksManager
    //Тогда в методе main можно вызывать так же метод getDefault() с параметром. Вместо того чтобы в main вызывать сам метод loadFromFile()
    public static TaskManager getDefault(String filePath) {
        return FileBackedTasksManager.loadFromFile(Paths.get(filePath));

    }
}
