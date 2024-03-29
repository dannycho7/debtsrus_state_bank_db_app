package models.transaction;

import java.sql.*;

public class BinaryTransaction extends TransactionBase {
    public enum BinaryTransactionType {
        TOP_UP(TransactionType.TOP_UP),
        TRANSFER(TransactionType.TRANSFER),
        COLLECT(TransactionType.COLLECT),
        PAY_FRIEND(TransactionType.PAY_FRIEND),
        WIRE(TransactionType.WIRE);

        private final TransactionType transaction_type;
        BinaryTransactionType(TransactionType t) {
            transaction_type = t;
        }
        protected TransactionType getCorrespondingTransactionType() {
            return transaction_type;
        }
        public static BinaryTransactionType fromString(String n) {
            switch (n) {
                case "Top-Up":
                    return BinaryTransactionType.TOP_UP;
                case "Transfer":
                    return BinaryTransactionType.TRANSFER;
                case "Collect":
                    return BinaryTransactionType.COLLECT;
                case "Pay-Friend":
                    return BinaryTransactionType.PAY_FRIEND;
                case "Wire":
                    return BinaryTransactionType.WIRE;
                default:
                    return null;
            }
        }
    }

    protected String initiator;
    protected int operand;
    protected BinaryTransactionType binary_type;

    public BinaryTransaction(
            int t_id,
            int amount,
            String timestamp,
            int fee,
            String initiator, // customer tax_id
            int transactor, // transactor account_id
            int operand, // operand account_id
            BinaryTransactionType binary_type
    ) {
        super(
                t_id,
                amount,
                timestamp,
                fee,
                transactor,
                binary_type.getCorrespondingTransactionType()
        );
        this.operand = operand;
        this.binary_type = binary_type;
        this.initiator = initiator;
    }

    // returns: t_id
    public static int create(
            Connection conn,
            int amount,
            String timestamp,
            int fee,
            String initiator, // customer tax_id
            int transactor, // account_id
            BinaryTransactionType type,
            int operand,
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
        String sql = String.format("INSERT INTO Binary_transaction %s VALUES (%d, '%s', %d)"
                , "(t_id, initiator, operand)"
                , t_id
                , initiator
                , operand
        );
        int n = stmt.executeUpdate(sql);
        System.out.println(n + " rows affected");
        if (should_commit)
            conn.commit();

        return t_id;
    }

    public int getOperand() {
        return operand;
    }
    public String getInitiator() {
        return initiator;
    }
    public BinaryTransactionType getBinaryTransactionType() {
        return binary_type;
    }
    public boolean isTopUp() {
        return this.binary_type == BinaryTransactionType.TOP_UP;
    }
    public boolean isTransfer() {
        return this.binary_type == BinaryTransactionType.TRANSFER;
    }
    public boolean isCollect() {
        return this.binary_type == BinaryTransactionType.COLLECT;
    }
    public boolean isPayFriend() {
        return this.binary_type == BinaryTransactionType.PAY_FRIEND;
    }
    public boolean isWire() {
        return this.binary_type == BinaryTransactionType.WIRE;
    }
}