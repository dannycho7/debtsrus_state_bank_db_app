package gui.atm_panels;

import gui.*;
import models.*;

import java.awt.*;
import javax.swing.*;
import java.sql.*;

public class LoginPanel extends Panel {
    private Connection conn;

    private JTextField customer_id_field = new JTextField(10);
    private JTextField pin_field = new JTextField(5);

    public LoginPanel(Connection conn) {
        this.conn = conn;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(new JLabel("Customer Tax Id (9 digits):"));
        this.add(customer_id_field);
        this.add(new JLabel("Pin (4 digits):"));
        this.add(pin_field);
    }

    public void handleSubmit(ATMAppInterface app_interface) throws SQLException {
        String customer_id = customer_id_field.getText();
        String pin = pin_field.getText();
        if (Customer.find(conn, customer_id).verifyPin(pin)) {
            app_interface.changeLoggedInCustomer(customer_id);
            JOptionPane.showMessageDialog(null, "Successfully logged in.");
        } else {
            JOptionPane.showMessageDialog(null, "Incorrect Pin.");
        }
    }

    public static void openLoginDialog(Connection conn, ATMAppInterface app_interface) {
        LoginPanel login_pane = new LoginPanel(conn);
        int result = JOptionPane.showConfirmDialog(
                null,
                login_pane,
                "Login",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                login_pane.handleSubmit(app_interface);
            } catch (SQLException se) {
                se.printStackTrace();
                JOptionPane.showMessageDialog(null, "Something went wrong with Logging In.");
            }
        }
    }
}