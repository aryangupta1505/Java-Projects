import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String type;
    private final double amount;
    private final double postBalance;
    private final String details;
    private final String timestamp;

    public Transaction(String type, double amount, double postBalance, String details) {
        this.type = type;
        this.amount = amount;
        this.postBalance = postBalance;
        this.details = details;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getPostBalance() {
        return postBalance;
    }

    public String getDetails() {
        return details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-14s | Amount: $%8.2f | Balance: $%9.2f | %s",
                timestamp, type, amount, postBalance, details);
    }
}