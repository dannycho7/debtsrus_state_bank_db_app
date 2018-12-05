package gui.atm_panels;

import bank_util.*;
import models.*;
import models.transaction.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.text.NumberFormat;

public class WirePanel extends Panel {
    private Connection conn;
    private String logged_in_customer_id;

    private JFormattedTextField transactor_account_id_field;
    private JFormattedTextField operand_account_id_field;
    private JFormattedTextField amount_field;

    public WirePanel(Connection conn, String logged_in_customer_id) {
        this.conn = conn;
        this.logged_in_customer_id = logged_in_customer_id;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        transactor_account_id_field = new JFormattedTextField(format);
        transactor_account_id_field.setColumns(10);
        operand_account_id_field = new JFormattedTextField(format);
        operand_account_id_field.setColumns(10);
        amount_field = new JFormattedTextField(format);
        amount_field.setColumns(10);

        this.add(new JLabel("From (Check/Savings) Account Id:"));
        this.add(transactor_account_id_field);
        this.add(new JLabel("To (Check/Savings) Account Id:"));
        this.add(operand_account_id_field);
        this.add(new JLabel("Amount:"));
        this.add(amount_field);
    }

    public void handleSubmit() throws SQLException {
        int transactor_account_id = ((Number) transactor_account_id_field.getValue()).intValue();
        int operand_account_id = ((Number) operand_account_id_field.getValue()).intValue();
        int amount = ((Number) amount_field.getValue()).intValue();
        TransactionFactory.createWire(
                conn,
                amount,
                logged_in_customer_id, // initiator
                transactor_account_id, // transactor
                operand_account_id, // operand
                false // should_commit
        );
    }

    public static void openWireDialog(Connection conn, String logged_in_customer_id) {
        WirePanel wire_pane = new WirePanel(conn, logged_in_customer_id);
        int result = JOptionPane.showConfirmDialog(
                null,
                wire_pane,
                "Wire",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                wire_pane.handleSubmit();
                conn.commit();
                JOptionPane.showMessageDialog(null, "Successfully executed Wire.");
            } catch (SQLException se) {
                se.printStackTrace();
                JDBCConnectionManager.rollbackConn(conn);
                JOptionPane.showMessageDialog(null, "Something went wrong when executing Wire on this account.");
            }
        }
    }
}