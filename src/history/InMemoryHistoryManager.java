package history;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//хранит историю просмотра
//удалила связанный список MyList, оставила только мапу, но судя по ТЗ я поняла что нам нужна и мапа и наш собственный связанный список, который мы и пишем здесь.
// Потому что в случае если удаляем наш связанный список, в этом случае пропадает и смысл метода removeNode, или я не права?
// Хотя да, наверное логично оставить только мапу
public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Long, Node<Task>> tasksMapHistory;
    private Node<Task> firstElement;
    private Node<Task> lastElement;


    public InMemoryHistoryManager() {
        tasksMapHistory = new HashMap<>();
    }

    // добавляет задачу в конец списка просмотров, удаляет ранее просмотренную такую же задачу
    @Override
    public void add(Task task) {
        Long id = task.getId();
        Node<Task> nodeTask;
        if (!tasksMapHistory.isEmpty()) {
            if (tasksMapHistory.containsKey(id)) { // возник вопрос почему containsKey лучше, чем то что было:nodeTask = tasksMapHistory.getOrDefault(id, null)?
                tasksMapHistory.remove(id);
            }
            nodeTask = linkLast(task);
        } else {
            nodeTask = new Node(task, null, null);
            linkLast(task);
        }
        tasksMapHistory.put(id, nodeTask);
    }

    // удаляет задачу из списка просмотров по ID
    @Override
    public void remove(Long id) {
        Node<Task> nodeTask = tasksMapHistory.getOrDefault(id, null);
        if (nodeTask != null) {
            tasksMapHistory.remove(id);
        }
    }

    // удаляет сразу всю историю, если все задачи, эпики и подзадачи были удалены
    public void removeAll() {
        tasksMapHistory.clear();
    }

    // возвращает список просмотренных задач
    @Override
    public List<Task> getHistoryList() {
        List<Task> taskHistory = new ArrayList<>();
        /*for (Node<Task> nodeTask : tasksMapHistory.values()) {
            taskHistory.add(nodeTask.data);
        }*/
        //и вот здесь вопрос, почему именно цикл while и без мапы tasksMapHistory, а не for? Я написала цикл while,
        // но тогда у меня всё ломается и код не работает корректно. Я не получаю верный список истории. Или я что-то не то делаю.
        // мне кажется мапа здесь нужна потому что она хранит всю историю просмотров, разве нет?
        Node<Task> node = firstElement;
        while (node != null) {
            taskHistory.add(node.data);
            node = node.nextElement;
        }

        return taskHistory;

    }

   //добавляет элемент в конец списка
    private Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = lastElement;
        final Node<Task> newNode = new Node<>(task, null, oldTail);
        lastElement = newNode;
        if (oldTail == null) {
            firstElement = newNode;
        } else {
            oldTail.previousElement = newNode;
        }
        return newNode;
    }

    // удаляет узел
    private void removeNode(Node<Task> node) {
        if (node.previousElement != null) {
            node.previousElement.nextElement = node.nextElement; // почему мне кажется что при обращении node.previousElement.nextElement
            // мы получаем тот же самый элемент node который у нас изначально получает метод?
            // то есть у node.previousElement следующий элемент это будет наш node, а вот предыдущий элемент у node.previousElement уже будет другой Node.
            // из моих рассуждений кажется должно быть так: node.previousElement.previousElement = node.nextElement;
        } else {
            firstElement = node.nextElement;
        }
        if (node.nextElement != null) {
            node.nextElement.previousElement = node.previousElement; // здесь то же самое, разве нет?
            // node.nextElement.previousElement — если у node.nextElement обратиться к предыдущему элементу previousElement, то получается мы ссылаемся на node
            // из моих рассуждений кажется должно быть так: node.nextElement.nextElement = node.previousElement;
        } else {
            lastElement = node.previousElement; // здесь я указала null, потому что если условие if (node.previousElement != null) не выполнится,
            // то получается что node.previousElement имеет значение null.
        }
    }

}
