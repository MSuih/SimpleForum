package simplforum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Exception for requests targetting ID's which do not exist on the database.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class IdNotFoundException extends RuntimeException {

    /** Constructs a new exception with a default message.
     */
    public IdNotFoundException() {
        this("Requested resource could not be located");
    }

    public IdNotFoundException(String message) {
        super(message);
    }
}
