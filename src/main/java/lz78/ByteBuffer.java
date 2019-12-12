package lz78;

import java.util.Arrays;

public class ByteBuffer {

    private final byte[] buf = new byte[256];
    private int size = 0;

    public ByteBuffer() {
    }

    public byte[] getBuf() {
        return buf;
    }

    public int getSize() {
        return size;
    }

    public void append(byte b) {
        buf[size++] = b;
    }

    @Deprecated // for tests only
    public void addAll (byte[] arr) {
        for (byte b : arr) {
            append(b);
        }
    }

    public byte[] slice () {
        byte[] slice = new byte[size];
        System.arraycopy(buf, 0,slice, 0, size);
        size = 0;
        return slice;
    }

    public void clear () {
        Arrays.fill(buf, (byte)0);  // todo unnecessary
        size = 0;
    }

}
