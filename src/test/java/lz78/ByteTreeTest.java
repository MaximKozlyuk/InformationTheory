package lz78;

import org.junit.Test;

public class ByteTreeTest {

    byte[] ar1 = {1};
    byte[] ar2 = {2};
    byte[] ar3 = {3};
    byte[] ar4 = {1,1};
    byte[] ar5 = {1,3};
    byte[] ar6 = {2,3};
    byte[] ar7 = {3,2};
    byte[] ar8 = {3,2,1};
    byte[] ar9 = {1,3,4};

    @Test
    public void testTree () {
        ByteTree tree = new ByteTree();
        ByteBuffer buf = new ByteBuffer();

        buf.appendAll(ar1);
        tree.add(buf);
        buf.clear();

        buf.appendAll(ar2);
        tree.add(buf);
        buf.clear();

        buf.appendAll(ar3);
        tree.add(buf);
        buf.clear();

        buf.appendAll(ar4);
        tree.add(buf);
        buf.clear();

        buf.appendAll(ar5);
        tree.add(buf);
        buf.clear();

        buf.appendAll(ar6);
        tree.add(buf);
        buf.clear();

        buf.appendAll(ar7);
        tree.add(buf);
        buf.clear();

        buf.appendAll(ar8);
        tree.add(buf);
        buf.clear();

        buf.appendAll(ar9);
        tree.add(buf);
        buf.clear();

        System.out.println();
    }

}