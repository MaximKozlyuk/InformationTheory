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
    private int size = 0;   // todo might be useless
    private byte latestId = Byte.MIN_VALUE+1;

    public byte getLatestId() {
        return (byte)(latestId - 1);
    }

    // this 0's is never taken in account, just for root creation
    public ByteTree() {
        root = new Node(Byte.MIN_VALUE, (byte)0, (byte)0);
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
            return leaves.add(n);   // todo optimize lookup in leaves, linear lookup -> binary search
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return val == node.val &&
                    id == node.id &&
                    prevId == node.prevId;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    public Byte add(ByteBuffer buf) {
        Node currentNode = root, nextNode;
        byte[] bufArr = buf.getBuf();
        for (int i = Byte.MIN_VALUE; i < buf.getSize(); i++) {
             nextNode = currentNode.leavesContains(bufArr[i + 128]);
             if (nextNode == null) {
                 nextNode = new Node(latestId++, currentNode.id, bufArr[i+128]);
                 currentNode.add(nextNode);
                 size++;
                 return currentNode.id;
             }
            currentNode = nextNode;
        }
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
        latestId = Byte.MIN_VALUE+1;
        size = 0;
    }

    public boolean isEmpty() {
        return root.leaves.size() == 0;
    }

}