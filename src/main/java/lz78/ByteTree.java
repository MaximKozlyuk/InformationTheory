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
    private byte latestId = Byte.MIN_VALUE;

    public byte getLatestId() {
        return (byte)(latestId - 1);
    }

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
            return leaves.add(n);   // todo optimize lookup in leaves, linear lookup -> classic binary search
        }

    }

    public boolean add(ByteBuffer buf) {
        Node currentNode = root, nextNode;
        byte[] bufArr = buf.getBuf();

        for (int i = 0; i < buf.getSize(); i++) {
             nextNode = currentNode.leavesContains(bufArr[i]);
             if (nextNode == null) {
                 nextNode = new Node(latestId++, currentNode.id, bufArr[i]);
                 currentNode.add(nextNode);
                size++;
                 return true;
             }
            currentNode = nextNode;
        }
        return false;
    }


//    // todo MB instead this, implement search in depth
//    // we can exclude from search all nodes, which id is > than desired id
//    // for now, array implementation is ok, but for 16bit version - not
//    private static final class CachedArray {
//        private final byte[] arr;
//        private final byte id;
//        public CachedArray(byte[] arr, byte id) {
//            this.arr = arr;
//            this.id = id;
//        }
//    }
//
//    // todo at least here should by binary search
//    private final List<CachedArray> cache = new ArrayList<>();
//    @Getter @Setter
//    private boolean isCached = false;
//
//    public byte[] getArr (byte lastNodeId) {
//        for (int i = 0; i < cache.size(); i++) {
//            if (cache.get(i).id == lastNodeId) {
//                return cache.get(i).arr;
//            }
//        }
//        return null;
//    }

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