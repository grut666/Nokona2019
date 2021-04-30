package com.nokona.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.nokona.data.NokonaDatabase;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.qualifiers.BaseDaoQualifier;
@BaseDaoQualifier
public class NokonaDAO  implements NokonaDatabase {
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static String DB_URL = "jdbc:mysql://192.168.1.2:3306/Nokona?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	private static String USER_NAME = "nokona";
	private static String PASSWORD = "xyz123";
	protected Connection conn;
	// SuperClass for all DAO classes
	public NokonaDAO() throws DatabaseConnectionException {
		connectToDB(USER_NAME, PASSWORD);
	}
	public NokonaDAO(String userName, String password) throws DatabaseConnectionException {
		connectToDB(userName, password);
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	private void connectToDB(String userName, String password) throws DatabaseConnectionException {
		if (conn == null) {
			try {
				System.err.println("Logging in with " + userName + " and " + password);
				Class.forName(JDBC_DRIVER);
				conn = DriverManager.getConnection(DB_URL, userName, password);
				conn.setAutoCommit(true);
			} catch (ClassNotFoundException e) {
				throw new DatabaseConnectionException(e.getMessage(), e);

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseConnectionException(e.getMessage(), e);
			}
		}
	}
	public void rollback() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	public void commit()  {
		try {
			conn.commit();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

}
