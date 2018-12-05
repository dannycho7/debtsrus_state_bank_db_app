package models;

import java.sql.*;
import java.util.ArrayList;
import models.account.*;
import models.transaction.*;
import models.transaction.BinaryTransaction.BinaryTransactionType;
import models.transaction.UnaryTransaction.UnaryTransactionType;

public class Customer {
   private String tax_id;
   private String name;
   private String address;
   private String pin;

   public Customer(
      String tax_id,
      String name,
      String address,
      String pin
   ) {
      this.tax_id = tax_id;
      this.name = name;
      this.address = address;
      this.pin = pin;
   }

   public static ArrayList<Customer> all(
           Connection conn
   ) throws SQLException {
       ArrayList<Customer> customers = new ArrayList<Customer>();
       String get_customers_sql = "SELECT C.tax_id, C.name, C.address, C.pin FROM Customer C";
       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(get_customers_sql);
       while (rs.next()) {
           String tax_id = rs.getString("tax_id");
           String name = rs.getString("name");
           String address = rs.getString("address");
           String pin = rs.getString("pin");
           Customer customer = new Customer(
                   tax_id,
                   name,
                   address,
                   pin
           );
           customers.add(customer);
       }
       return customers;
   }

   public static void create(
      Connection conn,
      String tax_id,
      String name,
      String address,
      String pin,
      boolean should_commit
   ) throws SQLException {
      Statement stmt = conn.createStatement();
      Pin.create(
              conn,
              pin,
              false // should_commit
      );

      String sql = String.format("INSERT INTO Customer %s VALUES ('%s', '%s', '%s', '%s')"
                  , "(tax_id, name, address, pin)"
                  , tax_id
                  , name
                  , address
                  , pin
      );
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected");
      if (should_commit)
         conn.commit();
   }

   public static void deleteAllWithNoAccounts(
           Connection conn,
           boolean should_commit
   ) throws SQLException {
      String no_accounts_sql = "SELECT Ao.tax_id FROM Account_ownership Ao GROUP BY Ao.tax_id HAVING COUNT(*) = 0";
      String delete_customers_sql = String.format("DELETE FROM Customer C " +
                      "WHERE C.tax_id IN (%s)"
              , no_accounts_sql
      );
      Statement stmt = conn.createStatement();
      int n = stmt.executeUpdate(delete_customers_sql);
      System.out.println(n + " rows deleted");
      if (should_commit)
         conn.commit();
   }

   public static Customer find(
           Connection conn,
           String customer_tax_id
   ) throws SQLException, IllegalArgumentException {
      String get_customer_sql = String.format("SELECT %s FROM Customer C WHERE C.tax_id = %s "
              , "C.tax_id, C.name, C.address, C.pin"
              , customer_tax_id
      );

      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(get_customer_sql);
      while (rs.next()) {
         String tax_id = rs.getString("tax_id");
         String name = rs.getString("name");
         String address = rs.getString("address");
         String pin = rs.getString("pin");

         return new Customer(
                 tax_id,
                 name,
                 address,
                 pin
         );
      }

      throw new IllegalArgumentException(String.format("Could not find Customer %s", customer_tax_id));
   }

   public String getTaxId() {
      return tax_id;
   }
   public String getName() {
      return name;
   }
   public String getAddress() {
      return address;
   }
   public String getPin() {
      return pin;
   }

   public ArrayList<AccountBase> genAccounts(
           Connection conn
   ) throws SQLException {
      ArrayList<AccountBase> accounts = new ArrayList<AccountBase>();
      String find_accounts_sql = String.format("SELECT %s " +
                      "FROM Account A " +
                      "JOIN Account_ownership Ao ON A.account_id = Ao.account_id " +
                      "WHERE Ao.tax_id = '%s' "
              , "A.account_id, A.balance, A.closed, A.branch_name, A.type, A.primary_owner"
              , this.tax_id
      );
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(find_accounts_sql);
      while (rs.next()) {
         int account_id = rs.getInt("account_id");
         int balance = rs.getInt("balance");
         boolean closed = (rs.getInt("closed") == 1);
         String branch_name = rs.getString("branch_name");
         AccountBase.AccountType type = AccountBase.AccountType.fromString(rs.getString("type"));
         String primary_owner = rs.getString("primary_owner");

         AccountBase account = new AccountBase(
                 account_id,
                 balance,
                 closed,
                 branch_name,
                 type,
                 primary_owner
         );

         accounts.add(account);
      }
      return accounts;
   }

   public ArrayList<Customer> genCustomersForDTER(
           Connection conn
   ) throws SQLException {
       ArrayList<Customer> dter_customers = new ArrayList<Customer>();
       ArrayList<Customer> customers = Customer.all(conn);
       for (Customer customer : customers) {
           int total = 0;
           ArrayList<AccountBase> accounts = customer.genAccounts(conn);
           for (AccountBase account : accounts) {
               ArrayList<BinaryTransaction> binary_transactions = account.genBinaryTransactionsThisMonth(conn);
               for (BinaryTransaction binary_transaction : binary_transactions) {
                   if (binary_transaction.getInitiator() == customer.getTaxId() &&
                           (binary_transaction.isTransfer() || binary_transaction.isWire())) {
                       total += binary_transaction.getAmount();
                   }
               }
               ArrayList<UnaryTransaction> unary_transactions = account.genUnaryTransactionsThisMonth(conn);
               for (UnaryTransaction unary_transaction : unary_transactions) {
                   if (unary_transaction.getInitiator() == customer.getTaxId() && unary_transaction.isDeposit()) {
                       total += unary_transaction.getAmount();
                   }
               }
           }
           if (total > 10000 * 100) {
               dter_customers.add(customer);
           }
       }
       return dter_customers;
   }
}