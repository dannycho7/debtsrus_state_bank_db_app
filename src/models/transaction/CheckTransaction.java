package models.transaction;

import bank_util.*;
import java.sql.*;

public class CheckTransaction extends TransactionBase {
    protected int check_no;
    protected String initiator;

    public CheckTransaction(
            int t_id,
            int amount,
            String timestamp,
            int fee,
            String initiator, // customer tax_id
            int transactor, // account_id
            int check_no
    ) {
        super(
                t_id,
                amount,
                timestamp,
                fee,
                transactor,
                TransactionType.WRITE_CHECK
        );
        this.check_no = check_no;
        this.initiator = initiator;
    }

    // returns: t_id
    public static int create(
            Connection conn,
            int amount,
            String timestamp,
            int fee,
            String initiator,
            int transactor,
            boolean should_commit
    ) throws SQLException {
        int check_no = BankUtil.getUUID();
        int t_id = TransactionBase.create(
                conn,
                amount,
                timestamp,
                fee,
                transactor,
                TransactionType.WRITE_CHECK,
                false // should_commit
        ); // creates transaction base

        Statement stmt = conn.createStatement();
        String sql = String.format("INSERT INTO Check_transaction %s VALUES (%d, '%s', %d)"
                , "(t_id, initiator, check_no)"
                , t_id
                , initiator
                , check_no
        );
        int n = stmt.executeUpdate(sql);
        System.out.println(n + " rows affected");
        if (should_commit)
            conn.commit();

        return t_id;
    }

    public int getCheckNo() {
        return check_no;
    }
    public String getInitiator() {
        return initiator;
    }
}