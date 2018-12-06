package models.account;

import java.sql.*;

public class SavingsAccount extends CheckSavingsAccountBase {
   public SavingsAccount(
      int account_id,
      int balance,
      boolean closed,
      String branch_name,
      AccountType acct_type,
      String customer_tax_id
   ) {
      super(
         account_id,
         balance,
         closed,
         branch_name,
         acct_type,
         customer_tax_id
      );
   }

   public static void create(
      Connection conn,
      int account_id,
      int balance, // $$ in cents
      String branch_name,
      String customer_tax_id,
      boolean should_commit
   ) throws SQLException {
      CheckSavingsAccountBase.create(
              conn,
              account_id,
              balance,
              branch_name,
              CheckSavingsAccountType.SAVINGS,
              customer_tax_id,
              false // should_commit
      ); // creates account base
      if (should_commit)
         conn.commit();
   }

   public static SavingsAccount find(
           Connection conn,
           int account_id
   ) throws SQLException {
      CheckSavingsAccountBase chk_savings_account = CheckSavingsAccountBase.find(conn, account_id);
      if (chk_savings_account.acct_type == CheckSavingsAccountType.SAVINGS.getCorrespondingAccountType()) {
         return new SavingsAccount(
                 chk_savings_account.account_id,
                 chk_savings_account.balance,
                 chk_savings_account.closed,
                 chk_savings_account.branch_name,
                 chk_savings_account.acct_type,
                 chk_savings_account.customer_tax_id
         );
      } else {
         String err_msg = String.format("Account id %d exists, but it was not an savings account", account_id);
         throw new IllegalArgumentException(err_msg);
      }
   }

   public static SavingsAccount findOpen(
           Connection conn,
           int account_id
   ) throws SQLException {
      SavingsAccount account = SavingsAccount.find(conn, account_id);
      if (account.isClosed()) {
         String err_msg = String.format("Found the account %d, but it was closed", account.account_id);
         throw new IllegalArgumentException(err_msg);
      }
      return account;
   }
}