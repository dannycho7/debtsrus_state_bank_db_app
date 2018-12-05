package gui.atm_panels;

import bank_util.*;
import models.*;
import models.transaction.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.text.NumberFormat;

public class PurchasePanel extends Panel {
    private Connection conn;
    private String logged_in_customer_id;

    private JFormattedTextField pocket_account_id_field;
    private JFormattedTextField amount_field;

    public PurchasePanel(Connection conn, String logged_in_customer_id) {
        this.conn = conn;
        this.logged_in_customer_id = logged_in_customer_id;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        pocket_account_id_field = new JFormattedTextField(format);
        pocket_account_id_field.setColumns(10);
        amount_field = new JFormattedTextField(format);
        amount_field.setColumns(10);

        this.add(new JLabel("Pocket Account Id:"));
        this.add(pocket_account_id_field);
        this.add(new JLabel("Amount:"));
        this.add(amount_field);
    }

    public void handleSubmit() throws SQLException {
        int pocket_account_id = ((Number) pocket_account_id_field.getValue()).intValue();
        int amount = ((Number) amount_field.getValue()).intValue();
        TransactionFactory.createPurchase(
                conn,
                amount,
                logged_in_customer_id, // initiator
                pocket_account_id, // transactor
                false // should_commit
        );
    }

    public static void openPurchaseDialog(Connection conn, String logged_in_customer_id) {
        PurchasePanel purchase_pane = new PurchasePanel(conn, logged_in_customer_id);
        int result = JOptionPane.showConfirmDialog(
                null,
                purchase_pane,
                "Purchase",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                purchase_pane.handleSubmit();
                conn.commit();
                JOptionPane.showMessageDialog(null, "Successfully purchased money.");
            } catch (SQLException se) {
                se.printStackTrace();
                JDBCConnectionManager.rollbackConn(conn);
                JOptionPane.showMessageDialog(null, "Something went wrong when purchased money from this account.");
            }
        }
    }
}