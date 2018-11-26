package accessors;

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

   public static void create(AccountType acct_type) {
      Connection conn = null;
      Statement stmt = null;
      try {
         conn = JDBCConnectionManager.getConnection();
         System.out.println("Creating statement...");
         stmt = conn.createStatement();

         String sql = "SELECT cid, cname, city, discount FROM cs174.Customers";
         ResultSet rs = stmt.executeQuery(sql);
         while (rs.next()) {
            //Retrieve by column name
            String cid  = rs.getString("cid");
            String cname = rs.getString("cname");
            String city = rs.getString("city");
            double discount = rs.getDouble("discount");

            System.out.print("cid: " + cid);
            System.out.print(", cname: " + cname);
            System.out.print(", city: " + city);
            System.out.println(", discount: " + discount);
         }
         rs.close();
      } catch(SQLException se) {
         // Handle errors for JDBC
         se.printStackTrace();
      } finally {
         JDBCConnectionManager.closeConnection(conn, stmt);
      }
   }
}