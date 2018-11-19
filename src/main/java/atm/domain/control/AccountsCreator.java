package atm.domain.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import atm.domain.entity.Account;
import atm.domain.entity.AccountsRepository;

/**
 * @author Jakub Kalinowski-Zajdak
 */
@Startup
@Singleton
public class AccountsCreator {
    
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final String ACCOUNTS_FILE = "Accounts.json";
    private static final String DIRECTORY_NAME = "/accounts";
    private static final String DATA_DIR_PATH = System.getProperty("jboss.server.data.dir", "../data/");
    
    @Inject
    AccountsRepository accountsRepository;
    
    @Inject
    Logger logger;
    
    @PostConstruct
    void init() {
        createDirectory(getDictionaryPath());
    }

    public static String getDictionaryPath() {
        return DATA_DIR_PATH + DIRECTORY_NAME;
    }
    
    private void createDirectory(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdir();
            logger.info("Directory " + dirName + " was created.");
        }
        readAccountsFromFile();
    }
    
    private void readAccountsFromFile() {
    	 StringBuilder fileContent = new StringBuilder();
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(ACCOUNTS_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
            reader.close();
        } catch (Exception e) {
            logger.error("Problem during reading resource file: {}", ACCOUNTS_FILE, e);
            throw new RuntimeException("Problem by reading resource properties");
        }
        addAccounts(convertToObject(fileContent.toString()));
    }
    
    private AccountsPojo convertToObject(String result) {
    	AccountsPojo accounts;
        try {
            accounts = MAPPER.readValue(result, AccountsPojo.class);
        } catch (IOException e) {
            logger.error("Problem during reading resource file: {}", ACCOUNTS_FILE, e);
            throw new RuntimeException("Problem during reading resource file: + " + ACCOUNTS_FILE, e);
        }

        return accounts;
    }
    
    private void addAccounts(AccountsPojo accounts) {
    	accounts.getAccounts().forEach(this::addAccount);
    }
    
    private void addAccount(Account account) {
    	if (!accountsRepository.getAccountById(account.getId()).isPresent()) {
        	accountsRepository.addAccount(account);
    	} else {
    		logger.info("Account " + account.getId() + " exist and it would not be updated!");
    	}

    }
    
    public static class AccountsPojo {
    	
        private List<Account> accounts;
        
        public List<Account> getAccounts() {
            return accounts;
        }

        public void setAccounts(List<Account> accounts) {
            this.accounts = accounts;
        }
    }
    
}
