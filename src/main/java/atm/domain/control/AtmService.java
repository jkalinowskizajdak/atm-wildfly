package atm.domain.control;

import java.util.Collection;
import java.util.UUID;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import org.slf4j.Logger;
import com.google.common.collect.ImmutableList;
import atm.domain.entity.Account;
import atm.domain.entity.AccountsRepository;
import atm.domain.entity.Operation;
import atm.domain.entity.OperationType;

/**
 * @author Jakub Kalinowski-Zajdak
 */
@Singleton
public class AtmService {
	
	private static final int MIN_ATM_OPERATION_VALUE = 50;
	private static final ImmutableList<Integer> ALLOWED_ATM_OPERATION_VALUE = ImmutableList.of(MIN_ATM_OPERATION_VALUE,
			100, 200, 500, 1000, 2000);
	
	@Inject
	AccountsRepository accountsRepository;
	
	@Inject
	Logger logger;
	
	
	public double getBalance(UUID accountId, String pin) {
		Account account = getAccount(accountId, pin);
		logger.info( "Account " + accountId + " balance is " + account.getBalance());
		return account.getBalance();
	}

	@Lock(LockType.WRITE)
	public double performOperation(UUID accountId, String pin, Operation operation) {
		validateOperatin(operation);
		Account account = getAccount(accountId, pin);
		double balance = account.getBalance();
		if (OperationType.WITHDRAW.equals(operation.getOperationType())) {
			if(account.getSingleWithdrawLimit() < operation.getAmount()) {
				throwRuntimeExecption("Single withdraw limit has been exceeded " + account.getSingleWithdrawLimit());
			}
			if (balance < operation.getAmount()) {
				throwRuntimeExecption("There are no funds in account!!!");
			}
			balance -= operation.getAmount();
		} else if (OperationType.DEPOSIT.equals(operation.getOperationType())) {
			balance += operation.getAmount();
		}
		accountsRepository.updateAccount(Account.builder()
				.copy(account)
				.balacne(balance)
				.build());
		logger.info("Account balance updated value: " + balance);
		return balance;
	}
	
	public String addAccount(Account account) {
		accountsRepository.addAccount(account);
		return account.getId().toString();
	}

	@Lock(LockType.WRITE)
	public void delete(UUID accountId) {
		accountsRepository.delete(accountId);
	}
	
	public Collection<Account> getAllAccounts() {
		return accountsRepository.getAllAccounts();
	}
	
	public Collection<Account> getOwnerAccounts(UUID ownerId) {
		return accountsRepository.getAccountsByOwnerId(ownerId);
	}
	
	private void validatePin(Account account, String pin) {
		if (!isPinValid(account, pin)) {
			String errorMessage = "Pin for account " + account.getId() + " is invalid!!!";
			throwRuntimeExecption(errorMessage);
		}
		logger.info( "Pin for account" + account.getId() + " is valid.");
	}
	
	private boolean isPinValid(Account account, String pin) {
		return account.getPin().equals(pin);
	}

	private Account getAccount(UUID accountId, String pin) {
		Account account = accountsRepository.getAccountById(accountId)
				.orElseThrow(() -> new RuntimeException("Account " + accountId + " does not exist!!!"));
		validatePin(account, pin);
		logger.info("Get account " + accountId + " from repository");
		return account;
	}
	
	private void validateOperatin(Operation operation) {
		int operationAmount = operation.getAmount();
		if (!ALLOWED_ATM_OPERATION_VALUE.contains(operationAmount)) {
			String errorMessage = "Allowed values: " + ALLOWED_ATM_OPERATION_VALUE.toString();
			throwRuntimeExecption(errorMessage);
		} else if (operationAmount <= 0 && operationAmount % MIN_ATM_OPERATION_VALUE != 0) {
			String errorMessage = "Value should be multiple of: " + MIN_ATM_OPERATION_VALUE;
			throwRuntimeExecption(errorMessage);
		}
		logger.info("Operation type " + operation.getOperationType().toString() + " amount " + operation.getAmount());
	}
	
	private void throwRuntimeExecption(String errorMessage) {
		logger.error(errorMessage);
		throw new RuntimeException(errorMessage);
	}
}
