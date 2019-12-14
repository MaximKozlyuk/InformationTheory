package lz78;

import lombok.Getter;

@Getter
public class ByteBuffer {

    private final byte[] buf;
    private int size = 0;

    public ByteBuffer() {
        buf = new byte[256];
    }

    public ByteBuffer(int bufCap) {
        if (bufCap < 0) {
            throw new IllegalArgumentException();
        }
        buf = new byte[bufCap];
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
        return slice;
    }

    public void clear () {
        size = 0;
    }

}
