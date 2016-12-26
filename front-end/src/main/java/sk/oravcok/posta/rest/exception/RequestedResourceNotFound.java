package sk.oravcok.posta.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * HTTP 404 not found
 *
 * @author Ondrej Oravcok
 * @version  25-Dec-16.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Requested resource does not exist in system")
public class RequestedResourceNotFound extends RuntimeException {

    public RequestedResourceNotFound(String message, Throwable cause){
        super(message, cause);
    }

}
