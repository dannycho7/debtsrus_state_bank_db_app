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
    }

    protected BinaryTransactionType binary_type;

    BinaryTransaction(
            int t_id,
            int amount,
            String timestamp,
            int fee,
            String initiator, // customer tax_id
            int transactor, // account_id
            BinaryTransactionType binary_type
    ) {
        super(
                t_id,
                amount,
                timestamp,
                fee,
                initiator,
                transactor,
                binary_type.getCorrespondingTransactionType()
        );
        this.binary_type = binary_type;
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
                initiator,
                transactor,
                type.getCorrespondingTransactionType(),
                false // should_commit
        ); // creates transaction base

        Statement stmt = conn.createStatement();
        String sql = String.format("INSERT INTO Binary_transaction %s VALUES (%d, %d)"
                , "(t_id, operand)"
                , t_id
                , operand
        );
        int n = stmt.executeUpdate(sql);
        System.out.println(n + " rows affected");
        if (should_commit)
            conn.commit();

        return t_id;
    }

    public BinaryTransactionType getBinaryTransactionType() {
        return binary_type;
    }
}