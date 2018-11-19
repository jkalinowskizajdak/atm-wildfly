package atm.domain.entity;

import java.util.UUID;

/**
 * @author Jakub Kalinowski-Zajdak
 */
public class Account {
	
	private UUID id;
	private String pin;
	private UUID ownerId;
	private long singleWithdrawLimit;
	private double balance;
	
	public Account() {
		
	}
	
	private Account(Builder builder) {
		id = builder.id;
		pin = builder.pin;
		ownerId = builder.ownerId;
		singleWithdrawLimit = builder.singleWithdrawLimit;
		balance = builder.balance;
	}
	
	public UUID getId() {
		return id;
	}
	
	public String getPin() {
		return pin;
	}
	
	public UUID getOwnerId() {
		return ownerId;
	}
	
	public long getSingleWithdrawLimit() {
		return singleWithdrawLimit;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		
		private UUID id;
		private String pin;
		private UUID ownerId;
		private long singleWithdrawLimit;
		private double balance;
		
		public Builder copy(Account account) {
			id = account.id;
			pin = account.pin;
			ownerId = account.ownerId;
			singleWithdrawLimit = account.singleWithdrawLimit;
			balance = account.balance;
			return this;
		}
		
		public Builder id(UUID pId) {
			id = pId;
			return this;
		}
		
		public Builder generateId() {
			id = UUID.randomUUID();
			return this;
		}
		
		public Builder pin(String pPin) {
			pin = pPin;
			return this;
		}
		
		public Builder ownerId(UUID pOwnerId) {
			ownerId = pOwnerId;
			return this;
		}
		
		public Builder singleWithdrawLimit(long pSingleWithdrawLimit) {
			singleWithdrawLimit = pSingleWithdrawLimit;
			return this;
		}
		
		public Builder balacne(double pBalance) {
			balance = pBalance;
			return this;
		}
		
		public Account build() {
			return new Account(this);
		}
		
	}
	
}
