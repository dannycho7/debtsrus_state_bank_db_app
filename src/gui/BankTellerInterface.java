package gui;

import gui.bank_teller_panels.*;

import bank_util.*;
import models.*;
import models.account.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class BankTellerInterface extends JPanel{
    private Connection conn;

    public BankTellerInterface(Connection conn) {
        this.conn = conn;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(new JLabel("Bank Teller Interface"));
        this.add(getAddAccountButton());
        this.add(getWriteCheckButton());
    }

    public JButton getAddAccountButton() {
        JButton add_account_btn = new JButton("Add Account");
        add_account_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AccountCreationPanel.openAccountCreationDialog(conn);
            }
        });
        return add_account_btn;
    }

    public JButton getWriteCheckButton() {
        JButton write_check_btn = new JButton("Write-Check");
        write_check_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                WriteCheckPanel.openWriteCheckDialog(conn);
            }
        });
        return write_check_btn;
    }
}