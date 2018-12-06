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

        public String getName() {
            return name;
        }

        public static TransactionType fromString(String n) {
            switch (n) {
                case "Deposit":
                    return TransactionType.DEPOSIT;
                case "Top-Up":
                    return TransactionType.TOP_UP;
                case "Withdrawal":
                    return TransactionType.WITHDRAWAL;
                case "Purchase":
                    return TransactionType.PURCHASE;
                case "Transfer":
                    return TransactionType.TRANSFER;
                case "Collect":
                    return TransactionType.COLLECT;
                case "Pay-Friend":
                    return TransactionType.PAY_FRIEND;
                case "Wire":
                    return TransactionType.WIRE;
                case "Write-Check":
                    return TransactionType.WRITE_CHECK;
                case "Accrue-Interest":
                    return TransactionType.ACCRUE_INTEREST;
                default:
                    return null;
            }
        }
    }

    protected int t_id;
    protected int amount;
    protected String timestamp;
    protected int fee;
    protected int transactor;
    protected TransactionType type;

    public TransactionBase(
            int t_id,
            int amount,
            String timestamp,
            int fee,
            int transactor, // account_id
            TransactionType type
    ) {
        this.t_id = t_id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.fee = fee;
        this.transactor = transactor;
        this.type = type;
    }

    // returns: transaction_id
    protected static int create(
            Connection conn,
            int amount,
            String timestamp,
            int fee,
            int transactor, // account_id
            TransactionType type,
            boolean should_commit
    ) throws SQLException {
        int t_id = BankUtil.getUUID();
        Statement stmt = conn.createStatement();
        String sql = String.format("INSERT INTO Transaction %s VALUES (%d, %d, %s, %d, %d, '%s')"
                , "(t_id, amount, timestamp, fee, transactor, type)"
                , t_id
                , amount
                , String.format("TO_DATE('%s', 'YYYY-MM-DD')", timestamp) // should not have single quotes
                , fee
                , transactor
                , type.getName()
        );
        int n = stmt.executeUpdate(sql);
        System.out.println(n + " rows affected");
        if (should_commit)
            conn.commit();
        return t_id;
    }

    public static void deleteForBeforeMonth(
            Connection conn,
            boolean should_commit
    ) throws SQLException {
        String year_month_string = BankUtil.getCurrentYearMonth();
        String delete_transactions_sql = String.format("DELETE FROM Transaction T " +
                        "WHERE '%s' > TO_CHAR(T.timestamp, 'YYYY-MM')"
                , year_month_string
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
    public int getTransactor() {
        return transactor;
    }
    public TransactionType getTransactionType() {
        return type;
    }
}