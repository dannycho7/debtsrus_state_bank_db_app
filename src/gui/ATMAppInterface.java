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
    private String logged_in_customer_id;
    private JLabel logged_in_user_label;

    public ATMAppInterface(Connection conn) {
        super();
        this.conn = conn;
        logged_in_customer_id = "";
        logged_in_user_label = new JLabel("Not logged in");
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(new JLabel("ATM App Interface"));
        this.add(logged_in_user_label);
        this.add(getLogInButton());
        this.add(getDepositButton());
        this.add(getTopUpButton());
        this.add(getWithdrawalButton());
        this.add(getPurchaseButton());
        this.add(getTransferButton());
        this.add(getCollectButton());
        this.add(getPayFriendButton());
        this.add(getWireButton());
    }

    public void changeLoggedInCustomer(String customer_id) {
        this.logged_in_customer_id = customer_id;
        this.logged_in_user_label.setText("Logged In Customer: " + this.logged_in_customer_id);
    }

    public JButton getLogInButton() {
        ATMAppInterface app_interface = this;
        JButton deposit_btn = new JButton("Login");
        deposit_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LoginPanel.openLoginDialog(conn, app_interface);
            }
        });
        return deposit_btn;
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
    public JButton getWithdrawalButton() {
        JButton withdrawal_btn = new JButton("Withdrawal");
        withdrawal_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                WithdrawalPanel.openWithdrawalDialog(conn, logged_in_customer_id);
            }
        });
        return withdrawal_btn;
    }
    public JButton getPurchaseButton() {
        JButton purchase_btn = new JButton("Purchase");
        purchase_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PurchasePanel.openPurchaseDialog(conn, logged_in_customer_id);
            }
        });
        return purchase_btn;
    }
    public JButton getTransferButton() {
        JButton transfer_btn = new JButton("Transfer");
        transfer_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TransferPanel.openTransferDialog(conn, logged_in_customer_id);
            }
        });
        return transfer_btn;
    }
    public JButton getCollectButton() {
        JButton collect_btn = new JButton("Collect");
        collect_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CollectPanel.openCollectDialog(conn, logged_in_customer_id);
            }
        });
        return collect_btn;
    }
    public JButton getPayFriendButton() {
        JButton pay_friend_btn = new JButton("PayFriend");
        pay_friend_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PayFriendPanel.openPayFriendDialog(conn, logged_in_customer_id);
            }
        });
        return pay_friend_btn;
    }
    public JButton getWireButton() {
        JButton wire_btn = new JButton("Wire");
        wire_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                WirePanel.openWireDialog(conn, logged_in_customer_id);
            }
        });
        return wire_btn;
    }
}