package models.account;

import bank_util.*;
import java.sql.*;
import java.util.ArrayList;

public class AccountCloseHistory {
    protected int account_id;
    protected String timestamp;

    AccountCloseHistory(
            int account_id,
            String timestamp
    ) {
        this.account_id = account_id;
        this.timestamp = timestamp;
    }

    public static void create(
            Connection conn,
            int account_id,
            String timestamp,
            boolean should_commit
    ) throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = String.format("INSERT INTO Account_close_history %s VALUES (%d, '%s')"
                , "(account_id, timestamp)"
                , account_id
                , String.format("TO_DATE('%s', 'YYYY-MM-DD')", timestamp)
        );
        int n = stmt.executeUpdate(sql);
        System.out.println(n + " rows affected");
        if (should_commit)
            conn.commit();
    }

    public static ArrayList<AccountCloseHistory> genAccountsClosedInMonth(
            Connection conn
    ) throws SQLException {
        ArrayList<AccountCloseHistory> accounts = new ArrayList<AccountCloseHistory>();
        String find_accounts_sql = String.format("SELECT %s" +
                        "Account_close_history Ach" +
                        "WHERE TO_CHAR(T.timestamp, 'MM-YYYY') = '%s'"
                , "Ach.account_id, Ach.timestamp"
                , BankUtil.getCurrentMonthYear()
        );
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(find_accounts_sql);
        while (rs.next()) {
            int account_id = rs.getInt("account_id");
            String timestamp = rs.getString("timestamp");
            AccountCloseHistory account = new AccountCloseHistory(
                    account_id,
                    timestamp
            );

            accounts.add(account);
        }
        return accounts;
    }

    public int getAccountId() {
        return account_id;
    }
    public String getTimestamp() {
        return timestamp;
    }
}