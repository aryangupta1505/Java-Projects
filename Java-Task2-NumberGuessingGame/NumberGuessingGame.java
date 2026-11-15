import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumberGuessingGame extends JFrame {

    private int targetNumber;
    private int attemptsUsed;
    private int maxAttempts;
    private int maxRange;
    private int currentRound = 0;

    private final List<String> scoreHistory = new ArrayList<>();
    private final Random random = new Random();

    // GUI Components
    private JComboBox<String> cbDifficulty;
    private JLabel lblRangePrompt;
    private JLabel lblAttemptCounter;
    private JLabel lblFeedback;
    private JTextField tfGuess;
    private JButton btnGuess;
    private JButton btnPlayAgain;
    private JTextArea taScoreboard;

    public NumberGuessingGame() {
        setTitle("Number Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 460);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        startNewRound();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ---------------------------------------------------------------------
        // Top Panel: Difficulty Selector & Round Info
        // ---------------------------------------------------------------------
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        diffPanel.add(new JLabel("Difficulty:"));
        cbDifficulty = new JComboBox<>(new String[]{
                "Medium (1–100, 7 attempts)",
                "Easy (1–50, 10 attempts)",
                "Hard (1–200, 5 attempts)"
        });
        cbDifficulty.addActionListener(e -> startNewRound());
        diffPanel.add(cbDifficulty);

        lblAttemptCounter = new JLabel("Round 1 | Attempts: 0 / 7", JLabel.CENTER);
        lblAttemptCounter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAttemptCounter.setForeground(new Color(0, 102, 204));

        topPanel.add(diffPanel);
        topPanel.add(lblAttemptCounter);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ---------------------------------------------------------------------
        // Center Panel: Gameplay Input & Feedback
        // ---------------------------------------------------------------------
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEtchedBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblRangePrompt = new JLabel("Guess a number between 1 and 100:", JLabel.CENTER);
        lblRangePrompt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(lblRangePrompt, gbc);

        tfGuess = new JTextField(10);
        tfGuess.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tfGuess.setHorizontalAlignment(JTextField.CENTER);
        tfGuess.addActionListener(this::handleGuess); // Allow pressing Enter key
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridx = 0;
        centerPanel.add(tfGuess, gbc);

        btnGuess = new JButton("Submit Guess");
        btnGuess.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuess.setBackground(new Color(40, 167, 69));
        btnGuess.setForeground(Color.WHITE);
        btnGuess.addActionListener(this::handleGuess);
        gbc.gridx = 1;
        centerPanel.add(btnGuess, gbc);

        lblFeedback = new JLabel("Enter your guess to start!", JLabel.CENTER);
        lblFeedback.setFont(new Font("Segoe UI", Font.BOLD, 15));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        centerPanel.add(lblFeedback, gbc);

        btnPlayAgain = new JButton("Play Again");
        btnPlayAgain.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPlayAgain.setBackground(new Color(0, 123, 255));
        btnPlayAgain.setForeground(Color.WHITE);
        btnPlayAgain.setVisible(false);
        btnPlayAgain.addActionListener(e -> startNewRound());
        gbc.gridy = 3;
        centerPanel.add(btnPlayAgain, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ---------------------------------------------------------------------
        // Bottom Panel: Multi-Round Score History
        // ---------------------------------------------------------------------
        taScoreboard = new JTextArea(6, 30);
        taScoreboard.setEditable(false);
        taScoreboard.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(taScoreboard);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Round Score Summary"));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void startNewRound() {
        currentRound++;
        attemptsUsed = 0;

        // Apply selected difficulty settings
        String selected = (String) cbDifficulty.getSelectedItem();
        if (selected != null && selected.startsWith("Easy")) {
            maxRange = 50;
            maxAttempts = 10;
        } else if (selected != null && selected.startsWith("Hard")) {
            maxRange = 200;
            maxAttempts = 5;
        } else {
            // Default: Medium
            maxRange = 100;
            maxAttempts = 7;
        }

        targetNumber = random.nextInt(maxRange) + 1;

        // Reset UI state
        lblRangePrompt.setText("Guess a number between 1 and " + maxRange + ":");
        updateAttemptDisplay();
        lblFeedback.setText("New round started! Make a guess.");
        lblFeedback.setForeground(Color.DARK_GRAY);

        tfGuess.setText("");
        tfGuess.setEnabled(true);
        btnGuess.setEnabled(true);
        btnPlayAgain.setVisible(false);
        tfGuess.requestFocus();
    }

    private void handleGuess(ActionEvent e) {
        String input = tfGuess.getText().trim();
        int guess;

        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid whole number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            tfGuess.selectAll();
            tfGuess.requestFocus();
            return;
        }

        if (guess < 1 || guess > maxRange) {
            JOptionPane.showMessageDialog(this, "Please enter a number between 1 and " + maxRange + ".", "Out of Range", JOptionPane.WARNING_MESSAGE);
            tfGuess.selectAll();
            tfGuess.requestFocus();
            return;
        }

        attemptsUsed++;
        updateAttemptDisplay();

        if (guess == targetNumber) {
            lblFeedback.setText("Correct! The number was " + targetNumber + ".");
            lblFeedback.setForeground(new Color(0, 153, 51));
            endRound(true);
        } else if (attemptsUsed >= maxAttempts) {
            lblFeedback.setText("You Lost! The number was " + targetNumber + ".");
            lblFeedback.setForeground(Color.RED);
            endRound(false);
        } else if (guess > targetNumber) {
            lblFeedback.setText("Too High!");
            lblFeedback.setForeground(new Color(204, 102, 0));
        } else {
            lblFeedback.setText("Too Low!");
            lblFeedback.setForeground(new Color(0, 102, 204));
        }

        tfGuess.setText("");
        tfGuess.requestFocus();
    }

    private void updateAttemptDisplay() {
        lblAttemptCounter.setText(String.format("Round %d | Attempts: %d / %d", currentRound, attemptsUsed, maxAttempts));
    }

    private void endRound(boolean won) {
        tfGuess.setEnabled(false);
        btnGuess.setEnabled(false);
        btnPlayAgain.setVisible(true);

        // Record round summary
        String record;
        if (won) {
            record = String.format("Round %d — guessed in %d %s",
                    currentRound, attemptsUsed, (attemptsUsed == 1 ? "attempt" : "attempts"));
        } else {
            record = String.format("Round %d — You Lost! (Number: %d)", currentRound, targetNumber);
        }
        scoreHistory.add(record);
        refreshScoreboard();

        // Prompt Play Again dialog
        int choice = JOptionPane.showConfirmDialog(
                this,
                (won ? "Correct! You guessed it in " + attemptsUsed + " attempts.\n"
                     : "You Lost! The secret number was " + targetNumber + ".\n")
                + "Would you like to play another round?",
                "Round Over",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            startNewRound();
        }
    }

    private void refreshScoreboard() {
        StringBuilder sb = new StringBuilder();
        for (String entry : scoreHistory) {
            sb.append(entry).append("\n");
        }
        taScoreboard.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NumberGuessingGame game = new NumberGuessingGame();
            game.setVisible(true);
        });
    }
}