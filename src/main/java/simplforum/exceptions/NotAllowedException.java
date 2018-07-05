package simplforum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Exception for requests which the current user is not allowed to perform. This
 * could either occur because the user is not logged in or because he is not
 * authorized to perform the action.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotAllowedException extends RuntimeException {

    /** Constructs a new exception with a default message.
     */
    public NotAllowedException() {
        this("You are not allowed to perform the requested operation");
    }

    public NotAllowedException(String message) {
        super(message);
    }

}
