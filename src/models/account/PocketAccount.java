package models.account;

import java.sql.*;

public class PocketAccount extends AccountBase{

   protected int linked_account_id;

   PocketAccount(
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

   // returns: account_id
   public static int create(
      Connection conn,
      int balance, // $$ in cents
      String branch_name,
      String customer_tax_id,
      int linked_account_id
   ) throws SQLException {
      int account_id = AccountBase.create(
         conn,
         balance,
         branch_name,
         AccountBase.AccountType.POCKET,
         customer_tax_id
      ); // creates account base

      Statement stmt = null;
      System.out.println("Creating statement...");
      stmt = conn.createStatement();

      String sql = String.format("INSERT INTO Pocket_account %s VALUES (%d, '%s')"
                  , "(account_id, link)"
                  , account_id
                  , linked_account_id
      );
      System.out.println(sql);
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected");

      return account_id;
   }
}