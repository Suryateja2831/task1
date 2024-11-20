import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/ATM_System";
    private static final String USER = "user";
    private static final String PASSWORD = "";
    
    private Connection connection;

    public Database() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticateUser(String userId, String userPin) {
        String query = "SELECT * FROM Users WHERE user_id = ? AND user_pin = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userId);
            statement.setString(2, userPin);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Returns true if user exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getBalance(String userId) {
        String query = "SELECT balance FROM Users WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean deposit(String userId, double amount) {
        String updateBalance = "UPDATE Users SET balance = balance + ? WHERE user_id = ?";
        String insertTransaction = "INSERT INTO Transactions (user_id, transaction_type, amount) VALUES (?, 'Deposit', ?)";
        try (PreparedStatement balanceStatement = connection.prepareStatement(updateBalance);
             PreparedStatement transactionStatement = connection.prepareStatement(insertTransaction)) {

            connection.setAutoCommit(false); // Start transaction

            balanceStatement.setDouble(1, amount);
            balanceStatement.setString(2, userId);
            balanceStatement.executeUpdate();

            transactionStatement.setString(1, userId);
            transactionStatement.setDouble(2, amount);
            transactionStatement.executeUpdate();

            connection.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // Rollback in case of an error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public boolean withdraw(String userId, double amount) {
        String updateBalance = "UPDATE Users SET balance = balance - ? WHERE user_id = ? AND balance >= ?";
        String insertTransaction = "INSERT INTO Transactions (user_id, transaction_type, amount) VALUES (?, 'Withdraw', ?)";
        try (PreparedStatement balanceStatement = connection.prepareStatement(updateBalance);
             PreparedStatement transactionStatement = connection.prepareStatement(insertTransaction)) {

            connection.setAutoCommit(false); // Start transaction

            balanceStatement.setDouble(1, amount);
            balanceStatement.setString(2, userId);
            balanceStatement.setDouble(3, amount);
            int rowsUpdated = balanceStatement.executeUpdate();

            if (rowsUpdated > 0) {
                transactionStatement.setString(1, userId);
                transactionStatement.setDouble(2, amount);
                transactionStatement.executeUpdate();
                connection.commit(); // Commit transaction
                return true;
            } else {
                connection.rollback(); // Rollback if insufficient funds
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // Rollback in case of an error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public boolean transfer(String senderId, String recipientId, double amount) {
        String updateSenderBalance = "UPDATE Users SET balance = balance - ? WHERE user_id = ? AND balance >= ?";
        String updateRecipientBalance = "UPDATE Users SET balance = balance + ? WHERE user_id = ?";
        String insertTransaction = "INSERT INTO Transactions (user_id, transaction_type, amount) VALUES (?, 'Transfer', ?)";
        try (PreparedStatement senderStatement = connection.prepareStatement(updateSenderBalance);
             PreparedStatement recipientStatement = connection.prepareStatement(updateRecipientBalance);
             PreparedStatement transactionStatement = connection.prepareStatement(insertTransaction)) {

            connection.setAutoCommit(false); // Start transaction

            // Deduct from sender
            senderStatement.setDouble(1, amount);
            senderStatement.setString(2, senderId);
            senderStatement.setDouble(3, amount);
            int rowsUpdatedSender = senderStatement.executeUpdate();

            if (rowsUpdatedSender > 0) {
                // Credit recipient
                recipientStatement.setDouble(1, amount);
                recipientStatement.setString(2, recipientId);
                recipientStatement.executeUpdate();

                // Log transaction for sender
                transactionStatement.setString(1, senderId);
                transactionStatement.setDouble(2, amount);
                transactionStatement.executeUpdate();

                connection.commit(); // Commit transaction
                return true;
            } else {
                connection.rollback(); // Rollback if insufficient funds
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // Rollback in case of an error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public void showTransactionHistory(String userId) {
        String query = "SELECT transaction_type, amount, transaction_date FROM Transactions WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("Transaction History:");
            while (resultSet.next()) {
                System.out.printf("%s: $%.2f on %s%n",
                        resultSet.getString("transaction_type"),
                        resultSet.getDouble("amount"),
                        resultSet.getTimestamp("transaction_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
