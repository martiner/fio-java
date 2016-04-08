package cz.geek.fio;

public class FioException extends RuntimeException {

    public FioException(final String message) {
        super(message);
    }

    public FioException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
