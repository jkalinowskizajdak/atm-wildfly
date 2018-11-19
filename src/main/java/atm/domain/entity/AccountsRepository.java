package atm.domain.entity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Jakub Kalinowski-Zajdak
 */
public interface AccountsRepository {

	Optional<Account> getAccountById(UUID id); 
	Collection<Account> getAccountsByOwnerId(UUID ownerId);
	Collection<Account> getAllAccounts();
	void addAccount(Account account);
	void updateAccount(Account account);
	void delete(UUID id);
}
