import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

class BTree {
    Node root; // указатель на корень дерева
    int t; // минимальная степень дерева

    public BTree() {
    }

    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    public void traverse() {
        if (root != null) {
            ArrayList<Integer> keysArray = new ArrayList<>();
            ArrayList<String> valuesArray = new ArrayList<>();
            root.traverse(keysArray, valuesArray);
            for (int i = 0; i < keysArray.size(); i++) {
                int elem = keysArray.get(i);
                String val = valuesArray.get(i);
                System.out.printf("%d : %s", elem, val);
                System.out.println();
            }
        }
        System.out.println();
    }

    public String search(int key) {
        String result;
        int[] comp = new int[1];
        Node node = root == null ? null : root.search(key, comp);
        System.out.println(comp[0] + " comparisons were made to find the value: " + key);
        result = node.values[node.findKey(key)];
        return result;
    }

    public void add(int key, String value) {
        if (root == null) {
            root = new Node(t, true);
            root.keys[0] = key;
            root.values[0] = value;
            root.num = 1;
        } else {
            if (root.num == 2 * t - 1) {
                Node node = new Node(t, false);
                node.child[0] = root;
                node.splitChild(0, root);
                int i = 0;
                if (node.keys[0] < key)
                    i++;
                node.child[i].addNotFull(key, value);

                root = node;
            } else
                root.addNotFull(key, value);
        }
    }

    public void remove(int key) {
        if (root == null) {
            System.out.println("Tree is empty!!!");
            return;
        }
        root.remove(key);
        if (root.num == 0) {
            if (root.leaf)
                root = null;
            else
                root = root.child[0];
        }
    }

    private ArrayList<String> getData(String path) {
        ArrayList<String> strings = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(path))) {
            strings.addAll(Arrays.asList(dis.readUTF().split("\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }

    public void getTree() {
        ArrayList<String> keyArray = getData("keys.bin");
        ArrayList<String> valueArray = getData("values.bin");
        this.t = Integer.parseInt(keyArray.get(0));
        for (int i = 1; i < valueArray.size(); i++) {
            add(Integer.parseInt(keyArray.get(i)), valueArray.get(i));

        }
    }

    public void saveData() throws IOException {
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        ArrayList<String> stringArrayList = new ArrayList<>();
        root.traverse(integerArrayList, stringArrayList);
        StringBuilder builderInt = new StringBuilder();
        StringBuilder builderStr = new StringBuilder();
        builderInt.append(this.t).append("\n");
        for (int i = 0; i < integerArrayList.size(); i++) {
            int intEl = integerArrayList.get(i);
            String strEl = stringArrayList.get(i);
            builderInt.append(intEl).append("\n");
            builderStr.append(strEl).append("\n");
        }
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream("keys.bin"))) {
            dataOutputStream.writeUTF(builderInt.toString());
        }
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream("values.bin"))) {
            dataOutputStream.writeUTF(builderStr.toString());
        }

    }
}