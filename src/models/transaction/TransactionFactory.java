package models.transaction;

import models.*;
import models.account.*;
import bank_util.*;
import java.sql.*;

public class TransactionFactory {
    public static int createDeposit(
        Connection conn,
        int amount,
        String initiator, // customer tax_id
        int transactor, // account_id
        boolean should_commit
    ) throws SQLException, IllegalArgumentException {
        CheckSavingsAccountBase chk_savings_account = CheckSavingsAccountBase.find(conn, transactor);
        if (!chk_savings_account.hasOwner(conn, initiator)) {
            String err_msg = String.format("Initiator %s does not own account %d", initiator, transactor);
            throw new IllegalArgumentException(err_msg);
        }
        String timestamp = BankUtil.getSQLTimeStamp();
        int t_id = UnaryTransaction.create(
                conn,
                amount,
                timestamp,
                0, // fee
                initiator,
                transactor,
                UnaryTransaction.UnaryTransactionType.DEPOSIT,
                false // should_commit
        );
        if (should_commit)
            conn.commit();

        return t_id;
    }

    public static int createTopUp(
            Connection conn,
            int amount,
            String initiator, // customer tax_id
            int transactor, // pocket account_id
            int operand, // account_id
            boolean should_commit
    ) throws SQLException, IllegalArgumentException {
        PocketAccount transactor_account = PocketAccount.findOpen(conn, transactor);
        if (!transactor_account.hasOwner(conn, initiator)) {
            String err_msg = String.format("The initiator %s does not own the transactor account %d"
                    , initiator
                    , transactor
            );
            throw new IllegalArgumentException(err_msg);
        }
        if (transactor_account.getLinkedAccountId() != operand) {
            String err_msg = String.format("The account %d is not linked to the pocket account %d"
                    , operand
                    , transactor
            );
            throw new IllegalArgumentException(err_msg);
        }
        CheckSavingsAccountBase linked_account = CheckSavingsAccountBase.findOpen(conn, operand);
        int fee = transactor_account.hasTransactionThisMonth(conn) ? 0 : 500;
        String timestamp = BankUtil.getSQLTimeStamp();
        transactor_account.updateBalance(
                conn,
                transactor_account.getBalance() + amount - fee,
                false // should_commit
        );
        linked_account.updateBalance(
                conn,
                linked_account.getBalance() - amount,
                false // should_commit
        );

        int t_id = BinaryTransaction.create(
                conn,
                amount,
                timestamp,
                fee,
                initiator,
                transactor,
                BinaryTransaction.BinaryTransactionType.TOP_UP,
                operand,
                false // should_commit
        );
        if (should_commit)
            conn.commit();

        return t_id;
    }

    public static int createWithdrawal(
            Connection conn,
            int amount,
            String initiator, // customer tax_id
            int transactor, // account_id
            boolean should_commit
    ) throws SQLException, IllegalArgumentException {
        CheckSavingsAccountBase chk_savings_account = CheckSavingsAccountBase.findOpen(conn, transactor);
        String timestamp = BankUtil.getSQLTimeStamp();
        chk_savings_account.updateBalance(
                conn,
                chk_savings_account.getBalance() - amount,
                false // should_commit
        );
        int t_id = UnaryTransaction.create(
                conn,
                amount,
                timestamp,
                0, // fee
                initiator,
                transactor,
                UnaryTransaction.UnaryTransactionType.WITHDRAWAL,
                false // should_commit
        );
        if (should_commit)
            conn.commit();

        return t_id;
    }
}