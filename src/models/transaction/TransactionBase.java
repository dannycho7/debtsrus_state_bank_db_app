package models.transaction;

import bank_util.*;
import java.sql.*;

abstract public class TransactionBase {
    public enum TransactionType {
        DEPOSIT("Deposit"),
        TOP_UP("Top-Up"),
        WITHDRAWAL("Withdrawal"),
        PURCHASE("Purchase"),
        TRANSFER("Transfer"),
        COLLECT("Collect"),
        PAY_FRIEND("Pay-Friend"),
        WIRE("Wire"),
        WRITE_CHECK("Write-Check"),
        ACCRUE_INTEREST("Accrue-Interest");

        private final String name;

        TransactionType(String n) {
            name = n;
        }

        protected String getName() {
            return name;
        }
    }

    protected int t_id;
    protected int amount;
    protected String timestamp;
    protected int fee;
    protected String initiator;
    protected int transactor;
    protected TransactionType type;

    public TransactionBase(
            int t_id,
            int amount,
            String timestamp,
            int fee,
            String initiator, // customer tax_id
            int transactor, // account_id
            TransactionType type
    ) {
        this.t_id = t_id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.fee = fee;
        this.initiator = initiator;
        this.transactor = transactor;
        this.type = type;
    }

    // returns: transaction_id
    protected static int create(
            Connection conn,
            int amount,
            String timestamp,
            int fee,
            String initiator, // customer tax_id
            int transactor, // account_id
            TransactionType type,
            boolean should_commit
    ) throws SQLException {
        int t_id = BankUtil.getUUID();
        Statement stmt = conn.createStatement();
        String sql = String.format("INSERT INTO Transaction %s VALUES ('%d', '%d', %s, '%d', '%s', '%d', '%s')"
                , "(t_id, amount, timestamp, fee, initiator, transactor, type)"
                , t_id
                , amount
                , String.format("TO_DATE('%s', 'YYYY-MM-DD')", timestamp)
                , fee
                , initiator
                , transactor
                , type.getName()
        );
        int n = stmt.executeUpdate(sql);
        System.out.println(n + " rows affected");
        if (should_commit)
            conn.commit();
        return t_id;
    }

    public static void deleteForOnAndBeforeMonth(
            Connection conn,
            String month_year_string,
            boolean should_commit
    ) throws SQLException {
        String delete_transactions_sql = String.format("DELETE FROM Transaction T" +
                        "WHERE '%s' >= TO_CHAR(T.timestamp, 'YYYY-MM')"
                , month_year_string
        );
        Statement stmt = conn.createStatement();
        int n = stmt.executeUpdate(delete_transactions_sql);
        System.out.println(n + " rows deleted");
        if (should_commit)
            conn.commit();
    }

    public int getTId() {
        return t_id;
    }
    public int getAmount() {
        return amount;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public int getFee() {
        return fee;
    }
    public String getInitiator() {
        return initiator;
    }
    public int getTransactor() {
        return transactor;
    }
    public TransactionType getTransactionType() {
        return type;
    }
}