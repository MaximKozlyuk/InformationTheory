package lz78;

public class FileCorruptionException extends RuntimeException {

    public FileCorruptionException() { }

    public FileCorruptionException(String message) {
        super(message);
    }

    public FileCorruptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
