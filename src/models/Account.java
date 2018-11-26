package models;

import java.sql.*;

public class Account {
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

   private int account_id;
   private int balance;
   private boolean closed;
   private String branch_name;
   private AccountType acct_type;
   private String customer_tax_id;

   Account(
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

   public static void create(
      Connection conn,
      int balance, // $$ in cents
      String branch_name,
      AccountType acct_type,
      String customer_tax_id
   ) throws SQLException {
      Statement stmt = null;
      System.out.println("Creating statement...");
      stmt = conn.createStatement();
      String sql = String.format("INSERT INTO Account %s VALUES (%d, %d, %d, %s, %s, %s)"
                  , "(account_id, balance, closed, branch_name, type, primary_owner)"
                  , 1 // account_id
                  , balance
                  , 0 // closed
                  , branch_name
                  , acct_type.name()
                  , customer_tax_id
      );
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected");
   }
}