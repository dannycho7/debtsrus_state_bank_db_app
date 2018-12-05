package gui.bank_teller_panels;

import bank_util.*;
import models.account.*;
import models.account.AccountBase.AccountType;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.text.NumberFormat;

public class AccountCreationPanel extends Panel {
    private final static String[] account_types = {
            AccountType.STUDENT_CHECKING.getName(),
            AccountType.INTEREST_CHECKING.getName(),
            AccountType.SAVINGS.getName(),
            AccountType.POCKET.getName()
    };

    private Connection conn;
    private JFormattedTextField account_id_field;
    private JFormattedTextField balance_field;
    private JTextField branch_name_field;
    private JTextField primary_owner_field;
    private JTextField owners_field;
    private JComboBox account_type_list;
    private JFormattedTextField optional_linked_account_field;

    public AccountCreationPanel(Connection conn) {
        this.conn = conn;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);

        account_id_field = new JFormattedTextField(format);
        account_id_field.setColumns(10);
        balance_field = new JFormattedTextField(format);
        balance_field.setColumns(10);
        branch_name_field = new JTextField(10);
        primary_owner_field = new JTextField(10);
        owners_field = new JTextField(20);
        account_type_list = new JComboBox(account_types);
        optional_linked_account_field = new JFormattedTextField(format);
        balance_field.setColumns(10);

        this.add(getAddCustomerButton());
        this.add(new JLabel("Account Id:"));
        this.add(account_id_field);
        this.add(new JLabel("Balance (in cents):"));
        this.add(balance_field);
        this.add(new JLabel("Branch Name:"));
        this.add(branch_name_field);
        this.add(new JLabel("Account Type:"));
        this.add(account_type_list);
        this.add(new JLabel("Primary Owner:"));
        this.add(primary_owner_field);
        this.add(new JLabel("Other Owners (comma separated):"));
        this.add(owners_field);
        this.add(new JLabel("Linked Account (Only for Pocket)"));
        this.add(optional_linked_account_field);
    }

    public JButton getAddCustomerButton() {
        JButton add_customer_btn = new JButton("Add Customer");
        add_customer_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CustomerCreationPanel.openCustomerCreationDialog(conn);
            }
        });
        return add_customer_btn;
    }

    public void handleSubmit() throws SQLException {
        int account_id = ((Number) account_id_field.getValue()).intValue();
        int balance = ((Number) balance_field.getValue()).intValue();
        String branch_name = branch_name_field.getText();
        String primary_owner = primary_owner_field.getText();
        AccountType acct_type = AccountType.fromString((String) account_type_list.getSelectedItem());

        switch (acct_type) {
            case STUDENT_CHECKING:
                CheckingAccount.createForStudent(
                        conn,
                        account_id,
                        balance,
                        branch_name,
                        primary_owner,
                        false // should_commit
                );
                break;
            case INTEREST_CHECKING:
                CheckingAccount.createForInterest(
                        conn,
                        account_id,
                        balance,
                        branch_name,
                        primary_owner,
                        false // should_commit
                );
                break;
            case SAVINGS:
                SavingsAccount.create(
                        conn,
                        account_id,
                        balance,
                        branch_name,
                        primary_owner,
                        false // should_commit
                );
                break;
            case POCKET:
                int linked_account_id = ((Number) optional_linked_account_field.getValue()).intValue();
                PocketAccount.create(
                        conn,
                        account_id,
                        balance,
                        branch_name,
                        primary_owner,
                        linked_account_id,
                        false // should_commit
                );
                break;
            default:
                System.out.println("Inexhaustive case");
        }

        String comma_sep_owners = owners_field.getText().replaceAll(" ", "");
        String[] owners = comma_sep_owners.length() == 0 ? new String[0] : comma_sep_owners.split(",");
        for (String owner : owners) {
            AccountBase.createAccountOwnership(
                    conn,
                    owner,
                    account_id,
                    false // should_commit
            );
        }
    }

    public static void openAccountCreationDialog(Connection conn) {
        AccountCreationPanel acct_create_pane = new AccountCreationPanel(conn);
        int result = JOptionPane.showConfirmDialog(
                null,
                acct_create_pane,
                "Create Account",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            try {
                acct_create_pane.handleSubmit();
                conn.commit();
                JOptionPane.showMessageDialog(null, "Successfully created account.");
            } catch (SQLException se) {
                se.printStackTrace();
                JDBCConnectionManager.rollbackConn(conn);
                JOptionPane.showMessageDialog(null, "Something went wrong when creating the account.");
            }
        } else {
            JDBCConnectionManager.rollbackConn(conn);
        }
    }
}