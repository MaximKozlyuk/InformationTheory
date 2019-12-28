package lz78;

import java.util.Iterator;

/**
 * Simple wrapper over byte array for buffering needs.
 */

public class ByteBuffer implements Iterable<Byte> {

    private byte[] buf;
    private byte size = Byte.MIN_VALUE;

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
        buf[size+128] = b;
        size++;
    }

    @Deprecated // for tests only
    public void appendAll(byte[] arr) {
        for (byte b : arr) {
            append(b);
        }
    }

    public byte[] slice () {
        byte[] slice = new byte[size];
        System.arraycopy(buf, 0,slice, 0, size+128);
        return slice;
    }

    public void clear () {
        size = Byte.MIN_VALUE;
    }

    public byte[] getBuf() {
        return buf;
    }

    public byte getSize() {
        return size;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private int i = Byte.MIN_VALUE;

            @Override
            public boolean hasNext() {
                return size > i;
            }

            @Override
            public Byte next() {
                byte b = buf[i+128];
                i++;
                return b;
            }
        };
    }
}
