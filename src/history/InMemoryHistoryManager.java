package history;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//хранит историю просмотра
public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Long, Node<Task>> tasksMapHistory;
    private final MyList<Task> historyList;


    public InMemoryHistoryManager() {
        tasksMapHistory = new HashMap<>();
        historyList = new MyList<>();
    }

    // добавляет задачу в конец списка просмотров, удаляет ранее просмотренную такую же задачу
    @Override
    public void add(Task task) {
        Long id = task.getId();
        Node<Task> nodeTask;
        if (!tasksMapHistory.isEmpty()) {
            nodeTask = tasksMapHistory.getOrDefault(id, null);
            if (nodeTask != null) {
                historyList.removeNode(nodeTask);
                tasksMapHistory.remove(id);
            }
            nodeTask = historyList.linkLast(task);
        } else {
            nodeTask = new Node(task, null, null);
            historyList.linkLast(task);
        }
        tasksMapHistory.put(id, nodeTask);
    }

    // удаляет задачу из списка просмотров по ID
    @Override
    public void remove(Long id) {
        Node<Task> nodeTask = tasksMapHistory.getOrDefault(id, null);
        if (nodeTask != null) {
            historyList.removeNode(nodeTask);
            tasksMapHistory.remove(id);
        }
    }

    // удаляет сразу всю историю, если все задачи, эпики и подзадачи были удалены
    public void removeAll() {
        for (Node<Task> node : tasksMapHistory.values()) {
            historyList.removeNode(node);
        }
        tasksMapHistory.clear();
    }

    // возвращает список просмотренных задач
    @Override
    public List<Task> getHistoryList() {
        List<Task> taskHistory = new ArrayList<>();
        for (Node<Task> nodeTask : tasksMapHistory.values()) {
            taskHistory.add(nodeTask.data);
        }
        return taskHistory;
    }

    // реализация класса для связанного списка
    private static class MyList<T> {
        private Node<T> firstElement;
        private Node<T> lastElement;

        private Node<T> linkLast(T task) {
            final Node<T> oldTail = lastElement;
            final Node<T> newNode = new Node<>(task, null, oldTail);
            lastElement = newNode;
            if (oldTail == null) {
                firstElement = newNode;
            } else {
                oldTail.previousElement = newNode;
            }
            return newNode;
        }

        private void removeNode(Node<T> node) {
            if (node.previousElement != null) {
                node.previousElement = node.nextElement;
            } else {
                firstElement = node.nextElement;
            }
            if (node.nextElement != null) {
                node.nextElement = node.previousElement;
            } else {
                lastElement = null;
            }
        }
    }
}
