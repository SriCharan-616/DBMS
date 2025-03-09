import java.sql.*;

public class TableExistenceChecker {
    // List of tables to check
    private static final String[] TABLES = {
        "F_CUSTOMERS", "F_PROMOTIONAL_MENUS", "F_REGULAR_MENUS",
        "F_FOOD_ITEMS", "F_STAFFS", "F_ORDERS", "F_ORDER_LINES",
        "F_SHIFTS", "F_SHIFT_ASSIGNMENTS"
    };

    public static void main(String[] args) {
        // Database connection details (update with your credentials)
        String url = "jdbc:oracle:thin:@localhost:1521:XE"; // Update with your DB details
        String username = "SYSTEM";  // Update with your username
        String password = "licet";  // Update with your password

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to the database.");
            
            for (String table : TABLES) {
                if (checkTableExists(conn, table)) {
                    System.out.println("✅ Table exists: " + table);
                } else {
                    System.out.println("❌ Table does NOT exist: " + table);
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to check if a table exists in the database
    public static boolean checkTableExists(Connection conn, String tableName) throws SQLException {
        String query = "SELECT TABLE_NAME FROM ALL_TABLES WHERE TABLE_NAME = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, tableName.toUpperCase()); // Ensure case insensitivity
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // If there is a result, the table exists
            }
        }
    }
}
