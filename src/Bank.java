import accessors.*;
import java.sql.*;

public class Bank {

	public static void main(String[] args) {
		Connection conn = null;
        try {
        	conn = JDBCConnectionManager.getConnection();
			Account.create(conn, 500, "Bank of America", Account.AccountType.STUDENT_CHECKING, "123456789");
		} catch(SQLException se) {
         // Handle errors for JDBC
         se.printStackTrace();
      	} finally {
			JDBCConnectionManager.closeConnection(
				conn,
				null // statement
			);
      	}
	}
}