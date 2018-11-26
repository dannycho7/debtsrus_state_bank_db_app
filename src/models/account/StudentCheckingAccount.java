package models.account;

import java.sql.*;

public class StudentCheckingAccount extends AccountBase {
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
      String customer_tax_id
   ) throws SQLException {
      int account_id = AccountBase.create(
         conn,
         balance,
         branch_name,
         AccountBase.AccountType.STUDENT_CHECKING,
         customer_tax_id
      ); // creates account base

      Statement stmt = null;
      System.out.println("Creating statement...");
      stmt = conn.createStatement();

      String sql = String.format("INSERT INTO Check_savings_account %s VALUES (%d)"
                  , "(account_id)"
                  , account_id
      );
      System.out.println(sql);
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected");

      return account_id;
   }
}