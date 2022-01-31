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

    // добавляет задачу в конец списка просмотров, удаляет ранее просмотренную такую же задачу
    @Override
    public void add(Task task) { //После того как подробнее разобралась во всех вопросах, решила изначально сама изменить этот метод.
        // Опираясь на свои личные мысли и рассуждения так сказать, и пока не смотреть на ваше предложение, как его изменить
        //Потом проверила и оказалось что я его переделала так, как вы и предложили. :)
        //Собой довольно, а вам ещё раз спасибо, вы очень помогли.
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
    public void removeAll() {
        removeAllNodes();
        tasksMapHistory.clear();
    }

    // возвращает список просмотренных задач
    @Override
    public List<Task> getHistoryList() { // доработала метод, в нём была неточность.
        // Когда я просматривала задачи по id и после этого просматривала историю просмотров, у меня в истории выводились не все задачи, а все кроме последней.
        // Это было потому, что цикл while выполняется до тех пор, пока nextElement у ноды не равен null:(node.nextElement != null)
        // Соответственно, если nextElement равен null то цикл прекращает выполнение и уже данные по текущей ноде (у которой nextElement == null) не добавляются в список:
        // taskHistory.add(node.data); — не выполняется
        // поэтому немного дописала реализацию метода, чтобы в историю добавлялись точно все просмотры.
        if (firstElement == null) { // добавила проверку, чтобы избежать исключения nullpointerexception, в случае если, например, мы удалили все таски, и соответственно все ноды тоже.
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
    private void removeNode(Node<Task> node) { //Спасибо вам огромное за такие большие и подробные комментарии, я разобралась что вы имели в виду и да, теперь всё встало на свои места.
        // До этого у меня было не совсем чёткое и ясное представление о том, что я делю. Сейчас я понимаю что уже гораздо лучше разбираюсь в том что здесь происходит. :)
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
