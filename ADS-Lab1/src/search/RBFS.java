package search;

import puzzle.Node;
import puzzle.SortedStack;


import java.util.Arrays;
import java.util.Stack;

public class RBFS {
    private Node root;
    private Stack<SortedStack> stack;
    private Node[] result;
    private int index;

    public RBFS(Node r) {
        root = r;
        stack = new Stack<>();
        index = 0;
    }

    public Node solve() {
        Node[] solution = search(root, Integer.MAX_VALUE);
        return solution[0];
    }

    private Node[] search(Node node, int f_limit) {
        SortedStack successors = new SortedStack();
        if (node.isGoal()) {
            System.out.println("All states: " + index);

            return new Node[]{node, null};
        }


        for (Node child : node.generateChildren()) {
            if (child == null)
                break;
            if (!Arrays.equals(child.getState(), root.getState())) {
                successors.push(child);
                index++;
            }
        }
        while (!successors.isEmpty()) {
            Node best = (Node) successors.pop();
            if (best.getFn() > f_limit)
                return new Node[]{null, best};
            int alternative = ((Node) successors.peek()).getFn();
            stack.push(successors);
            result = search(best, Math.min(alternative, f_limit));
            successors = stack.pop();
            if (result[1] != null) {
                best.setFn(result[1].getFn());
                successors.push(best);
            }
            if (result[0] != null)
                break;
        }
        return result;
    }
}
