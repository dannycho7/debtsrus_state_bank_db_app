import bank_util.*;
import models.*;
import models.account.*;
import java.sql.*;

public class Bank {

	public static void main(String[] args) {
		Connection conn = null;
        try {
        	conn = JDBCConnectionManager.getConnection();
			Customer.create(conn, "123456355", "Danny Cho", "6681 Berkshire Terrace", "1511", false);
			SavingsAccount.create(conn, 104050, 500, "Bank of America", "123456355", false);
			PocketAccount.create(conn, 105019, 500, "Bank of America", "123456355", 104050, false);
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