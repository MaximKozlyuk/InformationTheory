package lz78;

import lombok.Getter;

import java.util.Iterator;

/**
 * Simple wrapper over byte array for buffering needs.
 */
@Getter
public class ByteBuffer implements Iterable<Byte> {

    private byte[] buf;
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
    public void appendAll(byte[] arr) {
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

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return size > i;
            }

            @Override
            public Byte next() {
                return buf[i++];
            }
        };
    }
}
