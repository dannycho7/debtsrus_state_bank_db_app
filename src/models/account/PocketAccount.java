package models.account;

import java.sql.*;
import java.util.*;

public class PocketAccount extends AccountBase {

   protected int linked_account_id;
   public static Set<String> valid_linked_account_types = new HashSet<String>(Arrays.asList(
      AccountType.STUDENT_CHECKING.getName(),
      AccountType.INTEREST_CHECKING.getName(),
      AccountType.SAVINGS.getName()
   ));

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



   private static boolean verifyLinkedAccountId(
      Connection conn,
      String customer_tax_id,
      int linked_account_id
   ) throws SQLException {
      String sql = String.format("SELECT type FROM Account A " + 
                                 "JOIN Account_ownership Ao ON A.account_id = Ao.account_id " +
                                 "WHERE A.account_id='%s' AND Ao.tax_id = '%s' AND balance > 0"
                  , linked_account_id
                  , customer_tax_id);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
         String account_type = rs.getString("type");
         if (valid_linked_account_types.contains(account_type)) {
            return true;
         }
      }
      return false;
   }

   // returns: account_id
   public static int create(
      Connection conn,
      int balance, // $$ in cents
      String branch_name,
      String customer_tax_id,
      int linked_account_id,
      boolean should_commit
   ) throws SQLException, IllegalArgumentException {
      if (!verifyLinkedAccountId(conn, customer_tax_id, linked_account_id)) {
         throw new SQLException("Could not create pocket account");
      }
      int account_id = AccountBase.create(
         conn,
         balance,
         branch_name,
         AccountType.POCKET,
         customer_tax_id,
         false // should_commit
      ); // creates account base
      Statement stmt = conn.createStatement();
      String sql = String.format("INSERT INTO Pocket_account %s VALUES (%d, '%s')"
                  , "(account_id, link)"
                  , account_id
                  , linked_account_id
      );
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected");
      if (should_commit)
         conn.commit();

      return account_id;
   }
}