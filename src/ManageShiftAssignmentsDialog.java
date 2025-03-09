import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageShiftAssignmentsDialog extends JDialog {
    private JTable shiftAssignmentsTable;
    private DefaultTableModel tableModel;
    private Connection connection;

    public ManageShiftAssignmentsDialog(JFrame parent) {
        super(parent, "Manage Shift Assignments", true);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Table Columns
        String[] columnNames = {"Code", "ID", "Shift Assign Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        shiftAssignmentsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(shiftAssignmentsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data from the database
        loadShiftAssignments();

        // Button Actions
        addButton.addActionListener(e -> addShiftAssignment());
        editButton.addActionListener(e -> editShiftAssignment());
        deleteButton.addActionListener(e -> deleteShiftAssignment());
    }

    // Load Shift Assignments from the database
    private void loadShiftAssignments() {
        try {
            // Establish Database Connection
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.18:1521:LICETORCL", "exam1", "licet");

            // Fetch data from F_SHIFT_ASSIGNMENTS
            String query = "SELECT * FROM F_SHIFT_ASSIGNMENTS";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Clear existing rows
            tableModel.setRowCount(0);

            // Add rows to table model
            while (resultSet.next()) {
                int code = resultSet.getInt("CODE");
                int id = resultSet.getInt("ID");
                Date shiftDate = resultSet.getDate("SHIFT_ASSIGN_DATE");

                tableModel.addRow(new Object[]{code, id, shiftDate});
            }

            resultSet.close();
            statement.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add Shift Assignment
    private void addShiftAssignment() {
        JTextField codeField = new JTextField();
        JTextField idField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");

        Object[] message = {
                "Code:", codeField,
                "Staff ID:", idField,
                "Shift Assign Date (YYYY-MM-DD):", dateField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Shift Assignment", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int code = Integer.parseInt(codeField.getText().trim());
                int id = Integer.parseInt(idField.getText().trim());
                String date = dateField.getText().trim();

                String query = "INSERT INTO F_SHIFT_ASSIGNMENTS (CODE, ID, SHIFT_ASSIGN_DATE) VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'))";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, code);
                preparedStatement.setInt(2, id);
                preparedStatement.setString(3, date);
                preparedStatement.executeUpdate();
                preparedStatement.close();

                JOptionPane.showMessageDialog(this, "Shift Assignment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadShiftAssignments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding shift assignment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Edit Shift Assignment
    private void editShiftAssignment() {
        int selectedRow = shiftAssignmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a shift assignment to edit!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int oldCode = (int) tableModel.getValueAt(selectedRow, 0);
        int oldId = (int) tableModel.getValueAt(selectedRow, 1);
        String oldDate = tableModel.getValueAt(selectedRow, 2).toString();

        JTextField codeField = new JTextField(String.valueOf(oldCode));
        JTextField idField = new JTextField(String.valueOf(oldId));
        JTextField dateField = new JTextField(oldDate);

        Object[] message = {
                "Code:", codeField,
                "Staff ID:", idField,
                "Shift Assign Date (YYYY-MM-DD):", dateField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Shift Assignment", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int newCode = Integer.parseInt(codeField.getText().trim());
                int newId = Integer.parseInt(idField.getText().trim());
                String newDate = dateField.getText().trim();

                String query = "UPDATE F_SHIFT_ASSIGNMENTS SET CODE = ?, ID = ?, SHIFT_ASSIGN_DATE = TO_DATE(?, 'YYYY-MM-DD') WHERE CODE = ? AND ID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, newCode);
                preparedStatement.setInt(2, newId);
                preparedStatement.setString(3, newDate);
                preparedStatement.setInt(4, oldCode);
                preparedStatement.setInt(5, oldId);
                preparedStatement.executeUpdate();
                preparedStatement.close();

                JOptionPane.showMessageDialog(this, "Shift Assignment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadShiftAssignments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating shift assignment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Delete Shift Assignment
    private void deleteShiftAssignment() {
        int selectedRow = shiftAssignmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a shift assignment to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int code = (int) tableModel.getValueAt(selectedRow, 0);
        int id = (int) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this shift assignment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM F_SHIFT_ASSIGNMENTS WHERE CODE = ? AND ID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, code);
                preparedStatement.setInt(2, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();

                JOptionPane.showMessageDialog(this, "Shift Assignment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadShiftAssignments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting shift assignment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
