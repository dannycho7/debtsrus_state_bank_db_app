import models.*;
import models.account.*;
import java.sql.*;

public class Bank {

	public static void main(String[] args) {
		Connection conn = null;
        try {
        	conn = JDBCConnectionManager.getConnection();
			PocketAccount.create(conn, 500, "Bank of America", "123456789", 1527885980);
			// Pin.create(conn, "1717");
			// Customer.create(conn, "123456789", "Danny Cho", "6681 Berkshire Terrace");
		} catch (SQLException se) {
         // Handle errors for JDBC
         se.printStackTrace();
      	} 
      	catch (IllegalArgumentException ex) {
      		ex.printStackTrace();
      	} finally {
			JDBCConnectionManager.closeConnection(
				conn,
				null // statement
			);
      	}
	}
}