import java.sql.*;

public class JDBCConnectionManager {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";  
   static final String DB_URL = "jdbc:oracle:thin:@cloud-34-133.eci.ucsb.edu:1521:XE";

   //  Database credentials
   static final String USERNAME = "hyunbumcho";
   static final String PASSWORD = "12345678";
   
   public static Connection getConnection() {
      Connection conn = null;
      try {
         Class.forName(JDBC_DRIVER);
         System.out.println("Connecting to a selected database...");
         conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
         System.out.println("Connected database successfully...");
      } catch(SQLException se) {
         // Handle errors for JDBC
         se.printStackTrace();
      } catch(Exception e) {
         // Handle errors for Class.forName
         e.printStackTrace();
      }
      return conn;
   }

   public static void closeConnection(Connection conn, Statement stmt) {
      try{
         if(stmt!=null)
            conn.close();
      } catch(SQLException se){} // do nothing
      try {
         if (conn!=null)
            conn.close();
      } catch (SQLException se) {
         se.printStackTrace();
      }
   }
}