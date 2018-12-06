package models.transaction;

import java.sql.*;

public class AccrueInterestTransaction extends TransactionBase {
    public AccrueInterestTransaction(
            int t_id,
            int amount,
            String timestamp,
            int fee,
            int transactor // account_id
    ) {
        super(
                t_id,
                amount,
                timestamp,
                fee,
                transactor,
                TransactionType.ACCRUE_INTEREST
        );
    }

    // returns: t_id
    public static int create(
            Connection conn,
            int amount,
            String timestamp,
            int fee,
            int transactor, // account_id
            boolean should_commit
    ) throws SQLException {
        int t_id = TransactionBase.create(
                conn,
                amount,
                timestamp,
                fee,
                transactor,
                TransactionType.ACCRUE_INTEREST,
                false // should_commit
        ); // creates transaction base

        Statement stmt = conn.createStatement();
        String sql = String.format("INSERT INTO Accrue_interest_transaction %s VALUES (%d)"
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