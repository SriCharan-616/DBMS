import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class ManageRegularMenusDialog extends JDialog {
    private JTable regularMenusTable;
    private DefaultTableModel tableModel;
    private Connection connection;

    public ManageRegularMenusDialog(JFrame parent) {
        super(parent, "Manage Regular Menus", true);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Column names for the table
        String[] columnNames = {"Code", "Type", "Hours Served"};
        tableModel = new DefaultTableModel(columnNames, 0);
        regularMenusTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(regularMenusTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data from the database
        loadRegularMenus();

        // Button Listeners
        addButton.addActionListener(e -> addRegularMenu());
        editButton.addActionListener(e -> editRegularMenu());
        deleteButton.addActionListener(e -> deleteRegularMenu());
    }

    // Load Regular Menus from the database into JTable
    private void loadRegularMenus() {
        try {
            // Establish Database Connection
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.18:1521:LICETORCL", "exam1", "licet");

            // Fetch data from F_REGULAR_MENUS
            String query = "SELECT * FROM F_REGULAR_MENUS";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Clear existing rows
            tableModel.setRowCount(0);

            // Add rows to table model
            while (resultSet.next()) {
                String code = resultSet.getString("CODE");
                String type = resultSet.getString("TYPE");
                String hoursServed = resultSet.getString("HOURS_SERVED");

                tableModel.addRow(new Object[]{code, type, hoursServed});
            }

            resultSet.close();
            statement.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add Regular Menu
    private void addRegularMenu() {
        JTextField codeField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField hoursField = new JTextField();

        Object[] message = {
                "Code:", codeField,
                "Type:", typeField,
                "Hours Served:", hoursField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Regular Menu", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String code = codeField.getText().trim();
            String type = typeField.getText().trim();
            String hours = hoursField.getText().trim();

            if (code.isEmpty() || type.isEmpty() || hours.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String query = "INSERT INTO F_REGULAR_MENUS (CODE, TYPE, HOURS_SERVED) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, code);
                preparedStatement.setString(2, type);
                preparedStatement.setString(3, hours);
                preparedStatement.executeUpdate();
                preparedStatement.close();

                JOptionPane.showMessageDialog(this, "Menu added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRegularMenus(); // Refresh table
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding menu: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Edit Regular Menu
    private void editRegularMenu() {
        int selectedRow = regularMenusTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a menu to edit!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String oldCode = (String) tableModel.getValueAt(selectedRow, 0);
        JTextField codeField = new JTextField((String) tableModel.getValueAt(selectedRow, 0));
        JTextField typeField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField hoursField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));

        Object[] message = {
                "Code:", codeField,
                "Type:", typeField,
                "Hours Served:", hoursField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Regular Menu", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newCode = codeField.getText().trim();
            String newType = typeField.getText().trim();
            String newHours = hoursField.getText().trim();

            if (newCode.isEmpty() || newType.isEmpty() || newHours.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String query = "UPDATE F_REGULAR_MENUS SET CODE = ?, TYPE = ?, HOURS_SERVED = ? WHERE CODE = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, newCode);
                preparedStatement.setString(2, newType);
                preparedStatement.setString(3, newHours);
                preparedStatement.setString(4, oldCode);
                preparedStatement.executeUpdate();
                preparedStatement.close();

                JOptionPane.showMessageDialog(this, "Menu updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRegularMenus(); // Refresh table
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating menu: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Delete Regular Menu
    private void deleteRegularMenu() {
        int selectedRow = regularMenusTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a menu to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String code = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this menu?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM F_REGULAR_MENUS WHERE CODE = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, code);
                preparedStatement.executeUpdate();
                preparedStatement.close();

                JOptionPane.showMessageDialog(this, "Menu deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRegularMenus(); // Refresh table
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting menu: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
