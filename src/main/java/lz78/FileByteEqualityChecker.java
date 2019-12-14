package lz78;

import lombok.Getter;
import lombok.Setter;

import java.io.*;

@Getter @Setter
public final class FileByteEqualityChecker {

    private File a;
    private File b;

    public FileByteEqualityChecker(File a, File b) {
        this.a = a;
        this.b = b;
    }

    public boolean checkEquality () {
        if (!a.canRead() || !b.canRead()) {
            throw new RuntimeException("can't read one of files");
        }
        try (
            BufferedInputStream inA = new BufferedInputStream(new FileInputStream(a));
            BufferedInputStream inB = new BufferedInputStream(new FileInputStream(b))
        ) {
            byte[] aPortion = new byte[1 << 12];
            byte[] bPortion = new byte[1 << 12];
            int readA, readB;
            do {
                readA = inA.read(aPortion);
                readB = inB.read(bPortion);
                if (readA != readB) {
                    return false;
                }
                for (int i = 0; i < readA; i++) {
                    if (aPortion[i] != bPortion[i]) {
                        return false;
                    }
                }
            } while (readA > 0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
