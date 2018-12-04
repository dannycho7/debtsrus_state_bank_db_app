package models.account;

import java.sql.*;

public class CheckingAccount extends CheckSavingsAccountBase {
   public CheckingAccount(
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
      AccountType acct_type,
      String customer_tax_id,
      boolean should_commit
   ) throws SQLException {
      CheckSavingsAccountBase.create(
              conn,
              account_id,
              balance,
              branch_name,
              acct_type,
              customer_tax_id,
              false // should_commit
      ); // creates account base
      if (should_commit)
         conn.commit();
   }

   public static void createForInterest(
           Connection conn,
           int account_id,
           int balance, // $$ in cents
           String branch_name,
           String customer_tax_id,
           boolean should_commit
   ) throws SQLException {
      CheckingAccount.create(
              conn,
              account_id,
              balance,
              branch_name,
              CheckSavingsAccountType.STUDENT_CHECKING,
              customer_tax_id,
              false // should_commit
      );
   }

   public static void createForStudent(
           Connection conn,
           int account_id,
           int balance, // $$ in cents
           String branch_name,
           String customer_tax_id,
           boolean should_commit
   ) throws SQLException {
      CheckingAccount.create(
              conn,
              account_id,
              balance,
              branch_name,
              CheckSavingsAccountType.INTEREST_CHECKING,
              customer_tax_id,
              false // should_commit
      );
   }

   public static CheckingAccount find(
           Connection conn,
           int account_id
   ) throws SQLException, IllegalArgumentException {
      CheckSavingsAccountBase chk_savings_account = CheckSavingsAccountBase.find(conn, account_id);
      if (chk_savings_account.acct_type == CheckSavingsAccountType.STUDENT_CHECKING.getCorrespondingAccountType()) {
         return new CheckingAccount(
                 chk_savings_account.account_id,
                 chk_savings_account.balance,
                 chk_savings_account.closed,
                 chk_savings_account.branch_name,
                 chk_savings_account.acct_type,
                 chk_savings_account.customer_tax_id
         );
      } else {
         String err_msg = String.format("Account id %d exists, but it was not an student checking account", account_id);
         throw new IllegalArgumentException(err_msg);
      }
   }

   public static CheckingAccount findOpen(
           Connection conn,
           int account_id
   ) throws SQLException, IllegalArgumentException {
      CheckingAccount account = CheckingAccount.find(conn, account_id);
      if (account.isClosed()) {
         String err_msg = String.format("Found the account %d, but it was closed", account.account_id);
         throw new IllegalArgumentException(err_msg);
      }
      return account;
   }

   public boolean isStudentAccount() {
      return (this.acct_type == AccountType.STUDENT_CHECKING);
   }
   public boolean isInterestAccount() {
      return (this.acct_type == AccountType.INTEREST_CHECKING);
   }
}