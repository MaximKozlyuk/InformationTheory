package lz78;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple file archive functionality.
 * todo 16bit version if this one succeed...
 */
public class Lz78 {

    public static final int DEFAULT_PORTION_SIZE = 512;

    @Getter @Setter
    private int portionSize = DEFAULT_PORTION_SIZE;

    private final ByteTree dictionary;

    public Lz78() {
        dictionary = new ByteTree();
    }

    public Lz78(int portionSize) {
        this();
        this.portionSize = portionSize;
    }

    public void archiveFile (File file, File archived) {
        if (file == null || !file.exists()) {
            throw new RuntimeException("cant' read file");
        }
        if (!file.exists() || !archived.canWrite()) {
            throw new IllegalArgumentException("file is not exist or can't write result");
        }
        dictionary.clear();

        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(archived)
        ) {
            final ByteBuffer buf = new ByteBuffer();
            int read;
            byte phraseCounter = Byte.MIN_VALUE;
            byte[] portion, phraseContainer = new byte[2];
            do {
                portion = new byte[portionSize];
                read = fis.read(portion);
                for (int i = 0; i < read; i++) {
                    buf.append(portion[i]);
                    if (dictionary.add(buf)) {
                        phraseContainer[0] = portion[i];
                        phraseContainer[1] = dictionary.getLatestId();
                        phraseCounter++;
                        fos.write(phraseContainer);
                        if (phraseCounter == Byte.MAX_VALUE) { // is was written maximum amount of phrases
                            // todo tail writing [tail size - n bytes] [n bytes of tail] [new chunk]
                            phraseCounter = Byte.MIN_VALUE;
                            dictionary.clear();
                        }
                        buf.clear();
                    }
                }
            } while (read > 0);
            if (buf.getSize() > 0) {
                // write the rest as tail
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unArchiveFile (File archived, File unArchived) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(archived));   // todo BufferedInputStream
             FileOutputStream fos = new FileOutputStream(unArchived)
        ) {
            int read;
            byte phraseCounter = Byte.MIN_VALUE;
            byte[] phrase = new byte[2];
            Map<Byte, Byte> currentChunk = new HashMap<>(256);
            while (true) {   // read all bytes in file
               for (int i = 0; i < 256; i++) {  // read chunk
                   read = bis.read(phrase);
                   currentChunk.put(phrase[0],phrase[1]);
                   if (bis.available() < 1) {
                       break;
                   }
               }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
