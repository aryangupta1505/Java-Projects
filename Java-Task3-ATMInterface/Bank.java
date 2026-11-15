import java.util.HashMap;
import java.util.Map;

public class Bank {
    private final Map<String, Account> accounts;

    public Bank() {
        this.accounts = new HashMap<>();
    }

    public void addAccount(Account account) {
        accounts.put(account.getUserId(), account);
    }

    public Account authenticateUser(String userId, String pin) {
        Account acc = accounts.get(userId);
        if (acc != null && acc.validatePin(pin)) {
            return acc;
        }
        return null;
    }

    public Account findAccount(String userId) {
        return accounts.get(userId);
    }
}