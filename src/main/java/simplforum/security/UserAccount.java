package simplforum.security;

import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import simplforum.models.Account;

/** Implementation of User class which contains an account associated with this user.
 */
public class UserAccount extends User {
    private final Account account;

    /** Creates a new user with account.
     * @param account The account associated with this user.
     */
    public UserAccount(Account account) {
        super(
            account.getName(), //user
            account.getPassword(), //pass
            !account.isDisabled(), // enabled
            true, // not expired
            true, // credentials not expired
            true, // not locked
            AccountTypeUtility.shortToStream(account.getType())
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        this.account = account;
    }

    /** Returns the account associated to this user.
     * @return The account.
     */
    public Account getAccount() {
        return account;
    }

}
