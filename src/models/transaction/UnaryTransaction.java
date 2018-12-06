package models.transaction;

import java.sql.*;

public class UnaryTransaction extends TransactionBase {
    public enum UnaryTransactionType {
        DEPOSIT(TransactionType.DEPOSIT),
        WITHDRAWAL(TransactionType.WITHDRAWAL),
        PURCHASE(TransactionType.PURCHASE);

        private final TransactionType transaction_type;
        UnaryTransactionType(TransactionType t) {
            transaction_type = t;
        }
        protected TransactionType getCorrespondingTransactionType() {
            return transaction_type;
        }
        public static UnaryTransactionType fromString(String n) {
            switch (n) {
                case "Deposit":
                    return UnaryTransactionType.DEPOSIT;
                case "Withdrawal":
                    return UnaryTransactionType.WITHDRAWAL;
                case "Purchase":
                    return UnaryTransactionType.PURCHASE;
                default:
                    return null;
            }
        }
    }

    protected String initiator;
    protected UnaryTransactionType unary_type;

    public UnaryTransaction(
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
                transactor,
                unary_type.getCorrespondingTransactionType()
        );
        this.initiator = initiator;
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
                transactor,
                type.getCorrespondingTransactionType(),
                false // should_commit
        ); // creates transaction base

        Statement stmt = conn.createStatement();
        String sql = String.format("INSERT INTO Unary_transaction %s VALUES (%d, '%s')"
                , "(t_id, initiator)"
                , t_id
                , initiator
        );
        int n = stmt.executeUpdate(sql);
        System.out.println(n + " rows affected");
        if (should_commit)
            conn.commit();

        return t_id;
    }

    public UnaryTransactionType getUnaryTransactionType() {
        return unary_type;
    }
    public String getInitiator() {
        return initiator;
    }
    public boolean isDeposit() {
        return this.unary_type == UnaryTransactionType.DEPOSIT;
    }
    public boolean isWithdrawal() {
        return this.unary_type == UnaryTransactionType.WITHDRAWAL;
    }
    public boolean isPurchase() {
        return this.unary_type == UnaryTransactionType.PURCHASE;
    }
}