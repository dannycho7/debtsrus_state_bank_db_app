package models.account;

import bank_util.*;
import java.sql.*;
import java.util.ArrayList;
import models.Customer;
import models.transaction.*;
import models.transaction.TransactionBase.TransactionType;

public class AccountBase {
	public enum AccountType {
		STUDENT_CHECKING("student_checking"),
		INTEREST_CHECKING("interest_checking"),
		SAVINGS("savings"),
		POCKET("pocket");

		private final String name;
		AccountType(String n) {
			name = n;
		}
		public String getName() {
         return name;
        }
        public static AccountType fromString(String n) {
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

   public AccountBase(
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

   public void addInterestToAllOpen(
       Connection conn,
       String initiator, // customer tax_id
       boolean should_commit
   ) throws SQLException, IllegalArgumentException {
        if (AccountBase.hasInterestBeenAddedThisMonth(conn)) {
            throw new IllegalArgumentException("Interest has already been added for this month");
        }
        ArrayList<Integer> account_ids = AccountBase.genAllOpenAccountIds(conn);
        for (int account_id : account_ids) {
            TransactionFactory.createAccrueInterest(
                    conn,
                    initiator,
                    account_id,
                    false // should_commit
            );
        }
        if (should_commit)
            conn.commit();
   }

   public void modifyAccountToClose(
           Connection conn,
           boolean should_commit
   ) throws SQLException, IllegalArgumentException {
       if (this.isClosed()) {
           throw new IllegalArgumentException("Cannot close an account that is already closed");
       }
       Statement stmt = conn.createStatement();
       String modify_sql = String.format("UPDATE Account A SET A.closed = 1 WHERE A.account_id = %d" , this.account_id);
       int n = stmt.executeUpdate(modify_sql);
       System.out.println(n + " rows affected");
       AccountCloseHistory.create(
               conn,
               account_id,
               BankUtil.getCurrentMonthYear(),
               false // should_commit
       );
       if (should_commit)
           conn.commit();
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

   protected static void create(
      Connection conn,
      int account_id,
      int balance, // $$ in cents
      String branch_name,
      AccountType acct_type,
      String customer_tax_id,
      boolean should_commit
   ) throws SQLException {
      Statement stmt = conn.createStatement();

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
   }

   public static void deleteClosedAccounts(
           Connection conn,
           boolean should_commit
   ) throws SQLException {
       String delete_accounts_sql = "DELETE FROM Account A WHERE A.closed = 1";
       Statement stmt = conn.createStatement();
       int n = stmt.executeUpdate(delete_accounts_sql);
       System.out.println(n + " rows deleted");
       Customer.deleteAllWithNoAccounts(
               conn,
               false // should_commit
       );
       if (should_commit)
           conn.commit();
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

    // use (transactor, operand, type) 3-tuple to figure out delta +/- in balance from this transaction
    protected int getDeltaFromTransactionMetadata(
            int transactor,
            int operand,
            String type,
            int amount,
            int fee
    ) throws IllegalArgumentException {
        boolean is_transactor = (this.account_id == transactor);
        boolean is_operand = (this.account_id == operand);
        if (!is_transactor && !is_operand) {
            throw new IllegalArgumentException("Account is neither the transactor nor the operand");
        }
        switch (TransactionType.fromString(type)) {
            case DEPOSIT:
            case ACCRUE_INTEREST:
                return amount;
            case TOP_UP:
                if (is_transactor) {
                    return amount - fee;
                } else {
                    return -1 * amount;
                }
            case WITHDRAWAL:
            case PURCHASE:
            case WRITE_CHECK:
                return -1 * amount;
            case TRANSFER:
            case COLLECT:
            case PAY_FRIEND:
            case WIRE:
                if (is_transactor) {
                    return -1 * (amount + fee);
                } else {
                    return amount;
                }
            default:
                throw new IllegalArgumentException("Inexhaustive case");
        }
    }

   public static ArrayList<Integer> genAllOpenAccountIds(
           Connection conn
   ) throws SQLException {
       ArrayList<Integer> account_ids = new ArrayList<Integer>();
       String get_open_account_id_sql = String.format("SELECT %s FROM Account A WHERE A.closed = 0"
               , "A.account_id"
       );
       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(get_open_account_id_sql);
       while (rs.next()) {
           int account_id = rs.getInt("account_id");
           account_ids.add(account_id);
       }
       return account_ids;
   }

   protected int[] genDeltasFromTransactionsThisMonth(
           Connection conn
   ) throws SQLException, IllegalArgumentException {
       String get_transactions_this_month_sql = String.format("SELECT %s" +
                       "FROM Transaction T" +
                       "LEFT JOIN Binary_transaction Bt ON T.t_id = Bt.t_id" +
                       "WHERE TO_CHAR(T.timestamp, 'MM-YYYY') = '%s' AND (T.transactor = %d OR Bt.operand = %d)"
               , "T.t_id, T.amount, T.timestamp, T.fee, T.initiator, T.transactor, T.type, Bt.operand"
               , BankUtil.getCurrentMonthYear()
               , this.account_id
               , this.account_id
       );
       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(get_transactions_this_month_sql);
       int num_days_in_month = BankUtil.getNumDaysInCurrentMonth();
       int[] delta_per_day = new int[num_days_in_month];
       while (rs.next()) {
           int t_id = rs.getInt("t_id");
           int amount = rs.getInt("amount");
           String timestamp = rs.getString("timestamp");
           int fee = rs.getInt("fee");
           String initiator = rs.getString("initiator");
           int transactor = rs.getInt("transactor");
           String type = rs.getString("type");
           int operand = rs.getInt("operand");

           int day = Integer.parseInt(timestamp.split("-")[0]);

           int delta = getDeltaFromTransactionMetadata(
                   transactor,
                   operand,
                   type,
                   amount,
                   fee
           );
           delta_per_day[day - 1] += delta;
       }
       return delta_per_day;
   }

   public void handleZeroBalance(
           Connection conn,
           boolean should_commit
   ) throws IllegalArgumentException {
       throw new IllegalArgumentException("handleZeroBalance must be overriden by classes!");
   }

   public static boolean hasInterestBeenAddedThisMonth(
           Connection conn
   ) throws SQLException {
       String find_interest_sql = String.format("SELECT %s FROM Transaction T" +
                       "WHERE T.type = '%s' AND TO_CHAR(T.timestamp, 'MM-YYYY') = '%s'"
               , "T.t_id"
               , TransactionBase.TransactionType.ACCRUE_INTEREST.getName()
               , BankUtil.getCurrentMonthYear()
       );
       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(find_interest_sql);
       while (rs.next()) {
           int t_id = rs.getInt("t_id");
           return true;
       }
       return false;
   }

   public static boolean hasOwner(
           Connection conn,
           String owner
   ) throws SQLException {
       String find_owner_sql = String.format("SELECT %s FROM Account_ownership Ao WHERE Ao.tax_id = '%s'"
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
       if (this.isClosed()) {
           throw new IllegalArgumentException("You cannot update balance of a closed account");
       }
       if (balance < 0) {
           throw new IllegalArgumentException("Cannot change balance to negative value");
       } else if (balance == 0 || balance == 1) {
           this.handleZeroBalance(
                   conn,
                   false // should_commit
           );
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