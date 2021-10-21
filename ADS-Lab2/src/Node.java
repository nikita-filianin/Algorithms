import java.util.ArrayList;

public class Node {
    int[] keys;
    String[] values;
    Node[] child;
    int t;
    int num;
    boolean leaf;

    public Node(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        this.keys = new int[2 * this.t - 1];
        this.values = new String[2 * this.t - 1];
        this.child = new Node[2 * this.t];
        this.num = 0;
    }

    public int findKey(int key) {
        int idx = 0;
        while (idx < num && keys[idx] < key)
            ++idx;
        return idx;
    }

    public void remove(int key) {
        int idx = findKey(key);
        if (idx < num && keys[idx] == key) {
            if (leaf)
                removeFromLeaf(idx);
            else
                removeFromNonLeaf(idx);
        } else {
            if (leaf) {
                System.out.printf("The key %d is not in a tree\n", key);
                return;
            }
            boolean flag = idx == num;
            if (child[idx].num < t)
                fill(idx);
            if (flag && idx > num)
                child[idx - 1].remove(key);
            else
                child[idx].remove(key);
        }
    }

    public void removeFromLeaf(int idx) {
        for (int i = idx + 1; i < num; ++i) {
            keys[i - 1] = keys[i];
            values[i - 1] = values[i];
        }
        num--;
    }

    public void removeFromNonLeaf(int idx) {
        int key = keys[idx];

        if (child[idx].num >= t) {
            int predecessorKey = getPredecessorKey(idx);
            String predecessorValue = getPredecessorValue(idx);
            values[idx] = predecessorValue;
            keys[idx] = predecessorKey;
            child[idx].remove(predecessorKey);
        } else if (child[idx + 1].num >= t) {
            int successorKey = getSuccessorKey(idx);
            String successorValue = getSuccessorValue(idx);
            keys[idx] = successorKey;
            child[idx + 1].remove(successorKey);
        } else {
            merge(idx);
            child[idx].remove(key);
        }
    }

    public int getPredecessorKey(int idx) {
        Node current = child[idx];
        while (!current.leaf)
            current = current.child[current.num];
        return current.keys[current.num - 1];
    }

    public String getPredecessorValue(int idx) {
        Node current = child[idx];
        while (!current.leaf)
            current = current.child[current.num];
        return current.values[current.num - 1];
    }

    public int getSuccessorKey(int idx) {
        Node current = child[idx + 1];
        while (!current.leaf)
            current = current.child[0];
        return current.keys[0];
    }

    public String getSuccessorValue(int idx) {
        Node current = child[idx + 1];
        while (!current.leaf)
            current = current.child[0];
        return current.values[0];
    }


    public void fill(int idx) {

        if (idx != 0 && child[idx - 1].num >= t)
            takeFromPrevious(idx);

        else if (idx != num && child[idx + 1].num >= t)
            takeFromNext(idx);
        else {
            if (idx != num)
                merge(idx);
            else
                merge(idx - 1);
        }
    }

    public void takeFromPrevious(int idx) {
        Node child = this.child[idx];
        Node sibling = this.child[idx - 1];

        for (int i = child.num - 1; i >= 0; --i) {
            child.keys[i + 1] = child.keys[i];
            child.values[i + 1] = child.values[i];
        }


        if (!child.leaf) {
            for (int i = child.num; i >= 0; --i) {
                child.child[i + 1] = child.child[i];
            }

        }

        child.keys[0] = keys[idx - 1];
        child.values[0] = values[idx - 1];

        if (!child.leaf)
            child.child[0] = sibling.child[sibling.num];

        keys[idx - 1] = sibling.keys[sibling.num - 1];
        values[idx - 1] = sibling.values[sibling.num - 1];
        child.num += 1;
        sibling.num -= 1;
    }

    public void takeFromNext(int idx) {
        Node child = this.child[idx];
        Node sibling = this.child[idx + 1];

        child.keys[child.num] = keys[idx];
        child.values[child.num] = values[idx];

        if (!child.leaf)
            child.child[child.num + 1] = sibling.child[0];

        keys[idx] = sibling.keys[0];
        values[idx] = sibling.values[0];

        for (int i = 1; i < sibling.num; ++i) {
            sibling.keys[i - 1] = sibling.keys[i];
            sibling.values[i - 1] = sibling.values[i];
        }


        if (!sibling.leaf) {
            for (int i = 1; i <= sibling.num; ++i)
                sibling.child[i - 1] = sibling.child[i];
        }
        child.num += 1;
        sibling.num -= 1;
    }

    public void merge(int idx) {
        Node child = this.child[idx];
        Node sibling = this.child[idx + 1];

        child.keys[t - 1] = keys[idx];
        child.values[t - 1] = values[idx];

        for (int i = 0; i < sibling.num; ++i) {
            child.keys[i + t] = sibling.keys[i];
            child.values[i + t] = sibling.values[i];
        }


        if (!child.leaf) {
            for (int i = 0; i <= sibling.num; ++i)
                child.child[i + t] = sibling.child[i];
        }

        for (int i = idx + 1; i < num; ++i) {
            keys[i - 1] = keys[i];
            values[i - 1] = values[i];
        }

        for (int i = idx + 2; i <= num; ++i)
            this.child[i - 1] = this.child[i];

        child.num += sibling.num + 1;
        num--;
    }

    public void addNotFull(int key, String value) {
        int i = num - 1;

        if (leaf) {
            while (i >= 0 && keys[i] > key) {
                keys[i + 1] = keys[i];
                values[i + 1] = values[i];
                i--;
            }
            keys[i + 1] = key;
            values[i + 1] = value;
            num++;
        } else {
            while (i >= 0 && keys[i] > key)
                i--;
            if (child[i + 1].num == 2 * t - 1) {
                splitChild(i + 1, child[i + 1]);
                if (keys[i + 1] < key)
                    i++;
            }
            child[i + 1].addNotFull(key, value);
        }
    }

    public void splitChild(int i, Node y) {
        Node z = new Node(y.t, y.leaf);
        z.num = t - 1;

        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = y.keys[j + t];
            z.values[j] = y.values[j + t];
        }
        if (!y.leaf) {
            for (int j = 0; j < t; j++)
                z.child[j] = y.child[j + t];
        }
        y.num = t - 1;

        for (int j = num; j >= i + 1; j--)
            child[j + 1] = child[j];
        child[i + 1] = z;

        for (int j = num - 1; j >= i; j--) {
            keys[j + 1] = keys[j];
            values[j + 1] = values[j];
        }

        keys[i] = y.keys[t - 1];
        values[i] = y.values[t - 1];

        num++;
    }

    public void traverse(ArrayList<Integer> keysArray, ArrayList<String> valuesArray) {
        int i;
        for (i = 0; i < num; i++) {
            if (!leaf)
                child[i].traverse(keysArray, valuesArray);
            keysArray.add(keys[i]);
            valuesArray.add(values[i]);
        }

        if (!leaf) {
            child[i].traverse(keysArray, valuesArray);
        }
    }

    public Node search(int key, int[] comp) {
        int i = 0;
        while (i < num && key > keys[i]) {
            i++;
            comp[0]++;
        }
        comp[0]++;

        if (keys[i] == key) {
            return this;
        }
        if (leaf)
            return null;
        return child[i].search(key, comp);
    }

}