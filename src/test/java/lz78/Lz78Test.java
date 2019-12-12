package lz78;

import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Lz78Test {

    @Test
    public void dummy () {
        HashMap m = new HashMap();
        byte[] a = new byte[]{1,2,3};
        byte[] b = new byte[]{1,2,3};
        System.out.println(a.hashCode() + " " + b.hashCode());
        System.out.println(Arrays.equals(a, b));
    }

    @Test
    public void byteTest () {
        byte b = 0;
        for (int i = 0; i < 513; i++) {
            b++;
            System.out.println(b);
        }
    }

    @Test
    public void lz78Test () {
        Lz78 lz = new Lz78();
        File result = new File("./lz_result.lz");
        try {
            result.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lz.archiveFile(new File("./red_color.txt"), result);
    }

    @Test
    public void arrToStrTest () {
        byte[] arr1 = new byte[]{1,2,3,4,5,0,0,0,0,0};

        String str = new String(arr1);
        System.out.println(str);
    }

    @Test
    public void arraysHashCodeTest () {
        List<byte[]> preArrays = new ArrayList<>();
        byte[] arr = {0};
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < 256; i++) { // i - index of byte in array
            for (int j = 0; j < 256; j++) {     // j - current byte value
                set.add(Arrays.hashCode(arr));
                arr[i]++;
            }
            byte[] newArr = new byte[arr.length+1];
            System.arraycopy(arr,0,newArr,0,arr.length);
            preArrays.add(arr);
            arr = newArr;
        }
        System.out.println(set.size());
    }

    @Test
    public void assembleArrayTest () {
        Random rand = new Random(System.currentTimeMillis());
        byte[] data = new byte[1024 * 1024];
        rand.nextBytes(data);
        AssembleNode root, current, next;
        root = new AssembleNode((byte) 0);
        current = new AssembleNode((byte) 0);
        root.setNext(current);
        for (int i = 1; i < data.length; i++) {
            next = new AssembleNode(data[i]);
            current.setNext(next);
            current = next;
        }
    }

    @Getter
    private static final class AssembleNode {
        private final byte val;
        @Setter
        private AssembleNode next;

        public AssembleNode(byte val) {
            this.val = val;
        }

    }

}