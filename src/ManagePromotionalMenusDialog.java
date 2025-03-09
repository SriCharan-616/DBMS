import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ManagePromotionalMenusDialog extends JDialog {
    private JTable promotionalMenusTable;
    private DefaultTableModel tableModel;
    private Connection connection;

    public ManagePromotionalMenusDialog(JFrame parent) {
        super(parent, "Manage Promotional Menus", true);
        setSize(700, 500);
        setLayout(new BorderLayout());

        // Database Connection
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/XEPDB1", "SYSTEM", "licet");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Table Setup
        String[] columnNames = {"Code", "Name", "Start Date", "End Date", "Giveaway"};
        tableModel = new DefaultTableModel(columnNames, 0);
        promotionalMenusTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(promotionalMenusTable);
        promotionalMenusTable.setFillsViewportHeight(true);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("➕ Add");
        JButton updateButton = new JButton("✏️ Update");
        JButton deleteButton = new JButton("🗑️ Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Add components to frame
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load Data from Database
        loadPromotionalMenus();

        // Button Actions
        addButton.addActionListener(e -> addPromotionalMenu());
        updateButton.addActionListener(e -> updatePromotionalMenu());
        deleteButton.addActionListener(e -> deletePromotionalMenu());
    }

    private void loadPromotionalMenus() {
        try {
            tableModel.setRowCount(0); // Clear previous data
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM F_PROMOTIONAL_MENUS");

            while (rs.next()) {
                String code = rs.getString("CODE");
                String name = rs.getString("NAME");
                Date startDate = rs.getDate("START_DATE");
                Date endDate = rs.getDate("END_DATE");
                String giveaway = rs.getString("GIVE_AWAY");

                tableModel.addRow(new Object[]{code, name, startDate, endDate, giveaway});
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPromotionalMenu() {
        JTextField codeField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField startDateField = new JTextField();
        JTextField endDateField = new JTextField();
        JTextField giveawayField = new JTextField();

        Object[] inputFields = {
            "Code:", codeField,
            "Name:", nameField,
            "Start Date (YYYY-MM-DD):", startDateField,
            "End Date (YYYY-MM-DD):", endDateField,
            "Giveaway:", giveawayField
        };

        int result = JOptionPane.showConfirmDialog(this, inputFields, "Add Promotional Menu", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String sql = "INSERT INTO F_PROMOTIONAL_MENUS (CODE, NAME, START_DATE, END_DATE, GIVE_AWAY) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, codeField.getText());
                stmt.setString(2, nameField.getText());
                stmt.setDate(3, Date.valueOf(startDateField.getText()));
                stmt.setDate(4, Date.valueOf(endDateField.getText()));
                stmt.setString(5, giveawayField.getText());

                stmt.executeUpdate();
                stmt.close();

                JOptionPane.showMessageDialog(this, "New menu added!");
                loadPromotionalMenus();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding menu!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updatePromotionalMenu() {
        int selectedRow = promotionalMenusTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a menu to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String oldCode = tableModel.getValueAt(selectedRow, 0).toString();
        JTextField nameField = new JTextField(tableModel.getValueAt(selectedRow, 1).toString());
        JTextField startDateField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
        JTextField endDateField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
        JTextField giveawayField = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());

        Object[] inputFields = {
            "Name:", nameField,
            "Start Date (YYYY-MM-DD):", startDateField,
            "End Date (YYYY-MM-DD):", endDateField,
            "Giveaway:", giveawayField
        };

        int result = JOptionPane.showConfirmDialog(this, inputFields, "Update Promotional Menu", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String sql = "UPDATE F_PROMOTIONAL_MENUS SET NAME=?, START_DATE=?, END_DATE=?, GIVE_AWAY=? WHERE CODE=?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, nameField.getText());
                stmt.setDate(2, Date.valueOf(startDateField.getText()));
                stmt.setDate(3, Date.valueOf(endDateField.getText()));
                stmt.setString(4, giveawayField.getText());
                stmt.setString(5, oldCode);

                stmt.executeUpdate();
                stmt.close();

                JOptionPane.showMessageDialog(this, "Menu updated!");
                loadPromotionalMenus();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating menu!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePromotionalMenu() {
        int selectedRow = promotionalMenusTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a menu to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = tableModel.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this menu?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM F_PROMOTIONAL_MENUS WHERE CODE=?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, code);

                stmt.executeUpdate();
                stmt.close();

                JOptionPane.showMessageDialog(this, "Menu deleted!");
                loadPromotionalMenus();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting menu!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
