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
}