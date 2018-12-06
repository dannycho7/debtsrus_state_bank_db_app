package gui.bank_teller_panels;

import gui.*;
import models.*;

import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.text.NumberFormat;

public class DatePanel extends Panel {
    private Connection conn;

    private JFormattedTextField year_field;
    private JFormattedTextField month_field;
    private JFormattedTextField day_field;

    public DatePanel(Connection conn) {
        this.conn = conn;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        year_field = new JFormattedTextField(format);
        year_field.setColumns(10);
        month_field = new JFormattedTextField(format);
        month_field.setColumns(10);
        day_field = new JFormattedTextField(format);
        day_field.setColumns(10);

        this.add(new JLabel("Year:"));
        this.add(year_field);
        this.add(new JLabel("Month:"));
        this.add(month_field);
        this.add(new JLabel("Day:"));
        this.add(day_field);
    }

    public void handleSubmit(BankTellerInterface bank_interface) throws SQLException {
        int year = ((Number) year_field.getValue()).intValue();
        int month = ((Number) month_field.getValue()).intValue();
        int day = ((Number) day_field.getValue()).intValue();
        bank_interface.changeDate(year, month, day);
    }

    public static void openDateDialog(Connection conn, BankTellerInterface bank_interface) {
        DatePanel date_pane = new DatePanel(conn);
        int result = JOptionPane.showConfirmDialog(
                null,
                date_pane,
                "Date",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                date_pane.handleSubmit(bank_interface);
            } catch (SQLException se) {
                se.printStackTrace();
                JOptionPane.showMessageDialog(null, "Something went wrong with changing the date.");
            }
        }
    }
}