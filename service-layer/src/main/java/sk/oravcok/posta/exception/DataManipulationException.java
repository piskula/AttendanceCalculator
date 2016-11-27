package sk.oravcok.posta.exception;

import org.springframework.dao.DataAccessException;

/**
 * this exception corresponds to all possible exceptions, which can be thrown from downstream layers
 *
 * Created by Ondrej Oravcok on 27-Nov-16.
 */
public class DataManipulationException extends DataAccessException {

    public DataManipulationException(String message){
        super(message);
    }

    public DataManipulationException(String message, Throwable cause){
        super(message, cause);
    }

}
