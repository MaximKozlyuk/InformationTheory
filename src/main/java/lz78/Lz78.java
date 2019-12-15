package lz78;

import lombok.Getter;
import lombok.Setter;

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

    private byte lastPhraseId = Byte.MIN_VALUE+1;

    private final ByteTree dictionary;  // might be local var

    public Lz78() {
        dictionary = new ByteTree();
    }

    public Lz78(int portionSize) {
        this();
        setPortionSize(portionSize);
    }

    public void setPortionSize(int size) {
        if (size % 2 != 0) {
            throw new RuntimeException("portion size must be even");
        }
        this.portionSize = size;
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
            byte phraseCounter = Byte.MIN_VALUE+1;
            Byte currentLink;
            byte[] portion;
            List<Byte> cache = new ArrayList<>();
            do {
                portion = new byte[portionSize];    // redundant new mb
                read = fis.read(portion);
                for (int i = 0; i < read; i++) {
                    buf.append(portion[i]);
                    currentLink = dictionary.add(buf);
                    if (currentLink != null) {
                        phraseCounter++;
                        cache.add(portion[i]);
                        cache.add(currentLink);
                        if (phraseCounter == Byte.MAX_VALUE) { // is was written maximum amount of phrases
                            cache.add((byte)buf.getSize());
                            for (Byte b : buf) {
                                cache.add(b);
                            }
                            writeCache(fos, cache);
                            cache.clear();
                            phraseCounter = Byte.MIN_VALUE+1;
                            dictionary.clear();
                        }
                        buf.clear();
                    }
                }
            } while (read > 0);
            if (cache.size() > 0) {
                fos.write(cache.size() / 2);    // write amount of phrases
                writeCache(fos, cache);
            }
            fos.write(new byte[]{(byte)buf.getSize()});
            if (buf.getSize() > 0) {
                fos.write(buf.slice());
            }
            System.out.println("time spent: " + (System.currentTimeMillis() - timeBegin));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCache (FileOutputStream fos, List<Byte> cache) throws IOException {
        byte[] arr = new byte[cache.size()];
        for (int i = 0 ; i < cache.size(); i++) {
            arr[i] = cache.get(i);
        }
        fos.write(arr);
    }

    /**
     *
     * @param archived file to process
     * @param unArchived file to write result in
     */
    public void unArchiveFile (File archived, File unArchived) {
        dictionary.clear();
        try (FileInputStream fis = new FileInputStream(archived);
             FileOutputStream fos = new FileOutputStream(unArchived)
        ) {
            int read;
            byte currentSize;
            final List<Phrase> phrases = new ArrayList<>();
            for (;;) {  // loop over chunks with tails
                currentSize = (byte)fis.read(); // amount of phrases in chunk
                if (currentSize < 1) {
                    break;
                }
                byte[] chunkData = new byte[currentSize * 2];
                read = fis.read(chunkData); // read should be equal to chunkData size else file - corrupted
                byte[] buf;
                for (int i = 0; i < read; i+=2) {
                    buf = addPhrase(phrases, chunkData[i], chunkData[i+1]);
                    fos.write(buf); // todo buffered writing
                }
                currentSize = (byte)fis.read(); // reads size of tail
                if (currentSize < 1) {
                    break;
                }
                byte[] tailData = new byte[currentSize];
                read = fis.read(tailData);
                fos.write(tailData);

                System.out.println("chunk written. size=" + phrases.size() + " last phrase id=" + lastPhraseId);
                phrases.clear();
                lastPhraseId = Byte.MIN_VALUE+1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] addPhrase (final List<Phrase> phrases, byte val, byte link) {
        Phrase newPhrase = new Phrase(lastPhraseId++, val, link);
        if (link == Byte.MIN_VALUE) {
            newPhrase.setAllPhrase(new byte[]{val});
            phrases.add(newPhrase);
            return newPhrase.getAllPhrase();
        }
        for (Phrase p : phrases) {
            if (p.getId() == link) {
                byte[] allPhrase = new byte[p.getAllPhrase().length+1];
                System.arraycopy(p.getAllPhrase(), 0,allPhrase,0,p.getAllPhrase().length);
                allPhrase[allPhrase.length-1] = val;
                newPhrase.setAllPhrase(allPhrase);
                phrases.add(newPhrase);
                return newPhrase.getAllPhrase();
            }
        }
        throw new FileCorruptionException("File seems corrupted");
    }

    @Getter
    private static final class Phrase {

        private static final byte[] emptyPhrase = new byte[]{};

        @Setter
        private byte[] allPhrase = emptyPhrase;
        private final byte id;
        private final byte val;
        private final byte link;

        public Phrase(byte id, byte val, byte link) {
            this.id = id;
            this.val = val;
            this.link = link;
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
