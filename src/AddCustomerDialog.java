import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddCustomerDialog extends JDialog {
    private JTextField firstNameField, lastNameField, addressField, cityField, stateField, zipField, phoneNumberField;

    public AddCustomerDialog(JFrame parent) {
        super(parent, "Add Customer", true);
        setupDialog();
    }

    private void setupDialog() {
        setSize(400, 400);
        setLocationRelativeTo(getParent());
        setLayout(new GridLayout(8, 2, 10, 10));

        // Initialize fields
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        addressField = new JTextField();
        cityField = new JTextField();
        stateField = new JTextField();
        zipField = new JTextField();
        phoneNumberField = new JTextField();

        // Add fields to the dialog
        add(new JLabel("First Name:"));
        add(firstNameField);
        add(new JLabel("Last Name:"));
        add(lastNameField);
        add(new JLabel("Address:"));
        add(addressField);
        add(new JLabel("City:"));
        add(cityField);
        add(new JLabel("State:"));
        add(stateField);
        add(new JLabel("ZIP Code:"));
        add(zipField);
        add(new JLabel("Phone Number:"));
        add(phoneNumberField);

        // Add buttons
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        // Use lambda expressions instead of anonymous inner classes
        addButton.addActionListener(e -> addCustomer());
        cancelButton.addActionListener(e -> dispose());

        add(addButton);
        add(cancelButton);
    }

    // Method to add a customer to the database
    private void addCustomer() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String address = addressField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String zip = zipField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() ||
                city.isEmpty() || state.isEmpty() || zip.isEmpty() || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/XE", "SYSTEM", "licet");

            int newCustomerId = getNextCustomerId(connection);

            String sql = "INSERT INTO F_CUSTOMERS (ID, FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, ZIP, PHONE_NUMBER) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, newCustomerId);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, city);
            preparedStatement.setString(6, state);
            preparedStatement.setInt(7, Integer.parseInt(zip));
            preparedStatement.setString(8, phoneNumber);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding customer!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private int getNextCustomerId(Connection connection) {
        int nextId = 1;
        String query = "SELECT NVL(MAX(ID), 0) + 1 FROM SYSTEM.F_CUSTOMERS";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                nextId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return nextId;
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        addressField.setText("");
        cityField.setText("");
        stateField.setText("");
        zipField.setText("");
        phoneNumberField.setText("");
    }
}
