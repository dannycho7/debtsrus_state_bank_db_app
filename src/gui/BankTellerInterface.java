package gui;

import gui.bank_teller_panels.*;

import bank_util.*;
import models.*;
import models.transaction.*;
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
        this.add(getAddInterestButton());
        this.add(getDeleteTransactionsButton());
        this.add(getDeleteClosedAccountsButton());
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

    public JButton getAddInterestButton() {
        JButton add_interest_btn = new JButton("Add-Interest");
        add_interest_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    CheckSavingsAccountBase.addInterestToAllOpen(
                            conn,
                            "000000000", // admin customer
                            false // should_commit
                    );
                    conn.commit();
                    JOptionPane.showMessageDialog(null, "Successfully added interest to all open accounts.");
                } catch (SQLException se) {
                    se.printStackTrace();
                    JDBCConnectionManager.rollbackConn(conn);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JDBCConnectionManager.rollbackConn(conn);
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
        return add_interest_btn;
    }

    public JButton getDeleteTransactionsButton() {
        JButton delete_transactions_btn = new JButton("Delete Transactions");
        delete_transactions_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    TransactionBase.deleteForBeforeMonth(
                            conn,
                            false // should_commit
                    );
                    conn.commit();
                    JOptionPane.showMessageDialog(null, "Successfully deleted all transactions before this month.");
                } catch (SQLException se) {
                    se.printStackTrace();
                    JDBCConnectionManager.rollbackConn(conn);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JDBCConnectionManager.rollbackConn(conn);
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
        return delete_transactions_btn;
    }

    public JButton getDeleteClosedAccountsButton() {
        JButton delete_closed_accounts_and_customers_btn = new JButton("Delete Closed Accounts");
        delete_closed_accounts_and_customers_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    AccountBase.deleteClosedAccounts(
                            conn,
                            false // should_commit
                    );
                    conn.commit();
                    JOptionPane.showMessageDialog(null, "Successfully deleted all closed accounts and customers with no accounts.");
                } catch (SQLException se) {
                    se.printStackTrace();
                    JDBCConnectionManager.rollbackConn(conn);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JDBCConnectionManager.rollbackConn(conn);
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
        return delete_closed_accounts_and_customers_btn;
    }
}