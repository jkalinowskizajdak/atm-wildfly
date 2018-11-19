package atm.domain.entity;

/**
 * @author Jakub Kalinowski-Zajdak
 */
public enum OperationType {
	
	NONE("none"), WITHDRAW("withdraw"), DEPOSIT("deposit");
	
	private String value;
	
	private OperationType(String pValue) {
		value = pValue;
	}
	
	public String getValue() {
		return value;
	}
	
}
