package gui.atm_panels;

import bank_util.*;
import models.*;
import models.transaction.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.text.NumberFormat;

public class WriteCheckPanel extends Panel {
    private Connection conn;
    private String logged_in_customer_id;

    private JFormattedTextField check_account_id_field;
    private JFormattedTextField amount_field;

    public WriteCheckPanel(Connection conn, String logged_in_customer_id) {
        this.conn = conn;
        this.logged_in_customer_id = logged_in_customer_id;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        check_account_id_field = new JFormattedTextField(format);
        check_account_id_field.setColumns(10);
        amount_field = new JFormattedTextField(format);
        amount_field.setColumns(10);

        this.add(new JLabel("Check Account Id:"));
        this.add(check_account_id_field);
        this.add(new JLabel("Amount:"));
        this.add(amount_field);
    }

    public void handleSubmit() throws SQLException {
        int check_account_id = ((Number) check_account_id_field.getValue()).intValue();
        int amount = ((Number) amount_field.getValue()).intValue();
        TransactionFactory.createWriteCheck(
                conn,
                amount,
                logged_in_customer_id, // initiator
                check_account_id, // transactor
                false // should_commit
        );
    }

    public static void openWriteCheckDialog(Connection conn, String logged_in_customer_id) {
        WriteCheckPanel write_check_pane = new WriteCheckPanel(conn, logged_in_customer_id);
        int result = JOptionPane.showConfirmDialog(
                null,
                write_check_pane,
                "WriteCheck",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                write_check_pane.handleSubmit();
                conn.commit();
                JOptionPane.showMessageDialog(null, "Successfully WriteChecked money.");
            } catch (SQLException se) {
                se.printStackTrace();
                JDBCConnectionManager.rollbackConn(conn);
                JOptionPane.showMessageDialog(null, "Something went wrong when WriteChecking money to this account.");
            }
        }
    }
}