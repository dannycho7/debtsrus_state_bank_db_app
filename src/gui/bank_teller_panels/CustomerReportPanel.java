package gui.bank_teller_panels;

import bank_util.*;
import models.*;
import models.account.*;
import models.transaction.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

public class CustomerReportPanel extends Panel {
    private Connection conn;

    private JTextField customer_id_field = new JTextField(9);

    public CustomerReportPanel(Connection conn) {
        this.conn = conn;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(new JLabel("Customer Tax Id (9 digits):"));
        this.add(customer_id_field);
    }

    public void handleSubmit() throws SQLException {
        String customer_id = customer_id_field.getText();
        JTextArea monthly_statement = new JTextArea();
        monthly_statement.setEditable(false);
        Customer customer = Customer.find(conn, customer_id);
        ArrayList<AccountBase> accounts = customer.genAccounts(conn);
        int sum_balance = 0;
        for (AccountBase account : accounts) {
            sum_balance += account.getBalance();
            monthly_statement.append(String.format("* Account Id: %d\n", account.getAccountId()));
            monthly_statement.append(
                    String.format("** Account Status: %s\n", account.isClosed() ? "Closed" : "Open")
            );
        }
        JScrollPane scroll_pane = new JScrollPane(monthly_statement);
        scroll_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_pane.setPreferredSize(new Dimension(500, 500));
        JOptionPane.showMessageDialog(
                null,
                scroll_pane,
                "Customer Report",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    public static void openCustomerReportDialog(Connection conn) {
        CustomerReportPanel customer_report_pane = new CustomerReportPanel(conn);
        int result = JOptionPane.showConfirmDialog(
                null,
                customer_report_pane,
                "CustomerReport",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                customer_report_pane.handleSubmit();
            } catch (SQLException se) {
                se.printStackTrace();
                JOptionPane.showMessageDialog(null, "Something went wrong with CustomerReport.");
            }
        }
    }
}