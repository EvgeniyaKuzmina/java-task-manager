package history;

public class Node<T> {

    protected T data;
    protected Node<T> nextElement;
    protected Node<T> previousElement;

    public Node(T data, Node<T> nextElement, Node<T> previousElement) {
        this.data = data;
        this.nextElement = nextElement;
        this.previousElement = previousElement;
    }
}
