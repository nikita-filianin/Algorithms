package puzzle;

import java.util.Stack;

public class SortedStack<K> extends Stack {
    private Stack<Node> Past = new Stack<>();

    @Override
    public Object push(Object item) {
        K key = (K) item;
        Node node = (Node) key;
        int F_node = node.getFn();
        while (!this.isEmpty()) {
            Node head = (Node) this.peek();
            int F_stack = head.getFn();
            if (F_stack > F_node)
                break;
            else
                Past.push((Node) this.pop());
        }
        super.push(item);
        while (!Past.isEmpty()) {
            super.push(Past.pop());
        }
        return 1;
    }
}
