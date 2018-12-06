package gui.bank_teller_panels;

import bank_util.*;
import models.*;
import models.account.*;
import models.transaction.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

public class GenerateMonthlyStatementPanel extends Panel {
    private Connection conn;

    private JTextField customer_id_field = new JTextField(9);

    public GenerateMonthlyStatementPanel(Connection conn) {
        this.conn = conn;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(new JLabel("Customer Tax Id (9 digits):"));
        this.add(customer_id_field);
    }

    public void handleSubmit() throws SQLException {
        String customer_id = customer_id_field.getText();
        JTextArea monthly_statement = new JTextArea();
        monthly_statement.setEditable(false);
        Customer customer = Customer.find(conn, customer_id);
        ArrayList<AccountBase> accounts = customer.genAccounts(conn);
        int sum_balance = 0;
        for (AccountBase account : accounts) {
            sum_balance += account.getBalance();
            monthly_statement.append(String.format("* Account Id: %d\n", account.getAccountId()));
            monthly_statement.append(
                    String.format("** Initial Balance: %d\n", account.genInitialBalanceThisMonth(conn))
            );
            monthly_statement.append(String.format("** Final Balance: %d\n", account.getBalance()));
            monthly_statement.append("** Owners:\n");
            ArrayList<Customer> owners = account.genOwners(conn);
            for (Customer owner : owners) {
                monthly_statement.append(String.format("*** Name: %s\n", owner.getName()));
                monthly_statement.append(String.format("*** Address: %s\n", owner.getAddress()));
            }
            monthly_statement.append("** Transactions:\n");
            ArrayList<BinaryTransaction> binary_transactions = account.genBinaryTransactionsThisMonth(conn);
            for (BinaryTransaction binary_transaction : binary_transactions) {
                String msg = "";
                String transaction_format_string = "";
                switch (binary_transaction.getBinaryTransactionType()) {
                    case TOP_UP:
                        transaction_format_string = "(Top-Up, Fee %s) " +
                                "Moved %s to Pocket Account %d from Check/Savings account %d";
                        break;
                    case TRANSFER:
                        transaction_format_string = "(Transfer, Fee %s) " +
                                "Moved %s from Check/Savings account %d to Check/Savings Account %d";
                        break;
                    case COLLECT:
                        transaction_format_string = "(Collect, Fee %s) " +
                                "Moved %s from Check/Savings account %d to Pocket Account %d";
                        break;
                    case PAY_FRIEND:
                        transaction_format_string = "(Pay-Friend, Fee %s) " +
                                "Moved %s from Pocket account %d to Pocket Account %d";
                        break;
                    case WIRE:
                        transaction_format_string = "(Wire, Fee %s) " +
                                "Moved %s from Check/Savings account %d to Check/Savings Account %d";
                }
                String transaction_msg = String.format(
                        transaction_format_string,
                        BankUtil.getMoneyString(binary_transaction.getFee()),
                        BankUtil.getMoneyString(binary_transaction.getAmount()),
                        binary_transaction.getTransactor(),
                        binary_transaction.getOperand()
                );
                monthly_statement.append(String.format("%s\n", transaction_msg));
            }
            ArrayList<UnaryTransaction> unary_transactions = account.genUnaryTransactionsThisMonth(conn);
            for (UnaryTransaction unary_transaction : unary_transactions) {
                String msg = "";
                String transaction_format_string = "";
                switch (unary_transaction.getUnaryTransactionType()) {
                    case DEPOSIT:
                        transaction_format_string = "(Deposit, Fee %s) " +
                                "Added %s from Check/Savings Account %d";
                        break;
                    case WITHDRAWAL:
                        transaction_format_string = "(Withdrawal, Fee %s) " +
                                "Subtracted %s from Check/Savings Account %d";
                        break;
                    case PURCHASE:
                        transaction_format_string = "(Purchase, Fee %s) " +
                                "Added %s to PocketAccount %d";
                        break;
                    case ACCRUE_INTEREST:
                        transaction_format_string = "(Accrue-Interest, Fee %s) " +
                                "Added %s of interest to Account %d";
                }
                String transaction_msg = String.format(
                        "*** " + transaction_format_string,
                        BankUtil.getMoneyString(unary_transaction.getFee()),
                        BankUtil.getMoneyString(unary_transaction.getAmount()),
                        unary_transaction.getTransactor()
                );
                monthly_statement.append(String.format("%s\n", transaction_msg));
            }
            ArrayList<CheckTransaction> check_transactions = account.genCheckTransactionsThisMonth(conn);
            for (CheckTransaction check_transaction : check_transactions) {
                String msg = "";
                String transaction_format_string = "";
                switch (check_transaction.getCheckTransactionType()) {
                    case WRITE_CHECK:
                        transaction_format_string = "(Write-Check, Fee %s) " +
                                "Account %d wrote check #%s worth %s";
                }
                String transaction_msg = String.format(
                        transaction_format_string,
                        BankUtil.getMoneyString(check_transaction.getFee()),
                        check_transaction.getTransactor(),
                        check_transaction.getCheckNo(),
                        BankUtil.getMoneyString(check_transaction.getFee())
                );
                monthly_statement.append(String.format("%s\n", transaction_msg));
            }
        }
        if (sum_balance > 100000 * 100) {
            monthly_statement.append("Warning: This customer has reached the limit of insurance\n");
        }
        JScrollPane scroll_pane = new JScrollPane(monthly_statement);
        scroll_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_pane.setPreferredSize(new Dimension(500, 500));
        JOptionPane.showMessageDialog(
                null,
                scroll_pane,
                "Monthly Statement",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    public static void openGenerateMonthlyStatementDialog(Connection conn) {
        GenerateMonthlyStatementPanel generate_monthly_statement_pane = new GenerateMonthlyStatementPanel(conn);
        int result = JOptionPane.showConfirmDialog(
                null,
                generate_monthly_statement_pane,
                "GenerateMonthlyStatement",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                generate_monthly_statement_pane.handleSubmit();
            } catch (SQLException se) {
                se.printStackTrace();
                JOptionPane.showMessageDialog(null, "Something went wrong with GenerateMonthlyStatement.");
            }
        }
    }
}