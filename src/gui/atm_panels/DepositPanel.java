package gui.atm_panels;

import bank_util.*;
import models.*;
import models.transaction.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.text.NumberFormat;

public class DepositPanel extends Panel {
    private Connection conn;
    private String logged_in_customer_id;

    private JFormattedTextField account_id_field;
    private JFormattedTextField amount_field;

    public DepositPanel(Connection conn, String logged_in_customer_id) {
        this.conn = conn;
        this.logged_in_customer_id = logged_in_customer_id;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        account_id_field = new JFormattedTextField(format);
        account_id_field.setColumns(10);
        amount_field = new JFormattedTextField(format);
        amount_field.setColumns(10);

        this.add(new JLabel("Account Id:"));
        this.add(account_id_field);
        this.add(new JLabel("Amount:"));
        this.add(amount_field);
    }

    public void handleSubmit() throws SQLException {
        int account_id = ((Number) account_id_field.getValue()).intValue();
        int amount = ((Number) amount_field.getValue()).intValue();
        TransactionFactory.createDeposit(
                conn,
                amount,
                logged_in_customer_id, // initiator
                account_id, // transactor
                false // should_commit
        );
    }

    public static void openDepositDialog(Connection conn, String logged_in_customer_id) {
        DepositPanel deposit_pane = new DepositPanel(conn, logged_in_customer_id);
        int result = JOptionPane.showConfirmDialog(
                null,
                deposit_pane,
                "Deposit",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                deposit_pane.handleSubmit();
                conn.commit();
                JOptionPane.showMessageDialog(null, "Successfully deposited money.");
            } catch (SQLException se) {
                se.printStackTrace();
                JDBCConnectionManager.rollbackConn(conn);
                JOptionPane.showMessageDialog(null, "Something went wrong when depositing money to this account.");
            }
        }
    }
}