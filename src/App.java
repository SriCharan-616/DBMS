// Ensure the correct package based on your directory structure

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class App {
    public static void main(String[] args) {
        // Create Main Frame
        JFrame frame = new JFrame("Restaurant Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen mode
        frame.setLayout(new GridBagLayout());

        // Panel for Centered UI
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        panel.setBackground(Color.WHITE); // Minimalist background
        panel.setBorder(BorderFactory.createEmptyBorder(50, 200, 50, 200)); // Padding

        // Create Buttons
        JButton addCustomerButton = createButton("➕ Add Customer", "AddCustomer");
        JButton viewOrdersButton = createButton("📦 View Orders", "ViewOrders");
        JButton managePromotionalMenusButton = createButton("📢 Manage Promotional Menus", "ManagePromotionalMenus");
        JButton manageRegularMenusButton = createButton("📜 Manage Regular Menus", "ManageRegularMenus");
        JButton manageFoodItemsButton = createButton("🍽️ Manage Food Items", "ManageFoodItems");
        JButton manageShiftAssignmentsButton = createButton("⏳ Manage Shift Assignments", "ManageShiftAssignments");

        // Add Action Listeners for Opening Dialogs
        addCustomerButton.addActionListener(event -> handleButtonClick(event, frame));
        viewOrdersButton.addActionListener(event -> handleButtonClick(event, frame));
        managePromotionalMenusButton.addActionListener(event -> handleButtonClick(event, frame));
        manageRegularMenusButton.addActionListener(event -> handleButtonClick(event, frame));
        manageFoodItemsButton.addActionListener(event -> handleButtonClick(event, frame));
        manageShiftAssignmentsButton.addActionListener(event -> handleButtonClick(event, frame));

        // Add Buttons to Panel
        panel.add(addCustomerButton);
        panel.add(viewOrdersButton);
        panel.add(managePromotionalMenusButton);
        panel.add(manageRegularMenusButton);
        panel.add(manageFoodItemsButton);
        panel.add(manageShiftAssignmentsButton);

        // Add Panel to Frame
        frame.add(panel);
        frame.getContentPane().setBackground(Color.WHITE); // Background color
        frame.setVisible(true);
    }

    // Method to Create Styled Buttons
    private static JButton createButton(String text, String actionCommand) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setBackground(new Color(50, 50, 50)); // Dark Gray
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Button Padding
        button.setActionCommand(actionCommand); // Set Action Command
        return button;
    }

    // Handle Button Clicks and Open Corresponding Dialogs
    private static void handleButtonClick(ActionEvent event, JFrame parentFrame) {
        String command = event.getActionCommand();
        System.out.println("Button clicked: " + command);

        switch (command) {
            case "AddCustomer":
                new AddCustomerDialog(parentFrame).setVisible(true);
                break;
            case "ViewOrders":
                new ViewOrdersDialog(parentFrame).setVisible(true);
                break;
            case "ManagePromotionalMenus":
                new ManagePromotionalMenusDialog(parentFrame).setVisible(true);
                break;
            case "ManageRegularMenus":
                new ManageRegularMenusDialog(parentFrame).setVisible(true);
                break;
            case "ManageFoodItems":
                new ManageFoodItemsDialog(parentFrame).setVisible(true);
                break;
            case "ManageShiftAssignments":
                new ManageShiftAssignmentsDialog(parentFrame).setVisible(true);
                break;
            default:
                System.out.println("Unknown button action: " + command);
        }
    }
}
