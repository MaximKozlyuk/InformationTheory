package lz78;

import lombok.Getter;

import java.util.*;

class ByteTree {

    private final Node root;

    /**
     * amount of arrays, stored in tree.
     * in other words - amount of nodes
     */
    @Getter
    private int size = 0;
    @Getter
    private byte latestId = Byte.MIN_VALUE;

    // this 0's is never taken in account (as an id -1), just for root creation
    public ByteTree() {
        root = new Node((byte)-1, (byte)0, (byte)0);
    }

    protected static class Node {

        private final byte val;
        private final byte id;
        private final byte prevId;
        private final List<Node> leaves;

        public Node(byte id, byte prevId, byte val) {
            this.id = id;
            this.prevId = prevId;
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

        public boolean add (Node n) {
            return leaves.add(n);   // to simplify future optimization here
        }

    }

    public boolean add(ByteBuffer buf) {
        Node currentNode = root, nextNode;
        byte[] bufArr = buf.getBuf();

        for (int i = 0; i < buf.getSize(); i++) {
             nextNode = currentNode.leavesContains(bufArr[i]);
             if (nextNode == null) {
                 nextNode = new Node((byte)size++, currentNode.id, bufArr[i]);
                 currentNode.add(nextNode);
                 return true;
             }
            currentNode = nextNode;
        }
        return false;
    }

    public byte[] getArr (byte lastNodeId) {
        return null;
    }

    public boolean contains (byte[] arr) {
        Node currentNode = root;
        Node nextNode;
        for (int i = 0; i < arr.length; i++) {
            nextNode = currentNode.leavesContains(arr[i]);
            if (nextNode != null) {
                currentNode = nextNode;
            } else {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        root.leaves.clear();
        latestId = Byte.MIN_VALUE;
        size = 0;
    }

    public boolean isEmpty() {
        return root.leaves.size() == 0;
    }

}