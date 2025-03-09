import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class ManageFoodItemsDialog extends JDialog {
    private JTable foodItemsTable;
    private DefaultTableModel tableModel;
    private JTextField foodNumberField, descriptionField, priceField, regularCodeField, promoCodeField;
    private Connection connection;

    public ManageFoodItemsDialog(JFrame parent) {
        super(parent, "Manage Food Items", true); // Ensure Modal Dialog
        setSize(800, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent); // Center on screen

        // UI Setup
        setupUI();

        // Database Setup
        connectToDatabase();
        loadFoodItemsFromDatabase();

        // Make the Dialog Visible
        setVisible(true);
    }

    private void setupUI() {
        String[] columnNames = {"Food Number", "Description", "Price", "Regular Code", "Promo Code"};
        tableModel = new DefaultTableModel(columnNames, 0);
        foodItemsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(foodItemsTable);
        foodItemsTable.setFillsViewportHeight(true);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Food Item Details"));

        foodNumberField = new JTextField();
        descriptionField = new JTextField();
        priceField = new JTextField();
        regularCodeField = new JTextField();
        promoCodeField = new JTextField();

        formPanel.add(new JLabel("Food Number:"));
        formPanel.add(foodNumberField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Regular Code:"));
        formPanel.add(regularCodeField);
        formPanel.add(new JLabel("Promo Code:"));
        formPanel.add(promoCodeField);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("➕ Add");
        JButton updateButton = new JButton("✏️ Update");
        JButton deleteButton = new JButton("🗑 Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Add Components to Dialog
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event Listeners
        addButton.addActionListener(e -> addFoodItem());
        updateButton.addActionListener(e -> updateFoodItem());
        deleteButton.addActionListener(e -> deleteFoodItem());

        // Table Row Selection Listener
        foodItemsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = foodItemsTable.getSelectedRow();
                if (selectedRow != -1) {
                    foodNumberField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    descriptionField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    priceField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    regularCodeField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    promoCodeField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });
    }

    private void connectToDatabase() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/XE", "SYSTEM", "licet");
            System.out.println("Database Connected Successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadFoodItemsFromDatabase() {
        try {
            String query = "SELECT * FROM F_FOOD_ITEMS";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("FOOD_ITEM_NUMBER"),
                        rs.getString("DESCRIPTION"),
                        rs.getDouble("PRICE"),
                        rs.getString("REGULAR_CODE"),
                        rs.getString("PROMO_CODE")
                });
            }
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addFoodItem() {
        try {
            String query = "INSERT INTO F_FOOD_ITEMS (FOOD_ITEM_NUMBER, DESCRIPTION, PRICE, REGULAR_CODE, PROMO_CODE) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);

            int foodNumber = Integer.parseInt(foodNumberField.getText());
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            String regularCode = regularCodeField.getText();
            String promoCode = promoCodeField.getText();

            stmt.setInt(1, foodNumber);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setString(4, regularCode);
            stmt.setString(5, promoCode);

            int status = stmt.executeUpdate();
            if (status > 0) {
                JOptionPane.showMessageDialog(this, "Food Item Added Successfully!");
                loadFoodItemsFromDatabase(); // Refresh table
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error Adding Food Item!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateFoodItem() {
        int selectedRow = foodItemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "UPDATE F_FOOD_ITEMS SET DESCRIPTION=?, PRICE=?, REGULAR_CODE=?, PROMO_CODE=? WHERE FOOD_ITEM_NUMBER=?";
            PreparedStatement stmt = connection.prepareStatement(query);

            int foodNumber = Integer.parseInt(foodNumberField.getText());
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            String regularCode = regularCodeField.getText();
            String promoCode = promoCodeField.getText();

            stmt.setString(1, description);
            stmt.setDouble(2, price);
            stmt.setString(3, regularCode);
            stmt.setString(4, promoCode);
            stmt.setInt(5, foodNumber);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Food Item Updated Successfully!");
            loadFoodItemsFromDatabase(); // Refresh table
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteFoodItem() {
        int selectedRow = foodItemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int foodNumber = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "DELETE FROM F_FOOD_ITEMS WHERE FOOD_ITEM_NUMBER=?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, foodNumber);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this food item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Food Item Deleted Successfully!");
                loadFoodItemsFromDatabase(); // Refresh table
            }
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
