import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewOrdersDialog extends JDialog {
    private JTable ordersTable;
    private DefaultTableModel tableModel;

    public ViewOrdersDialog(JFrame parent) {
        super(parent, "View Orders", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);

        // Column names matching your table
        String[] columnNames = {"Order Number", "Order Date", "Order Total", "Customer ID", "Staff ID"};
        tableModel = new DefaultTableModel(columnNames, 0);
        ordersTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        ordersTable.setFillsViewportHeight(true);
        
        add(scrollPane, BorderLayout.CENTER);

        // Load orders from the database
        loadOrders();
    }

    private void loadOrders() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Load Oracle JDBC Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Establish database connection
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/XE", "SYSTEM", "licet");

            // SQL query to fetch order data
            String sql = "SELECT ORDER_NUMBER, ORDER_DATE, ORDER_TOTAL, CUST_ID, STAFF_ID FROM F_ORDERS";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            // Populate table with data from database
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("ORDER_NUMBER"),
                        resultSet.getDate("ORDER_DATE"),  // DATE type
                        resultSet.getDouble("ORDER_TOTAL"),  // NUMBER(8,2)
                        resultSet.getInt("CUST_ID"),
                        resultSet.getInt("STAFF_ID")
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
