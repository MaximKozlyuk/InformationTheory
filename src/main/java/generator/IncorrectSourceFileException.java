package generator;

public class IncorrectSourceFileException extends RuntimeException {

    public IncorrectSourceFileException() {
    }

    public IncorrectSourceFileException(String message) {
        super(message);
    }

    public IncorrectSourceFileException(String message, Throwable cause) {
        super(message, cause);
    }

}
