package com.nokona.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.enterprise.inject.Default;


import com.nokona.exceptions.DatabaseException;

public class NokonaDAO {
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static String DB_URL = "jdbc:mysql://localhost:3306/Nokona";
	private static String USER_NAME = "root";
	private static String PASSWORD = "xyz123";
	protected Connection conn;
	// SuperClass for all DAO classes
	public NokonaDAO() throws DatabaseException {
		connectToDB();
	}

	protected Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	private void connectToDB() throws DatabaseException {
		if (conn == null) {
			try {
				Class.forName(JDBC_DRIVER);
				conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
				conn.setAutoCommit(true);
			} catch (ClassNotFoundException e) {
				System.err.println("Class not found: " + e.getMessage());

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage(), e);
			}
		}
	}

}
