package models.account;

import java.sql.*;

abstract public class AccountBase {
	public enum AccountType {
		STUDENT_CHECKING("student_checking"),
		INTEREST_CHECKING("interest_checking"),
		SAVINGS("savings"),
		POCKET("pocket");

		private final String name;

		AccountType(String n) {
			name = n;
		}
	}

   protected int account_id;
   protected int balance;
   protected boolean closed;
   protected String branch_name;
   protected AccountType acct_type;
   protected String customer_tax_id;

   AccountBase(
      int account_id,
      int balance,
      boolean closed,
      String branch_name,
      AccountType acct_type,
      String customer_tax_id
   ) {
      this.account_id = account_id;
      this.balance = balance;
      this.closed = closed;
      this.branch_name = branch_name;
      this.acct_type = acct_type;
      this.customer_tax_id = customer_tax_id;
   }

   // returns: account_id
   protected static int create(
      Connection conn,
      int balance, // $$ in cents
      String branch_name,
      AccountType acct_type,
      String customer_tax_id
   ) throws SQLException {
      Statement stmt = null;
      System.out.println("Creating statement...");
      stmt = conn.createStatement();

      int account_id = (int) (Math.random() * Integer.MAX_VALUE); // low chance of collision

      String sql = String.format("INSERT INTO Account %s VALUES (%d, %d, %d, '%s', '%s', '%s')"
                  , "(account_id, balance, closed, branch_name, type, primary_owner)"
                  , account_id
                  , balance
                  , 0 // closed
                  , branch_name
                  , acct_type.name
                  , customer_tax_id
      );
      System.out.println(sql);
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected");

      return account_id;
   }
}