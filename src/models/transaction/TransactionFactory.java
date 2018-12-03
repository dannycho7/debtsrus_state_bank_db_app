package models.transaction;

import BankUtil.*;
import java.sql.*;

public class TransactionFactory {
    public static int createDeposit(
        Connection conn,
        int amount,
        int fee,
        String initiator, // customer tax_id
        int transactor, // account_id
        boolean should_commit
    ) throws SQLException {
        String timestamp = BankUtil.getSQLTimeStamp();
        int t_id = UnaryTransaction.create(
                conn,
                amount,
                timestamp,
                fee,
                initiator,
                transactor,
                UnaryTransaction.UnaryTransactionType.DEPOSIT,
                false // should_commit
        );

        return t_id;
    }
}