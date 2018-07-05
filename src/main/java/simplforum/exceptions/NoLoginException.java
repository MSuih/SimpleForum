package simplforum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Exception for when a request requires logging in. This usually occurs if the user
 * is considered to be authenticated but no user account could be retrieved from the
 * current session.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class NoLoginException extends RuntimeException {

    /** Constructs a new exception with a default message.
     */
    public NoLoginException() {
        this("No login information found. Please try logging out and back in.");
    }

    public NoLoginException(String message) {
        super(message);
    }
}
