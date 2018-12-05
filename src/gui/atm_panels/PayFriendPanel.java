package gui.atm_panels;

import bank_util.*;
import models.*;
import models.transaction.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.text.NumberFormat;

public class PayFriendPanel extends Panel {
    private Connection conn;
    private String logged_in_customer_id;

    private JFormattedTextField from_pocket_account_id_field;
    private JFormattedTextField to_pocket_account_id_field;
    private JFormattedTextField amount_field;

    public PayFriendPanel(Connection conn, String logged_in_customer_id) {
        this.conn = conn;
        this.logged_in_customer_id = logged_in_customer_id;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        from_pocket_account_id_field = new JFormattedTextField(format);
        from_pocket_account_id_field.setColumns(10);
        to_pocket_account_id_field= new JFormattedTextField(format);
        to_pocket_account_id_field.setColumns(10);
        amount_field = new JFormattedTextField(format);
        amount_field.setColumns(10);

        this.add(new JLabel("From Pocket Account Id:"));
        this.add(from_pocket_account_id_field);
        this.add(new JLabel("To Pocket Account Id:"));
        this.add(to_pocket_account_id_field);
        this.add(new JLabel("Amount:"));
        this.add(amount_field);
    }

    public void handleSubmit() throws SQLException {
        int from_pocket_account_id = ((Number) from_pocket_account_id_field.getValue()).intValue();
        int to_pocket_account_id = ((Number) to_pocket_account_id_field.getValue()).intValue();
        int amount = ((Number) amount_field.getValue()).intValue();
        TransactionFactory.createPayFriend(
                conn,
                amount,
                logged_in_customer_id, // initiator
                from_pocket_account_id, // transactor
                to_pocket_account_id, // operand
                false // should_commit
        );
    }

    public static void openPayFriendDialog(Connection conn, String logged_in_customer_id) {
        PayFriendPanel pay_friend_pane = new PayFriendPanel(conn, logged_in_customer_id);
        int result = JOptionPane.showConfirmDialog(
                null,
                pay_friend_pane,
                "PayFriend",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                pay_friend_pane.handleSubmit();
                conn.commit();
                JOptionPane.showMessageDialog(null, "Successfully executed Pay-Friend.");
            } catch (SQLException se) {
                se.printStackTrace();
                JDBCConnectionManager.rollbackConn(conn);
                JOptionPane.showMessageDialog(null, "Something went wrong when executing Pay-Friend on this account.");
            }
        }
    }
}