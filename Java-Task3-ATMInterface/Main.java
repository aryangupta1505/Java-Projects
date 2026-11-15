public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();

        // Pre-populated sample accounts for testing login & transfers
        bank.addAccount(new Account("1001", "1234", "Alice Johnson", 2500.00));
        bank.addAccount(new Account("1002", "5678", "Bob Smith", 1200.00));

        // Sample credentials hint for testing:
        // User ID: 1001 | PIN: 1234 (Balance: $2500.00)
        // User ID: 1002 | PIN: 5678 (Balance: $1200.00)

        ATM atm = new ATM(bank);
        atm.start();
    }
}