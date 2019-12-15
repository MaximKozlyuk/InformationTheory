package lz78;

public class NodeLeaves extends ByteBuffer {

    public NodeLeaves() {
        super();
    }

    public boolean add (byte b) {
        int size = getSize();
        byte[] buf = getBuf();
        return false;
    }

    private boolean isBinaryAdded(byte arr[], int l, int r, int x) {
        if (r >= l) {
            int mid = l + (r - l) / 2;
            if (arr[mid] == x) {
                return true;
            }
            if (arr[mid] > x) {
                return isBinaryAdded(arr, l, mid - 1, x);
            }
            return isBinaryAdded(arr, mid + 1, r, x);
        }
        if (getSize() == 256) {
            return false;
        }
        //System.arraycopy(arr, );
        return false;
    }

}
