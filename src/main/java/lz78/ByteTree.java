package lz78;

import java.util.*;

/**
 * Stores any byte[] as sequence of nodes with bytes.
 * Do not stores equivalent byte arrays. Only one sequence can be stored.
 * Do not stores empty array ( new byte[] {} ).
 * Maximum depth of tree = maximum length of byte[] stored in tree.
 */
public class ByteTree {

    /**
     * Tree starts from root, it might be any byte [-128..127]
     */
    private final Node root;

    public ByteTree() {
        root = new Node((byte)0);   // this 0 is never take in account, just for root creation
    }

    protected static class Node {
        private final byte val;
        /**
         * size obviously will never be > 256
         */
        private final List<Node> leaves;

        public Node(byte val) {
            this.val = val;
            this.leaves = new ArrayList<>();
        }

        public Node leavesContains (byte b) {
            int size = leaves.size();
            for (int i = 0; i < size; i++) {
                if (leaves.get(i).val == b) {
                    return leaves.get(i);
                }
            }
            return null;
        }

        public Node add (byte b) {
            Node n = leavesContains(b);
            if (n == null) {
                n = new Node(b);
                leaves.add(n);
            }
            return n;
        }
    }

    public boolean isEmpty() {
        return root.leaves.size() == 0;
    }

    public boolean contains(Object o) {
        if (!(o instanceof byte[])) {
            return false;
        }
        return noCheckContains((byte[]) o);
    }

    public boolean add(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return false;
        }
        Node currentNode = root;
        for (int iByte = 0; iByte < bytes.length; iByte++) {
            currentNode = currentNode.add(bytes[iByte]);
        }
        return true;
    }

    private boolean noCheckContains (byte[] bytes) {
        return false;
    }

    public void clear() {
        root.leaves.clear(); // todo not efficient, need GC help
    }

    /**
     * @return amount of arrays, stored in tree.
     * in other words - amount of lowest leaves.
     */
    public int size() {
        return 0;
    }

}
