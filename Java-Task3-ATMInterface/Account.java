import java.util.ArrayList;
import java.util.List;

public class Account {
    private final String userId;
    private final String pin;
    private final String holderName;
    private double balance;
    private final List<Transaction> transactionHistory;

    public Account(String userId, String pin, String holderName, double initialBalance) {
        this.userId = userId;
        this.pin = pin;
        this.holderName = holderName;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    public void deposit(double amount) {
        balance += amount;
        transactionHistory.add(new Transaction("DEPOSIT", amount, balance, "Cash Deposit"));
    }

    public boolean withdraw(double amount) {
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance, "ATM Withdrawal"));
        return true;
    }

    public boolean transferTo(Account recipient, double amount) {
        if (amount > balance) {
            return false;
        }
        this.balance -= amount;
        recipient.balance += amount;

        this.transactionHistory.add(new Transaction(
                "TRANSFER OUT", amount, this.balance, "To Account: " + recipient.getUserId()));
        recipient.transactionHistory.add(new Transaction(
                "TRANSFER IN", amount, recipient.balance, "From Account: " + this.userId));
        return true;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
}