package models.account;

import java.sql.*;

public class StudentCheckingAccount extends CheckSavingsAccountBase {
   StudentCheckingAccount(
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
              CheckSavingsAccountBase.CheckSavingsAccountType.STUDENT_CHECKING,
              customer_tax_id,
              false // should_commit
      ); // creates account base
      if (should_commit)
         conn.commit();
      return account_id;
   }
}