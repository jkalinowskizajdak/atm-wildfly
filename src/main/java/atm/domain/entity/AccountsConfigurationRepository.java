package atm.domain.entity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.slf4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import atm.domain.control.AccountsCreator;

/**
 * @author Jakub Kalinowski-Zajdak
 */
public class AccountsConfigurationRepository implements AccountsRepository {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Inject
    Logger logger;

	public Optional<Account> getAccountById(UUID id) {
		String configuration = loadConfigurationById(id.toString());
		if (!Strings.isNullOrEmpty(configuration)) {
			return Optional.ofNullable(convertToObject(configuration));
		}
		return Optional.empty();
	}

	public Collection<Account> getAccountsByOwnerId(UUID ownerId) {
		List<Account> accounts = Lists.newLinkedList(getAllAccounts());
		accounts.removeIf(account -> !ownerId.equals(account.getOwnerId()));
		return accounts;
	}

	public Collection<Account> getAllAccounts() {
		File[] configurations = new File(AccountsCreator.getDictionaryPath()).listFiles();
		if (Objects.isNull(configurations)) {
			return ImmutableList.of();
		}
		List<Account> accounts = Arrays.asList(configurations).stream()
				.map(conf -> convertToObject(
						loadConfigurationById(conf.getName().substring(0, conf.getName().lastIndexOf(".")))))
				.collect(Collectors.toList());
		return accounts;
	}

	public void addAccount(Account account) {
		try {
			MAPPER.writeValue(new File(getConfigurationPath(account.getId().toString())), account);
			logger.info("Account " + account.getId() + " was created.");
		} catch (IOException e) {
			logger.error("Unable to save account " + account.getId());
		}
	}

	public void delete(UUID id) {
		File configuration = new File(getConfigurationPath(id.toString()));
		if (configuration.exists()) {
			configuration.delete();
			logger.info("Account " + id + " was deleted.");
		}
	}

	public void updateAccount(Account account) {
		delete(account.getId());
		addAccount(account);
	}
	
    private String getConfigurationPath(String fileName) {
        return AccountsCreator.getDictionaryPath() + "/" + fileName + ".json";
    }	
    
	private Account convertToObject(String configuration) {
		Account account = null;
		try {
			account = MAPPER.readValue(configuration, Account.class);
		} catch (IOException e) {
			logger.error("Unable to convert " + configuration + " to Object " + e);
		}
		
		return account;
	}
	
	private String loadConfigurationById(String id) {
		String result = null;
		try {
			result = new String(Files.readAllBytes(Paths.get(getConfigurationPath(id))));
		} catch (IOException e) {
			logger.error("Unable to load file for configuration with id : " + id);
		}
		return result;
	}


}
