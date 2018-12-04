package models.account;

import bank_util.*;
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
        protected static AccountType fromString(String n) {
		    switch (n) {
		        case "student_checking":
		            return AccountType.STUDENT_CHECKING;
                case "interest_checking":
                    return AccountType.INTEREST_CHECKING;
                case "savings":
                    return AccountType.SAVINGS;
                case "pocket":
                    return AccountType.POCKET;
                default:
                    return null;
            }
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

   public int getAccountId() {
       return account_id;
   }
   public int getBalance() {
       return balance;
   }
   public boolean isClosed() {
       return closed;
   }
   public String getBranchName() {
       return branch_name;
   }
   public AccountType getAccountType() {
       return acct_type;
   }
   public String getCustomerTaxId() {
       return customer_tax_id;
   }

   public static boolean hasOwner(
           Connection conn,
           String owner
   ) throws SQLException {
       String find_owner_sql = String.format("SELECT %s FROM Account_ownership Ao WHERE Ao.tax_id = %s"
               , "Ao.tax_id, Ao.account_id"
               , owner
       );
       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(find_owner_sql);
       while (rs.next()) {
           String tax_id = rs.getString("tax_id");
           int account_id = rs.getInt("account_id");
           return true;
       }
       return false;
   }

   public void updateBalance(
           Connection conn,
           int balance,
           boolean should_commit
   ) throws SQLException, IllegalArgumentException {
       if (balance < 0) {
           throw new IllegalArgumentException("Cannot change balance to negative value");
       } else if (balance == 0) {
           // close account
       } else {
           Statement stmt = conn.createStatement();
           String sql = String.format("UPDATE Account A SET A.balance = %d WHERE A.account_id = %d"
                   , balance
                   , account_id
           );
           int n = stmt.executeUpdate(sql);
           this.balance = balance;
           System.out.println(n + " rows affected");

           if (should_commit)
               conn.commit();
       }
   }
}