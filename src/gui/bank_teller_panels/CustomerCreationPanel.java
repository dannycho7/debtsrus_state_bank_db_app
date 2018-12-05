package gui.bank_teller_panels;

import bank_util.*;
import models.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class CustomerCreationPanel extends Panel {
    private Connection conn;
    private JTextField tax_id_field = new JTextField(9);
    private JTextField name_field = new JTextField(20);
    private JTextField address_field = new JTextField(20);
    private JTextField pin_field = new JTextField(4);

    public CustomerCreationPanel(Connection conn) {
        this.conn = conn;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(new JLabel("Tax Id (9 digits):"));
        this.add(tax_id_field);
        this.add(new JLabel("Name:"));
        this.add(name_field);
        this.add(new JLabel("Address:"));
        this.add(address_field);
        this.add(new JLabel("Pin (4 digits):"));
        this.add(pin_field);
    }

    public void handleSubmit() throws SQLException {
        String tax_id = tax_id_field.getText();
        String name_id = name_field.getText();
        String address_id = address_field.getText();
        String pin_id = pin_field.getText();
        Customer.create(
                conn,
                tax_id,
                name_id,
                address_id,
                pin_id,
                false // should_commit
        );
    }

    public static void openCustomerCreationDialog(Connection conn) {
        CustomerCreationPanel customer_create_pane = new CustomerCreationPanel(conn);
        int result = JOptionPane.showConfirmDialog(
                null,
                customer_create_pane,
                "Create Customer",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                customer_create_pane.handleSubmit();
                JOptionPane.showMessageDialog(null, "Successfully created customer.");
            } catch (SQLException se) {
                se.printStackTrace();
                JDBCConnectionManager.rollbackConn(conn);
                JOptionPane.showMessageDialog(null, "Something went wrong when creating the customer.");
            }
        }
    }
}