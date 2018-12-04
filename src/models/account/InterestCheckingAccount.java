package models.account;

import java.sql.*;

public class InterestCheckingAccount extends CheckSavingsAccountBase {
   InterestCheckingAccount(
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

   // returns: account_id
   public static int create(
      Connection conn,
      int balance, // $$ in cents
      String branch_name,
      String customer_tax_id,
      boolean should_commit
   ) throws SQLException {
      int account_id = CheckSavingsAccountBase.create(
              conn,
              balance,
              branch_name,
              CheckSavingsAccountBase.CheckSavingsAccountType.INTEREST_CHECKING,
              customer_tax_id,
              false // should_commit
      ); // creates account base
      if (should_commit)
         conn.commit();
      return account_id;
   }

   public static InterestCheckingAccount find(
           Connection conn,
           int account_id
   ) throws SQLException, IllegalArgumentException {
      CheckSavingsAccountBase chk_savings_account = CheckSavingsAccountBase.find(conn, account_id);
      if (chk_savings_account.acct_type == CheckSavingsAccountType.INTEREST_CHECKING.getCorrespondingAccountType()) {
         return new InterestCheckingAccount(
                 chk_savings_account.account_id,
                 chk_savings_account.balance,
                 chk_savings_account.closed,
                 chk_savings_account.branch_name,
                 chk_savings_account.acct_type,
                 chk_savings_account.customer_tax_id
         );
      } else {
         String err_msg = String.format("Account id %d exists, but it was not an interest checking account", account_id);
         throw new IllegalArgumentException(err_msg);
      }
   }
}