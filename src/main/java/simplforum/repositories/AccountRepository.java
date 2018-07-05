package simplforum.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import simplforum.models.Account;

/** Repository for accounts.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    /** Retrieves an account by it's name, if one exist.
     * @param username The username of the account which is requested.
     * @return Optional that may or may not contain an account.
     */
    public Optional<Account> findByName(String username);
}
