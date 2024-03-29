package models.account;

import bank_util.*;
import java.sql.*;
import java.util.ArrayList;
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

    public static void addInterestToAllOpen(
            Connection conn,
            boolean should_commit
    ) throws SQLException {
        if (AccountBase.hasInterestBeenAddedThisMonth(conn)) {
            throw new IllegalArgumentException("Interest has already been added for this month");
        }
        ArrayList<Integer> account_ids = CheckSavingsAccountBase.genAllOpenAccountIds(conn);
        for (int account_id : account_ids) {
            TransactionFactory.createAccrueInterest(
                    conn,
                    account_id,
                    false // should_commit
            );
        }
        if (should_commit)
            conn.commit();
    }

    public static void create(
            Connection conn,
            int account_id,
            int balance, // $$ in cents
            String branch_name,
            CheckSavingsAccountType check_savings_account_type,
            String customer_tax_id,
            String[] other_owners,
            String initiator,
            boolean should_commit
    ) throws SQLException {
        if (balance == 0) {
            throw new IllegalArgumentException("Balance for a check/savings account cannot start at 0");
        }
        AccountBase.create(
                conn,
                account_id,
                0, // balance starts at 0 in the creation process, but it updated via createDeposit later
                branch_name,
                check_savings_account_type.getCorrespondingAccountType(),
                customer_tax_id,
                other_owners,
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
                initiator,
                account_id, // transactor
                false // should_commit
        );
        if (should_commit)
            conn.commit();
    }

    public static CheckSavingsAccountBase find(
            Connection conn,
            int account_id
    ) throws SQLException {
        String get_check_savings_account_sql = String.format("SELECT %s FROM Account A " +
                        "JOIN Check_savings_account Csa ON A.account_id = Csa.account_id " +
                        "WHERE Csa.account_id = %s "
                , "A.account_id, A.balance, A.closed, A.branch_name, A.type, A.primary_owner"
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
            rs.close();
            stmt.close();
            return new CheckSavingsAccountBase(
                    chk_savings_account_id,
                    balance,
                    closed,
                    branch_name,
                    type,
                    primary_owner
            );
        }
        rs.close();
        stmt.close();
        throw new IllegalArgumentException(String.format("Could not find CheckSavingsAccount %s", account_id));
    }

    public static CheckSavingsAccountBase findOpen(
            Connection conn,
            int account_id
    ) throws SQLException {
        CheckSavingsAccountBase account = CheckSavingsAccountBase.find(conn, account_id);
        if (account.isClosed()) {
            String err_msg = String.format("Found the account %d, but it was closed", account.account_id);
            throw new IllegalArgumentException(err_msg);
        }
        return account;
    }

    // NOTE: Does not work if a transaction was executed AFTER this month
    public int genAvgDailyBalanceInMonth(
            Connection conn
    ) throws SQLException {
        int num_days_in_month = BankUtil.getNumDaysInCurrentMonth();
        int[] delta_per_day = this.genDeltasFromTransactionsThisMonth(conn);
        int[] balance_per_day = new int[num_days_in_month];
        balance_per_day[num_days_in_month - 1] = this.getBalance();
        for (int i = num_days_in_month - 2; i >= 0; i--) {
            balance_per_day[i] = balance_per_day[i + 1] - delta_per_day[i + 1];
        }
        int sum_total_balance_in_month = 0;
        for (int balance : balance_per_day) {
            sum_total_balance_in_month += balance;
        }
        return (int) (sum_total_balance_in_month / num_days_in_month);
    }

    public static ArrayList<Integer> genAllOpenAccountIds(
            Connection conn
    ) throws SQLException {
        ArrayList<Integer> account_ids = new ArrayList<Integer>();
        String get_open_account_id_sql = String.format("SELECT %s FROM Account A " +
                        "JOIN Check_savings_account Csa ON A.account_id = Csa.account_id WHERE A.closed = 0"
                , "A.account_id"
        );
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(get_open_account_id_sql);
        while (rs.next()) {
            int account_id = rs.getInt("account_id");
            account_ids.add(account_id);
        }
        rs.close();
        stmt.close();
        return account_ids;
    }

    public double genInterestRate(
            Connection conn
    ) throws SQLException {
        String get_account_type_sql = String.format("SELECT %s FROM Account_type At " +
                        "WHERE At.name = '%s'"
                , "At.interest_rate"
                , this.acct_type.getName()
        );
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(get_account_type_sql);
        while (rs.next()) {
            double interest_rate = rs.getDouble("interest_rate");
            rs.close();
            stmt.close();
            return interest_rate;
        }
        rs.close();
        stmt.close();
        throw new IllegalArgumentException("No such account type exists");
    }

    public void updateBalance(
            Connection conn,
            int balance,
            boolean should_commit
    ) throws SQLException {
        super.updateBalance(
                conn,
                balance,
                false // should_commit
        );
        if (balance == 0 || balance == 1) {
            System.out.println("Closing account");
            this.modifyAccountToClose(
                    conn,
                    false // should_commit
            );
        }
        if (should_commit)
            conn.commit();
    }
}