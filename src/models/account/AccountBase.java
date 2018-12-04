package models.account;

import BankUtil.*;
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
		protected String getName() {
         return name;
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

   private static void createAccountOwnership(
           Connection conn,
           String customer_tax_id,
           int account_id
   ) throws SQLException {
       Statement stmt = conn.createStatement();
       String sql = String.format("INSERT INTO Account_ownership %s VALUES ('%s', %d)"
               , "(tax_id, account_id)"
               , customer_tax_id
               , account_id
       );
       int n = stmt.executeUpdate(sql);
       System.out.println(n + " rows affected");
   }

   // returns: account_id
   protected static int create(
      Connection conn,
      int balance, // $$ in cents
      String branch_name,
      AccountType acct_type,
      String customer_tax_id,
      boolean should_commit
   ) throws SQLException {
      Statement stmt = conn.createStatement();
      int account_id = BankUtil.getUUID();

      String sql = String.format("INSERT INTO Account %s VALUES (%d, %d, %d, '%s', '%s', '%s')"
                  , "(account_id, balance, closed, branch_name, type, primary_owner)"
                  , account_id
                  , balance
                  , 0 // closed
                  , branch_name
                  , acct_type.getName()
                  , customer_tax_id
      );
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected");
      AccountBase.createAccountOwnership(
              conn,
              customer_tax_id,
              account_id
      );
      if (should_commit)
          conn.commit();

      return account_id;
   }
}