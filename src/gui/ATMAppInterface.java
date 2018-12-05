package gui;

import gui.atm_panels.*;

import bank_util.*;
import models.*;
import models.account.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class ATMAppInterface extends JPanel{
    private Connection conn;
    private final String logged_in_customer_id = "123456789"; // TODO: Remove later for authentication

    public ATMAppInterface(Connection conn) {
        super();
        this.conn = conn;
        this.add(new JLabel("ATM App Interface"));
        this.add(getDepositButton());
        this.add(getTopUpButton());
    }

    public JButton getDepositButton() {
        JButton deposit_btn = new JButton("Deposit");
        deposit_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DepositPanel.openDepositDialog(conn, logged_in_customer_id);
            }
        });
        return deposit_btn;
    }
    public JButton getTopUpButton() {
        JButton top_up_btn = new JButton("Top-Up");
        top_up_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TopUpPanel.openTopUpDialog(conn, logged_in_customer_id);
            }
        });
        return top_up_btn;
    }
}