package gui.atm_panels;

import bank_util.*;
import models.*;
import models.transaction.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.text.NumberFormat;

public class CollectPanel extends Panel {
    private Connection conn;
    private String logged_in_customer_id;

    private JFormattedTextField pocket_account_id_field;
    private JFormattedTextField check_savings_account_id_field;
    private JFormattedTextField amount_field;

    public CollectPanel(Connection conn, String logged_in_customer_id) {
        this.conn = conn;
        this.logged_in_customer_id = logged_in_customer_id;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        pocket_account_id_field = new JFormattedTextField(format);
        pocket_account_id_field.setColumns(10);
        check_savings_account_id_field = new JFormattedTextField(format);
        check_savings_account_id_field.setColumns(10);
        amount_field = new JFormattedTextField(format);
        amount_field.setColumns(10);

        this.add(new JLabel("Pocket Account Id:"));
        this.add(pocket_account_id_field);
        this.add(new JLabel("Check/Savings Account Id:"));
        this.add(check_savings_account_id_field);
        this.add(new JLabel("Amount:"));
        this.add(amount_field);
    }

    public void handleSubmit() throws SQLException {
        int pocket_account_id = ((Number) pocket_account_id_field.getValue()).intValue();
        int check_savings_account_id = ((Number) check_savings_account_id_field.getValue()).intValue();
        int amount = ((Number) amount_field.getValue()).intValue();
        TransactionFactory.createCollect(
                conn,
                amount,
                logged_in_customer_id, // initiator
                pocket_account_id, // transactor
                check_savings_account_id, // operand
                false // should_commit
        );
    }

    public static void openCollectDialog(Connection conn, String logged_in_customer_id) {
        CollectPanel collect_pane = new CollectPanel(conn, logged_in_customer_id);
        int result = JOptionPane.showConfirmDialog(
                null,
                collect_pane,
                "Collect",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                collect_pane.handleSubmit();
                conn.commit();
                JOptionPane.showMessageDialog(null, "Successfully executed Collect.");
            } catch (SQLException se) {
                se.printStackTrace();
                JDBCConnectionManager.rollbackConn(conn);
                JOptionPane.showMessageDialog(null, "Something went wrong when executing Collect on this account.");
            }
        }
    }
}