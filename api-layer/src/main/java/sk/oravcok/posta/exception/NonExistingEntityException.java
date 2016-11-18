package sk.oravcok.posta.exception;

/**
 * Exception thrown on facade layer when requested entity does not exist.
 *
 * Created by Ondrej Oravcok on 18-Nov-16.
 */
public class NonExistingEntityException extends RuntimeException {

    public NonExistingEntityException(String message) {
        super(message);
    }

    public NonExistingEntityException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
