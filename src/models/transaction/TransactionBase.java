package models.transaction;

import BankUtil.*;
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

    TransactionBase(
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
        String sql = String.format("INSERT INTO Transaction %s VALUES ('%d', '%d', '%s', '%d', '%s', '%d', '%s')"
                , "(t_id, amount, timestamp, fee, initiator, transactor, type)"
                , t_id
                , amount
                , timestamp
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
}