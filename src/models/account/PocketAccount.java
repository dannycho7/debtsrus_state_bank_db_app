package models.account;

import models.transaction.*;
import bank_util.*;
import java.sql.*;
import java.util.*;

public class PocketAccount extends AccountBase {

   protected int linked_account_id;
   public static Set<String> valid_linked_account_types = new HashSet<String>(Arrays.asList(
      AccountType.STUDENT_CHECKING.getName(),
      AccountType.INTEREST_CHECKING.getName(),
      AccountType.SAVINGS.getName()
   ));

   public PocketAccount(
      int account_id,
      int balance,
      boolean closed,
      String branch_name,
      AccountType acct_type,
      String customer_tax_id,
      int linked_account_id
   ) {
      super(account_id, balance, closed, branch_name, acct_type, customer_tax_id);
      this.linked_account_id = linked_account_id;
   }

   private static boolean verifyLinkedAccountId(
      Connection conn,
      String initiator,
      int linked_account_id
   ) throws SQLException {
      String sql = String.format("SELECT type FROM Account A " + 
                                 "JOIN Account_ownership Ao ON A.account_id = Ao.account_id " +
                                 "WHERE A.account_id='%s' AND Ao.tax_id = '%s' AND A.closed = 0 AND A.balance > 0"
                  , linked_account_id
                  , initiator);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      boolean is_valid = false;
      while (rs.next()) {
         String account_type = rs.getString("type");
         if (valid_linked_account_types.contains(account_type)) {
             is_valid = true;
             break;
         }
      }
      rs.close();
      stmt.close();
      return is_valid;
   }

   public static void create(
      Connection conn,
      int account_id,
      int balance, // $$ in cents
      String branch_name,
      String customer_tax_id,
      String[] other_owners,
      String initiator,
      int linked_account_id,
      boolean should_commit
   ) throws SQLException {
      if (!verifyLinkedAccountId(conn, initiator, linked_account_id)) {
         throw new IllegalArgumentException("Could not create pocket account");
      }
      AccountBase.create(
         conn,
         account_id,
         0, // balance: pocket account starts at $0 and we deposit via Top-Up
         branch_name,
         AccountType.POCKET,
         customer_tax_id,
         other_owners,
         false // should_commit
      ); // creates account base
      Statement stmt = conn.createStatement();
      String sql = String.format("INSERT INTO Pocket_account %s VALUES (%d, '%s')"
                  , "(account_id, link)"
                  , account_id
                  , linked_account_id
      );
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected");
      if (balance != 0) {
          TransactionFactory.createTopUp(
                  conn,
                  balance,
                  initiator,
                  account_id, // transactor
                  linked_account_id, // operand
                  false // should_commit
          );
      }
      if (should_commit)
         conn.commit();
   }

   public static PocketAccount find(
           Connection conn,
           int account_id
   ) throws SQLException {
       String get_pocket_account_sql = String.format("SELECT %s FROM Account A " +
                       "JOIN Pocket_account Pa ON A.account_id = Pa.account_id " +
                       "WHERE Pa.account_id = %s "
               , "A.account_id, A.balance, A.closed, A.branch_name, A.type, A.primary_owner, Pa.link"
               , account_id
       );

       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(get_pocket_account_sql);
       while (rs.next()) {
           int pocket_account_id = rs.getInt("account_id");
           int balance = rs.getInt("balance");
           boolean closed = (rs.getInt("closed") == 1);
           String branch_name = rs.getString("branch_name");
           AccountType type = AccountBase.AccountType.fromString(rs.getString("type"));
           String primary_owner = rs.getString("primary_owner");
           int linked_account_id = rs.getInt("link");

           rs.close();
           stmt.close();
           return new PocketAccount(
                   pocket_account_id,
                   balance,
                   closed,
                   branch_name,
                   type,
                   primary_owner,
                   linked_account_id
           );
       }
       rs.close();
       stmt.close();
       throw new IllegalArgumentException(String.format("Could not find PocketAccount %s", account_id));
   }

   public static PocketAccount findOpen(
           Connection conn,
           int account_id
   ) throws SQLException {
       PocketAccount account = PocketAccount.find(conn, account_id);
       if (account.isClosed()) {
           String err_msg = String.format("Found the account %d, but it was closed", account.account_id);
           throw new IllegalArgumentException(err_msg);
       }
       return account;
   }

   public int getLinkedAccountId() {
       return linked_account_id;
   }

   public boolean hasTransactionThisMonth(
           Connection conn
   ) throws SQLException {
       String find_transaction_sql = String.format("SELECT %s " +
                       "FROM Transaction T " +
                       "LEFT JOIN Binary_transaction Bt ON T.t_id = Bt.t_id " +
                       "WHERE TO_CHAR(T.timestamp, 'MM-YYYY') = '%s' AND (T.transactor = %d OR Bt.operand = %d)"
               , "T.t_id"
               , BankUtil.getCurrentMonthYear()
               , account_id
               , account_id
       );
       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(find_transaction_sql);
       boolean has_transaction = false;
       while (rs.next()) {
           has_transaction = true;
           break;
       }
       rs.close();
       stmt.close();
       return has_transaction;
   }

    public void updateBalance(
            Connection conn,
            int balance,
            boolean should_commit
    ) throws SQLException {
        super.updateBalance(
                conn,
                balance,
                false // should_commit
        );
        if (should_commit)
            conn.commit();
    }
}