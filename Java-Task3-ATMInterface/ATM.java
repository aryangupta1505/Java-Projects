import java.util.List;
import java.util.Scanner;

public class ATM {
    private final Bank bank;
    private final Scanner scanner;

    public ATM(Bank bank) {
        this.bank = bank;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("==========================================");
        System.out.println("          WELCOME TO JAVA ATM             ");
        System.out.println("==========================================");

        Account currentAccount = login();
        if (currentAccount == null) {
            System.out.println("\nAccess Denied: Maximum login attempts (3) exceeded. Card locked.");
            return;
        }

        System.out.printf("\nLogin Successful! Welcome, %s (Balance: $%.2f)\n",
                currentAccount.getHolderName(), currentAccount.getBalance());

        showMenu(currentAccount);
    }

    private Account login() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            System.out.print("\nEnter User ID: ");
            String userId = scanner.nextLine().trim();

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account account = bank.authenticateUser(userId, pin);
            if (account != null) {
                return account;
            }

            attempts++;
            System.out.printf("Invalid User ID or PIN. Attempts remaining: %d\n", (MAX_ATTEMPTS - attempts));
        }
        return null;
    }

    private void showMenu(Account account) {
        boolean running = true;

        while (running) {
            System.out.println("\n------------------------------------------");
            System.out.printf("ATM MAIN MENU | Current Balance: $%.2f\n", account.getBalance());
            System.out.println("------------------------------------------");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.print("Select an option (1-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    displayTransactionHistory(account);
                    break;
                case "2":
                    handleWithdraw(account);
                    break;
                case "3":
                    handleDeposit(account);
                    break;
                case "4":
                    handleTransfer(account);
                    break;
                case "5":
                    System.out.println("\nThank you for using Java ATM. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 5.");
            }
        }
    }

    private void displayTransactionHistory(Account account) {
        System.out.println("\n--- Transaction History ---");
        List<Transaction> history = account.getTransactionHistory();

        if (history.isEmpty()) {
            System.out.println("No transactions recorded in this session.");
        } else {
            for (Transaction tx : history) {
                System.out.println(tx);
            }
        }
    }

    private void handleWithdraw(Account account) {
        double amount = readPositiveAmount("Enter amount to withdraw: $");
        if (amount <= 0) return;

        if (account.withdraw(amount)) {
            System.out.printf("Withdrawal of $%.2f successful. Updated Balance: $%.2f\n",
                    amount, account.getBalance());
        } else {
            System.out.println("Insufficient Funds! Your current balance is $" + String.format("%.2f", account.getBalance()));
        }
    }

    private void handleDeposit(Account account) {
        double amount = readPositiveAmount("Enter amount to deposit: $");
        if (amount <= 0) return;

        account.deposit(amount);
        System.out.printf("Deposit of $%.2f successful. Updated Balance: $%.2f\n",
                amount, account.getBalance());
    }

    private void handleTransfer(Account account) {
        System.out.print("Enter recipient User ID: ");
        String recipientId = scanner.nextLine().trim();

        if (recipientId.equals(account.getUserId())) {
            System.out.println("Cannot transfer funds to your own account.");
            return;
        }

        Account recipient = bank.findAccount(recipientId);
        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        double amount = readPositiveAmount("Enter amount to transfer: $");
        if (amount <= 0) return;

        if (account.transferTo(recipient, amount)) {
            System.out.printf("Successfully transferred $%.2f to %s (%s). Updated Balance: $%.2f\n",
                    amount, recipient.getHolderName(), recipientId, account.getBalance());
        } else {
            System.out.println("Insufficient Funds! Your current balance is $" + String.format("%.2f", account.getBalance()));
        }
    }

    private double readPositiveAmount(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                System.out.println("Amount must be greater than zero.");
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric amount entered.");
            return -1;
        }
    }
}