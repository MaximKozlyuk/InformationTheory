package lz78;

import lombok.Getter;

import java.io.*;
import java.util.*;

/**
 * Simple file archive functionality.
 * todo 16bit version if this one succeed...
 */
public class Lz78 {

    public static final int DEFAULT_PORTION_SIZE = 512;

    @Getter
    private int portionSize = DEFAULT_PORTION_SIZE;

    private final ByteTree dictionary;  // might be local var

    public Lz78() {
        dictionary = new ByteTree();
    }

    public Lz78(int portionSize) {
        this();
        this.portionSize = portionSize;
    }

    public void setPortionSize(int portionSize) {
        if (portionSize % 2 != 0) {
            throw new RuntimeException("portion size must be even");
        }
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
            long timeBegin = System.currentTimeMillis();


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
                            fos.write(new byte[]{(byte)buf.getSize()});
                            fos.write(buf.slice());
                            phraseCounter = Byte.MIN_VALUE;
                            dictionary.clear();
                        }
                        buf.clear();
                    }
                }
            } while (read > 0);
            if (buf.getSize() > 0) {
                fos.write(new byte[]{(byte)buf.getSize()});
                fos.write(buf.slice());
            }

            System.out.println("time spent: " + (System.currentTimeMillis() - timeBegin));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unArchiveFile (File archived, File unArchived) {
        dictionary.clear();
        try (FileInputStream fis = new FileInputStream(archived);
             FileOutputStream fos = new FileOutputStream(unArchived)
        ) {
            int read;
            byte phraseCounter = Byte.MIN_VALUE;
            byte[] chunkData = new byte[portionSize], assemble;

            do {
                read = fis.read(chunkData);
                for (int i = 0; i < read; i += 2) { // process chunk

                }
                // process tail
            } while (read > 0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final class DecompressionDictionary {
        final List<Phrase> chunkDictionary = new ArrayList<>();

        public DecompressionDictionary() {
        }

        public void addPhrase (byte val, byte link) {
            // assemble phrase
            ByteBuffer buf = new ByteBuffer();
            while (link != 0) {

            }

        }

    }

    @Getter
    private static final class Phrase {
        private final byte[] allPhrase;
        private final byte val;
        private final byte id;

        public Phrase(byte[] allPhrase, byte val, byte id) {
            this.allPhrase = allPhrase;
            this.val = val;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Phrase phrase = (Phrase) o;
            return val == phrase.val &&
                    id == phrase.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

}
