package com.nokona.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.nokona.constants.Constants;
import com.nokona.data.NokonaDatabase;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.qualifiers.BaseDaoQualifier;

@BaseDaoQualifier
public class NokonaDAO implements NokonaDatabase {
	private static final String JDBC_DRIVER = Constants.MYSQL_JDBC_DRIVER;
	private static String DB_URL = Constants.MYSQL_DB_URL;
	private static String USER_NAME = Constants.USER_NAME;
	private static String PASSWORD = Constants.PASSWORD;

	protected static Gson gson = new Gson();

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

	public void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

}
