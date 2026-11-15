import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class OnlineReservationSystem extends JFrame {

    // -------------------------------------------------------------------------
    // MySQL Database Configuration (Update DB_USER and DB_PASS as needed)
    // -------------------------------------------------------------------------
    private static final String DB_URL = "jdbc:mysql://localhost:3306/reservation_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "your_mysql_password";

    private static final Map<String, String> TRAIN_CATALOG = new HashMap<>();

    static {
        TRAIN_CATALOG.put("12001", "Shatabdi Express");
        TRAIN_CATALOG.put("12951", "Rajdhani Express");
        TRAIN_CATALOG.put("22436", "Vande Bharat Express");
        TRAIN_CATALOG.put("12626", "Kerala Express");
        TRAIN_CATALOG.put("12259", "Duronto Express");
    }

    private CardLayout cardLayout;
    private JPanel mainContainer;

    // Reservation components
    private JTextField tfPassengerName, tfTrainNumber, tfTrainName, tfDateOfJourney, tfSource, tfDestination;
    private JComboBox<String> cbClassType;

    // Cancellation components
    private JTextField tfCancelPnr;
    private JTextArea taBookingDetails;
    private JButton btnConfirmCancel;
    private String currentFetchedPnr = null;

    public OnlineReservationSystem() {
        verifyConnectionAndInit();

        setTitle("Online Train Reservation System (MySQL)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(createLoginPanel(), "LOGIN");
        mainContainer.add(createDashboardPanel(), "DASHBOARD");

        add(mainContainer);
    }

    // -------------------------------------------------------------------------
    // Database Connection Helper & Initialization
    // -------------------------------------------------------------------------
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private void verifyConnectionAndInit() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(50) PRIMARY KEY, " +
                    "password VARCHAR(50) NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS reservations (" +
                    "pnr VARCHAR(20) PRIMARY KEY, " +
                    "passenger_name VARCHAR(100) NOT NULL, " +
                    "train_number VARCHAR(20) NOT NULL, " +
                    "train_name VARCHAR(100) NOT NULL, " +
                    "class_type VARCHAR(50) NOT NULL, " +
                    "journey_date DATE NOT NULL, " +
                    "source_stn VARCHAR(100) NOT NULL, " +
                    "dest_stn VARCHAR(100) NOT NULL)");

            // MySQL syntax: INSERT IGNORE
            stmt.execute("INSERT IGNORE INTO users (username, password) VALUES ('admin', 'admin123')");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "MySQL Connection Failed!\nCheck that MySQL Server is running, database 'reservation_db' exists, and DB_PASS is correct.\n\nError: " + e.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    // -------------------------------------------------------------------------
    // Login Screen
    // -------------------------------------------------------------------------
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Online Reservation System - Login", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        JTextField tfUsername = new JTextField(15);
        gbc.gridx = 1;
        panel.add(tfUsername, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField pfPassword = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(pfPassword, gbc);

        JButton btnLogin = new JButton("Login");
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);

        JLabel lblHint = new JLabel("Default: username=admin, password=admin123", JLabel.CENTER);
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        gbc.gridy = 4;
        panel.add(lblHint, gbc);

        btnLogin.addActionListener(e -> {
            String user = tfUsername.getText().trim();
            String pass = new String(pfPassword.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (authenticate(user, pass)) {
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + user + ".");
                tfUsername.setText("");
                pfPassword.setText("");
                cardLayout.show(mainContainer, "DASHBOARD");
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private boolean authenticate(String username, String password) {
        String sql = "SELECT username FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Query Error: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Main Dashboard (Tabbed Pane: Book vs Cancel)
    // -------------------------------------------------------------------------
    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Book Ticket", createReservationTab());
        tabbedPane.addTab("Cancel Ticket", createCancellationTab());

        dashboard.add(tabbedPane, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> cardLayout.show(mainContainer, "LOGIN"));
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.add(btnLogout);
        dashboard.add(topBar, BorderLayout.NORTH);

        return dashboard;
    }

    // -------------------------------------------------------------------------
    // Tab 1: Reservation Form
    // -------------------------------------------------------------------------
    private JPanel createReservationTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfPassengerName = new JTextField(18);
        tfTrainNumber = new JTextField(18);
        tfTrainName = new JTextField(18);
        tfTrainName.setEditable(false);
        tfTrainName.setBackground(new Color(235, 235, 235));

        cbClassType = new JComboBox<>(new String[]{"1A (First AC)", "2A (Second AC)", "3A (Third AC)", "SL (Sleeper)", "CC (AC Chair Car)"});
        tfDateOfJourney = new JTextField(18); // format: YYYY-MM-DD
        tfSource = new JTextField(18);
        tfDestination = new JTextField(18);

        // Auto-populate train name based on train number lookup
        tfTrainNumber.getDocument().addDocumentListener(new DocumentListener() {
            private void updateTrainName() {
                String trainNo = tfTrainNumber.getText().trim();
                tfTrainName.setText(TRAIN_CATALOG.getOrDefault(trainNo, "Train not recognized"));
            }
            public void insertUpdate(DocumentEvent e) { updateTrainName(); }
            public void removeUpdate(DocumentEvent e) { updateTrainName(); }
            public void changedUpdate(DocumentEvent e) { updateTrainName(); }
        });

        int row = 0;
        addFormRow(panel, gbc, row++, "Passenger Name:", tfPassengerName);
        addFormRow(panel, gbc, row++, "Train Number (e.g., 12001, 12951):", tfTrainNumber);
        addFormRow(panel, gbc, row++, "Train Name (Auto):", tfTrainName);
        addFormRow(panel, gbc, row++, "Class Type:", cbClassType);
        addFormRow(panel, gbc, row++, "Date of Journey (YYYY-MM-DD):", tfDateOfJourney);
        addFormRow(panel, gbc, row++, "Source Station:", tfSource);
        addFormRow(panel, gbc, row++, "Destination Station:", tfDestination);

        JButton btnBook = new JButton("Book Reservation");
        btnBook.setBackground(new Color(40, 167, 69));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(btnBook, gbc);

        btnBook.addActionListener(this::handleBooking);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void handleBooking(ActionEvent e) {
        String name = tfPassengerName.getText().trim();
        String trainNo = tfTrainNumber.getText().trim();
        String trainName = tfTrainName.getText().trim();
        String classType = (String) cbClassType.getSelectedItem();
        String date = tfDateOfJourney.getText().trim();
        String src = tfSource.getText().trim();
        String dest = tfDestination.getText().trim();

        // 1. Check for empty fields
        if (name.isEmpty() || trainNo.isEmpty() || date.isEmpty() || src.isEmpty() || dest.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Validate train number is numeric
        if (!trainNo.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Train Number must contain numeric digits only.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Validate Date format (YYYY-MM-DD)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        java.sql.Date sqlDate;
        try {
            java.util.Date parsedDate = sdf.parse(date);
            sqlDate = new java.sql.Date(parsedDate.getTime());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD (e.g., 2026-11-15).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Generate 8-digit unique PNR
        String pnr = "PNR" + (10000000 + new Random().nextInt(90000000));

        String sql = "INSERT INTO reservations (pnr, passenger_name, train_number, train_name, class_type, journey_date, source_stn, dest_stn) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pnr);
            pstmt.setString(2, name);
            pstmt.setString(3, trainNo);
            pstmt.setString(4, trainName);
            pstmt.setString(5, classType);
            pstmt.setDate(6, sqlDate);
            pstmt.setString(7, src);
            pstmt.setString(8, dest);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                String summary = String.format(
                        "Reservation Successful!\n\n" +
                        "PNR: %s\n" +
                        "Passenger: %s\n" +
                        "Train: %s (%s)\n" +
                        "Class: %s\n" +
                        "Date: %s\n" +
                        "Route: %s -> %s\n",
                        pnr, name, trainName, trainNo, classType, date, src, dest
                );
                JOptionPane.showMessageDialog(this, summary, "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);

                tfPassengerName.setText("");
                tfTrainNumber.setText("");
                tfTrainName.setText("");
                tfDateOfJourney.setText("");
                tfSource.setText("");
                tfDestination.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Insert Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // Tab 2: Cancellation Form
    // -------------------------------------------------------------------------
    private JPanel createCancellationTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Enter PNR Number:"));
        tfCancelPnr = new JTextField(12);
        topPanel.add(tfCancelPnr);
        JButton btnFetch = new JButton("Fetch Booking");
        topPanel.add(btnFetch);
        panel.add(topPanel, BorderLayout.NORTH);

        taBookingDetails = new JTextArea();
        taBookingDetails.setEditable(false);
        taBookingDetails.setFont(new Font("Monospaced", Font.PLAIN, 13));
        taBookingDetails.setBorder(BorderFactory.createTitledBorder("Booking Details"));
        panel.add(new JScrollPane(taBookingDetails), BorderLayout.CENTER);

        btnConfirmCancel = new JButton("Confirm Cancellation");
        btnConfirmCancel.setEnabled(false);
        btnConfirmCancel.setBackground(new Color(220, 53, 69));
        btnConfirmCancel.setForeground(Color.WHITE);
        btnConfirmCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(btnConfirmCancel, BorderLayout.SOUTH);

        btnFetch.addActionListener(e -> fetchBooking());
        btnConfirmCancel.addActionListener(e -> cancelBooking());

        return panel;
    }

    private void fetchBooking() {
        String pnr = tfCancelPnr.getText().trim();
        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a PNR number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT * FROM reservations WHERE pnr = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pnr);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentFetchedPnr = pnr;
                String details = String.format(
                        "=========================================\n" +
                        "           BOOKING DETAILS               \n" +
                        "=========================================\n" +
                        "PNR Number    : %s\n" +
                        "Passenger     : %s\n" +
                        "Train Number  : %s\n" +
                        "Train Name    : %s\n" +
                        "Class         : %s\n" +
                        "Journey Date  : %s\n" +
                        "From Station  : %s\n" +
                        "To Station    : %s\n" +
                        "=========================================",
                        rs.getString("pnr"),
                        rs.getString("passenger_name"),
                        rs.getString("train_number"),
                        rs.getString("train_name"),
                        rs.getString("class_type"),
                        rs.getDate("journey_date").toString(),
                        rs.getString("source_stn"),
                        rs.getString("dest_stn")
                );
                taBookingDetails.setText(details);
                btnConfirmCancel.setEnabled(true);
            } else {
                currentFetchedPnr = null;
                taBookingDetails.setText("No reservation found matching PNR: " + pnr);
                btnConfirmCancel.setEnabled(false);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        if (currentFetchedPnr == null) return;

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel booking for " + currentFetchedPnr + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM reservations WHERE pnr = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, currentFetchedPnr);
                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Booking " + currentFetchedPnr + " has been successfully cancelled.");
                    taBookingDetails.setText("");
                    tfCancelPnr.setText("");
                    currentFetchedPnr = null;
                    btnConfirmCancel.setEnabled(false);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OnlineReservationSystem app = new OnlineReservationSystem();
            app.setVisible(true);
        });
    }
}