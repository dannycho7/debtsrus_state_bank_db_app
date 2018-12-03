package models.transaction;

import java.sql.*;

public class CheckTransaction extends TransactionBase {
    public enum CheckTransactionType {
        WRITE_CHECK("Write-Check");

        private final String name;

        CheckTransactionType(String n) {
            name = n;
        }

        protected String getName() {
            return name;
        }

        protected TransactionType getCorrespondingTransactionType() {
            return TransactionType.valueOf(name);
        }
    }

    protected CheckTransactionType check_type;

    CheckTransaction(
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
        int check_no = (int) (Math.random() * Integer.MAX_VALUE); // low chance of collision
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
}