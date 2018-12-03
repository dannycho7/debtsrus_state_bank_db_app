package models.account;

import java.sql.*;

public class InterestCheckingAccount extends AccountBase {
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
      int account_id = AccountBase.create(
         conn,
         balance,
         branch_name,
         AccountBase.AccountType.INTEREST_CHECKING,
         customer_tax_id,
         false // should_commit
      ); // creates account base

      Statement stmt = conn.createStatement();

      String sql = String.format("INSERT INTO Check_savings_account %s VALUES (%d)"
                  , "(account_id)"
                  , account_id
      );
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected");
      if (should_commit)
         conn.commit();

      return account_id;
   }
}