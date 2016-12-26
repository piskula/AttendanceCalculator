package sk.oravcok.posta.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used by Rest services.
 * Represents HTTP 422 Unprocessable entity response code
 *
 * @author Ondrej Oravcok
 * @version 25.12.2016
 */
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Requested resource already exists in system")
public class ExistingResourceException extends RuntimeException {
    public ExistingResourceException(Throwable cause) {
        super(cause);
    }
}
