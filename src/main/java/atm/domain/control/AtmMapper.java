package atm.domain.control;

import java.util.UUID;

import atm.domain.boundary.dto.AccountCreateDTO;
import atm.domain.boundary.dto.AccountDTO;
import atm.domain.boundary.dto.OperationDTO;
import atm.domain.entity.Account;
import atm.domain.entity.Operation;
import atm.domain.entity.OperationType;

/**
 * @author Jakub Kalinowski-Zajdak
 */
public class AtmMapper {
	
	private AtmMapper() {
		
	}
	
	public static Operation mapOperationDTOtoOperationEntity(OperationDTO operationDTO) {
		OperationType operationType = OperationType.NONE;
		if (OperationType.WITHDRAW.getValue().equals(operationDTO.type)) {
			operationType = OperationType.WITHDRAW;
		} else 	if (OperationType.DEPOSIT.getValue().equals(operationDTO.type)) {
			operationType = OperationType.DEPOSIT;
		}
		return new Operation(operationType, operationDTO.amount);
	}
	
	public static Account mapAccountDTOtoAccountEntity(AccountCreateDTO accountDTO) {
		return Account.builder()
				.generateId()
				.balacne(accountDTO.balance)
				.ownerId(UUID.fromString(accountDTO.ownerId))
				.singleWithdrawLimit(accountDTO.singleWithdrawLimit)
				.pin(accountDTO.pin)
				.build();
	}
	
	public static AccountDTO mapAccountEntitytoAccountDTO(Account account) {
		return new AccountDTO(account.getId().toString(), account.getPin(), account.getOwnerId().toString(),
				account.getSingleWithdrawLimit(), account.getBalance());
	}

}
