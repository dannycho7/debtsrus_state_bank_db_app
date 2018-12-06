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
                switch (binary_transaction.getBinaryTransactionType()) {
                    case TOP_UP:
                    case TRANSFER:
                    case COLLECT:
                    case PAY_FRIEND:
                    case WIRE:
                        monthly_statement.append("Transaction exists\n");
                }
            }
            ArrayList<UnaryTransaction> unary_transactions = account.genUnaryTransactionsThisMonth(conn);
            for (UnaryTransaction unary_transaction : unary_transactions) {
                switch (unary_transaction.getUnaryTransactionType()) {
                    case DEPOSIT:
                    case WITHDRAWAL:
                    case PURCHASE:
                    case ACCRUE_INTEREST:
                        monthly_statement.append("Transaction exists\n");
                }
            }
            ArrayList<CheckTransaction> check_transactions = account.genCheckTransactionsThisMonth(conn);
            for (CheckTransaction check_transaction : check_transactions) {
                switch (check_transaction.getCheckTransactionType()) {
                    case WRITE_CHECK:
                        monthly_statement.append("Transaction exists\n");
                }
            }
        }
        if (sum_balance > 100000 * 100) {
            monthly_statement.append("Warning: This customer has reached the limit of insurance\n");
        }
        JScrollPane scroll_pane = new JScrollPane(monthly_statement);
        scroll_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_pane.setPreferredSize(new Dimension(250, 250));
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