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

        buf.addAll(ar1);
        tree.add(buf);
        buf.clear();

        buf.addAll(ar2);
        tree.add(buf);
        buf.clear();

        buf.addAll(ar3);
        tree.add(buf);
        buf.clear();

        buf.addAll(ar4);
        tree.add(buf);
        buf.clear();

        buf.addAll(ar5);
        tree.add(buf);
        buf.clear();

        buf.addAll(ar6);
        tree.add(buf);
        buf.clear();

        buf.addAll(ar7);
        tree.add(buf);
        buf.clear();

        buf.addAll(ar8);
        tree.add(buf);
        buf.clear();

        buf.addAll(ar9);
        tree.add(buf);
        buf.clear();

        assert tree.contains(ar1);
        assert tree.contains(ar3);
        assert tree.contains(ar8);
        assert tree.contains(ar9);
        assert !tree.contains(new byte[]{5, 6, 7, 8, 9});
        assert !tree.contains(new byte[] {0});
        assert !tree.contains(new byte[] {1,1,0});
    }

}