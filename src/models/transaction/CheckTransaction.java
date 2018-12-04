package models.transaction;

import bank_util.*;
import java.sql.*;

public class CheckTransaction extends TransactionBase {
    public enum CheckTransactionType {
        WRITE_CHECK(TransactionType.WRITE_CHECK);

        private final TransactionType transaction_type;
        CheckTransactionType(TransactionType t) {
            transaction_type = t;
        }
        protected TransactionType getCorrespondingTransactionType() {
            return transaction_type;
        }
    }

    protected CheckTransactionType check_type;

    public CheckTransaction(
            int t_id,
            int amount,
            String timestamp,
            int fee,
            String initiator, // customer tax_id
            int transactor, // account_id
            CheckTransactionType check_type
    ) {
        super(
                t_id,
                amount,
                timestamp,
                fee,
                initiator,
                transactor,
                check_type.getCorrespondingTransactionType()
        );
        this.check_type = check_type;
    }

    // returns: t_id
    public static int create(
            Connection conn,
            int amount,
            String timestamp,
            int fee,
            String initiator,
            int transactor,
            CheckTransactionType type,
            boolean should_commit
    ) throws SQLException {
        int check_no = BankUtil.getUUID();
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
        String sql = String.format("INSERT INTO Check_transaction %s VALUES (%d, %d)"
                , "(t_id, check_no)"
                , t_id
                , check_no
        );
        int n = stmt.executeUpdate(sql);
        System.out.println(n + " rows affected");
        if (should_commit)
            conn.commit();

        return t_id;
    }

    public CheckTransactionType getCheckTransactionType() {
        return check_type;
    }
}