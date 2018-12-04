import models.*;
import models.account.*;
import java.sql.*;

public class Bank {

	public static void main(String[] args) {
		Connection conn = null;
        try {
        	conn = JDBCConnectionManager.getConnection();
			Customer.create(conn, "123456353", "Danny Cho", "6681 Berkshire Terrace", "1510", false);
			int account_id = SavingsAccount.create(conn, 500, "Bank of America", "123456353", false);
        	PocketAccount.create(conn, 500, "Bank of America", "123456353", account_id, false);
        	conn.commit();
		} catch (SQLException se) {
			// Handle errors for JDBC
			if (conn != null)
				JDBCConnectionManager.rollbackConn(conn);
			se.printStackTrace();
        } catch (Exception ex) {
			JDBCConnectionManager.rollbackConn(conn);
			ex.printStackTrace();
		} finally {
			JDBCConnectionManager.closeConnection(
				conn,
				null // statement
			);
      	}
	}
}