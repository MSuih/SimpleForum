package simplforum.security;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import simplforum.models.Account;
import simplforum.repositories.AccountRepository;

/** Implementation of user details service.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accoRepo;

    /** Retrieves the requested user from database.
     * @param username Name of the user to be retrieved.
     * @return UserAccount object containing the requested user.
     * @throws UsernameNotFoundException If the username does not exist.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accoRepo.findByName(username);
        if (!account.isPresent())
            throw new UsernameNotFoundException("The requested username could not be located.");
        return new UserAccount(account.get());
    }

}
