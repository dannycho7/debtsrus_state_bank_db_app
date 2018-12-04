package models.account;

import bank_util.*;
import java.sql.*;
import models.transaction.*;

public class CheckSavingsAccountBase extends AccountBase {
    public enum CheckSavingsAccountType {
        STUDENT_CHECKING(AccountType.STUDENT_CHECKING),
        INTEREST_CHECKING(AccountType.INTEREST_CHECKING),
        SAVINGS(AccountType.SAVINGS);

        private final AccountType account_type;
        CheckSavingsAccountType(AccountType t) {
            account_type = t;
        }
        protected AccountType getCorrespondingAccountType() {
            return account_type;
        }
    }

    public CheckSavingsAccountBase(
            int account_id,
            int balance,
            boolean closed,
            String branch_name,
            AccountType acct_type,
            String customer_tax_id
    ) {
        super(
                account_id,
                balance,
                closed,
                branch_name,
                acct_type,
                customer_tax_id
        );
    }

    public static void create(
            Connection conn,
            int account_id,
            int balance, // $$ in cents
            String branch_name,
            CheckSavingsAccountType check_savings_account_type,
            String customer_tax_id,
            boolean should_commit
    ) throws SQLException {
        AccountBase.create(
                conn,
                account_id,
                balance,
                branch_name,
                check_savings_account_type.getCorrespondingAccountType(),
                customer_tax_id,
                false // should_commit
        ); // creates account base
        Statement stmt = conn.createStatement();
        String sql = String.format("INSERT INTO Check_savings_account %s VALUES (%d)"
                , "(account_id)"
                , account_id
        );
        int n = stmt.executeUpdate(sql);
        System.out.println(n + " rows affected");
        TransactionFactory.createDeposit(
                conn,
                balance, // amount (we deposit the starting balance)
                customer_tax_id, // initiator is the creator of this account
                account_id, // transactor
                false // should_commit
        );
        if (should_commit)
            conn.commit();
    }

    public static CheckSavingsAccountBase find(
            Connection conn,
            int account_id
    ) throws SQLException, IllegalArgumentException {
        String get_check_savings_account_sql = String.format("SELECT %s FROM Account A" +
                        "JOIN Check_savings_account Csa ON A.account_id = Csa.account_id" +
                        "WHERE Csa.account_id = %s"
                , "A.account_id", "A.balance", "A.closed", "A.branch_name", "A.type", "A.primary_owner"
                , account_id
        );

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(get_check_savings_account_sql);
        while (rs.next()) {
            int chk_savings_account_id = rs.getInt("account_id");
            int balance = rs.getInt("balance");
            boolean closed = (rs.getInt("closed") == 1);
            String branch_name = rs.getString("branch_name");
            AccountType type = AccountBase.AccountType.fromString(rs.getString("type"));
            String primary_owner = rs.getString("primary_owner");

            return new CheckSavingsAccountBase(
                    chk_savings_account_id,
                    balance,
                    closed,
                    branch_name,
                    type,
                    primary_owner
            );
        }

        throw new IllegalArgumentException(String.format("Could not find CheckSavingsAccount %s", account_id));
    }

    public static CheckSavingsAccountBase findOpen(
            Connection conn,
            int account_id
    ) throws SQLException, IllegalArgumentException {
        CheckSavingsAccountBase account = CheckSavingsAccountBase.find(conn, account_id);
        if (account.isClosed()) {
            String err_msg = String.format("Found the account %d, but it was closed", account.account_id);
            throw new IllegalArgumentException(err_msg);
        }
        return account;
    }

    public int genAvgDailyBalanceInMonth(
            Connection conn
    ) throws SQLException {
        String get_transactions_this_month_sql = String.format("SELECT %s" +
                        "FROM Transaction T" +
                        "LEFT JOIN Binary_transaction Bt ON T.t_id = Bt.t_id" +
                        "WHERE TO_CHAR(T.timestamp, 'MM-YYYY') = '%s' AND (T.transactor = %d OR Bt.operand = %d)"
                , "T.t_id, T.amount, T.timestamp, T.fee, T.initiator, T.transactor, T.type, Bt.operand"
                , BankUtil.getCurrentMonthYear()
                , this.account_id
                , this.account_id
        );
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(get_transactions_this_month_sql);
        int num_days_in_month = BankUtil.getNumDaysInCurrentMonth();
        int sum_total_balance_in_month = 0;
        while (rs.next()) {
            int t_id = rs.getInt("t_id");
            int amount = rs.getInt("amount");
            String timestamp = rs.getString("timestamp");
            int fee = rs.getInt("fee");
            String initiator = rs.getString("initiator");
            int transactor = rs.getInt("transactor");
            String type = rs.getString("type");
            int operand = rs.getInt("operand");

            // TODO:
            // use (transactor, operand, type) 3-tuple to figure out delta +/- in balance from this transaction
            // add to sum_total_balance_in_month
        }
        return (int) (sum_total_balance_in_month / num_days_in_month);
    }
}