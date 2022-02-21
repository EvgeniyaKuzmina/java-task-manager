package history;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//хранит историю просмотра
public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Long, Node<Task>> tasksMapHistory;
    private Node<Task> firstElement;
    private Node<Task> lastElement;

    public InMemoryHistoryManager() {
        tasksMapHistory = new HashMap<>();
    }

    public HashMap<Long, Node<Task>> getTasksMapHistory() {
        return tasksMapHistory;
    }

    // добавляет задачу в конец списка просмотров, удаляет ранее просмотренную такую же задачу
    @Override
    public void add(Task task) {
        Long id = task.getId();
        Node<Task> nodeTask;
        if (tasksMapHistory.containsKey(id)) {
            removeNode(tasksMapHistory.get(id));
        }
        nodeTask = linkLast(task);
        tasksMapHistory.put(id, nodeTask);
    }

    // удаляет задачу из списка просмотров по ID
    @Override
    public void remove(Long id) {
        if (tasksMapHistory.containsKey(id)) {
            removeNode(tasksMapHistory.get(id));
            tasksMapHistory.remove(id);
        }
    }

    // удаляет сразу всю историю, если все задачи, эпики и подзадачи были удалены
    @Override
    public void removeAll() {
        removeAllNodes();
        tasksMapHistory.clear();
    }

    // возвращает список просмотренных задач
    @Override
    public List<Task> getHistoryList() {
        if (firstElement == null) {
            return new ArrayList<>();
        }
        List<Task> taskHistory = new ArrayList<>();
        Node<Task> node = firstElement;
        while (node.nextElement != null) {
            taskHistory.add(node.data);
            node = node.nextElement;
        }
        node = lastElement;
        taskHistory.add(node.data);
        return taskHistory;
    }

    //добавляет элемент в конец списка
    private Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = lastElement; // Null
        final Node<Task> newNode = new Node<>(task, null, oldTail);
        lastElement = newNode;
        if (oldTail == null) {
            firstElement = newNode;
        } else {
            oldTail.nextElement = newNode;
        }
        return newNode;
    }

    // удаляет узел
    private void removeNode(Node<Task> node) {
        if (node.previousElement != null) {
            node.previousElement.nextElement = node.nextElement;
        } else {
            firstElement = node.nextElement;
        }
        if (node.nextElement != null) {
            node.nextElement.previousElement = node.previousElement;
        } else {
            lastElement = node.previousElement;
        }
    }

    private void removeAllNodes() {
        firstElement = null;
        lastElement = null;
    }

}
