package simplforum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Exception for requests that target a user which doesn't exist.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    /** Constructs a new exception with a default message.
     */
    public UserNotFoundException() {
        this("The requested user does not exist");
    }

    public UserNotFoundException(String message) {
        super(message);
    }

}
