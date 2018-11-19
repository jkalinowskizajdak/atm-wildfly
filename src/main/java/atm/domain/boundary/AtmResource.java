package atm.domain.boundary;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import atm.domain.boundary.dto.AccountCreateDTO;
import atm.domain.boundary.dto.AccountDTO;
import atm.domain.boundary.dto.OperationDTO;
import atm.domain.control.AtmMapper;
import atm.domain.control.AtmService;

/**
 * @author Jakub Kalinowski-Zajdak
 */
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Stateless
@Path("/")
public class AtmResource {
	
	private static final String ACCOUNT_ID_PARAM = "accountId";
	private static final String OWNER_ID_PARAM = "ownerId";
	private static final String PIN_PARAM = "pin";
	
	@Inject
	AtmService atmService;
	
    @GET
    @Path("/{"+ ACCOUNT_ID_PARAM + "}/balance")
    public double getBalance(@PathParam(ACCOUNT_ID_PARAM) UUID accountId, 
            @QueryParam(PIN_PARAM) String pin) {
    	validatePinParam(pin);
        return atmService.getBalance(accountId, pin);
    }
    
    @PUT
    @Path("/{"+ ACCOUNT_ID_PARAM + "}")
    public Response withdraw(@PathParam(ACCOUNT_ID_PARAM) UUID accountId, 
            @DefaultValue("") @QueryParam(PIN_PARAM) String pin,
            OperationDTO operationDTO) {
    	validatePinParam(pin);
    	double balance = atmService.performOperation(accountId, pin, AtmMapper.mapOperationDTOtoOperationEntity(operationDTO));
        return Response.ok()
        		.entity(balance)
        		.build();
    }
    
    @POST
    @Path("/")
    public Response addAccount(AccountCreateDTO accountDTO) {
    	String accountId = atmService.addAccount(AtmMapper.mapAccountDTOtoAccountEntity(accountDTO));
        return Response.ok()
        		.entity(accountId)
        		.build();
    }
    
	@GET
	@Path("/accounts")
	public List<AccountDTO> getAllAccounts() {
		return Lists.newLinkedList(atmService.getAllAccounts().stream().map(AtmMapper::mapAccountEntitytoAccountDTO)
				.collect(Collectors.toList()));
	}
	
	@GET
    @Path("/{"+ OWNER_ID_PARAM + "}/accounts")
	public List<AccountDTO> getAllAccounts(@PathParam(OWNER_ID_PARAM) UUID ownerId ) {
		return Lists.newLinkedList(atmService.getOwnerAccounts(ownerId).stream().map(AtmMapper::mapAccountEntitytoAccountDTO)
				.collect(Collectors.toList()));
	}
    
    @DELETE
    @Path("/{"+ ACCOUNT_ID_PARAM + "}")
    public void deleteAccount(@PathParam(ACCOUNT_ID_PARAM) UUID accountId) {
    	atmService.delete(accountId);
    }
    
    private void validatePinParam(String pin) {
        if (Strings.isNullOrEmpty(pin)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Pin is empty!!!")
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .build());
        }
    }

}
