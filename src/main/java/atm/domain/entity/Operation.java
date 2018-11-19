package atm.domain.entity;

/**
 * @author Jakub Kalinowski-Zajdak
 */
public class Operation {

    private final OperationType type;
    private final int amount;
    
    public Operation(OperationType pType, int pAmount) {
    	type = pType;
    	amount = pAmount;
    }
    
    public OperationType getOperationType() {
    	return type;
    }
    
    public int getAmount() {
    	return amount;
    }
	
}
