import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class ViewOrdersDialog extends JDialog {
    private JTable ordersTable;

    public ViewOrdersDialog(JFrame parent) {
        super(parent, "View Orders", true);
        setSize(600, 400);

        // Create a table to display orders
        String[] columnNames = {"Order Number", "Order Date", "Order Total", "Customer ID", "Staff ID"};
        Object[][] data = {}; // You can fetch data from your database and populate this array

        ordersTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        ordersTable.setFillsViewportHeight(true);

        add(scrollPane, BorderLayout.CENTER);
    }
}
