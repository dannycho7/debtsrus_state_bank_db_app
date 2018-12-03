package models.transaction;

import java.sql.*;

public class UnaryTransaction extends TransactionBase {
    public enum UnaryTransactionType {
        DEPOSIT("Deposit"),
        WITHDRAWAL("Withdrawal"),
        PURCHASE("Purchase"),
        ACCRUE_INTEREST("Accrue-Interest");

        private final String name;

        UnaryTransactionType(String n) {
            name = n;
        }

        protected String getName() {
            return name;
        }

        protected TransactionType getCorrespondingTransactionType() {
            return TransactionType.valueOf(name);
        }
    }

    protected UnaryTransactionType unary_type;

    UnaryTransaction(
            int t_id,
            int amount,
            String timestamp,
            int fee,
            String initiator, // customer tax_id
            int transactor, // account_id
            UnaryTransactionType unary_type
    ) {
        super(
                t_id,
                amount,
                timestamp,
                fee,
                initiator,
                transactor,
                unary_type.getCorrespondingTransactionType()
        );
        this.unary_type = unary_type;
    }

    // returns: t_id
    public static int create(
            Connection conn,
            int amount,
            String timestamp,
            int fee,
            String initiator, // customer tax_id
            int transactor, // account_id
            UnaryTransactionType type,
            boolean should_commit
    ) throws SQLException {
        int t_id = TransactionBase.create(
                conn,
                amount,
                timestamp,
                fee,
                initiator,
                transactor,
                type.getCorrespondingTransactionType(),
                false // should_commit
        ); // creates transaction base

        Statement stmt = conn.createStatement();
        String sql = String.format("INSERT INTO Unary_transaction %s VALUES (%d)"
                , "(t_id)"
                , t_id
        );
        int n = stmt.executeUpdate(sql);
        System.out.println(n + " rows affected");
        if (should_commit)
            conn.commit();

        return t_id;
    }
}