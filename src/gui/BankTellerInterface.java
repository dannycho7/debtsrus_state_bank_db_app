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
import java.util.ArrayList;

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
        this.add(getListClosedAccountsButton());
        this.add(getGenerateMonthlyStatementsButton());
        this.add(getCustomerReportButton());
        this.add(getGenerateGovernmentDTERButton());
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
    public JButton getListClosedAccountsButton() {
        JButton list_closed_accounts_btn = new JButton("List Closed Accounts");
        list_closed_accounts_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<AccountCloseHistory> accounts_closed = AccountCloseHistory.genAccountsClosedInMonth(conn);
                    String closed_message = "Closed Accounts:\n";
                    for (AccountCloseHistory account_closed : accounts_closed) {
                        closed_message += String.format("Account %d was closed on %s\n",
                                account_closed.getAccountId(),
                                account_closed.getTimestamp()
                        );
                    }
                    JOptionPane.showMessageDialog(null, closed_message);
                } catch (SQLException se) {
                    se.printStackTrace();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
        return list_closed_accounts_btn;
    }
    public JButton getGenerateMonthlyStatementsButton() {
        JButton generate_monthly_statement_btn = new JButton("Generate Monthly Statement");
        generate_monthly_statement_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GenerateMonthlyStatementPanel.openGenerateMonthlyStatementDialog(conn);
            }
        });
        return generate_monthly_statement_btn;
    }
    public JButton getCustomerReportButton() {
        JButton customer_report_btn = new JButton("Customer Report");
        customer_report_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CustomerReportPanel.openCustomerReportDialog(conn);
            }
        });
        return customer_report_btn;
    }
    public JButton getGenerateGovernmentDTERButton() {
        JButton generate_government_DTER_btn = new JButton("Generate Government DTER");
        generate_government_DTER_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<Customer> qualified_customers = Customer.genCustomersForDTER(conn);
                    JTextArea monthly_statement = new JTextArea();
                    monthly_statement.setEditable(false);
                    for (Customer customer : qualified_customers) {
                        monthly_statement.append(String.format("Customer tax id: %s", customer.getTaxId()));
                    }
                    JScrollPane scroll_pane = new JScrollPane(monthly_statement);
                    scroll_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    scroll_pane.setPreferredSize(new Dimension(500, 500));
                    JOptionPane.showMessageDialog(
                            null,
                            scroll_pane,
                            "Government DTER",
                            JOptionPane.PLAIN_MESSAGE
                    );                } catch (SQLException se) {
                    se.printStackTrace();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
        return generate_government_DTER_btn;
    }
}