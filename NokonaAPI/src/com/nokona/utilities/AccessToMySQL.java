package com.nokona.utilities;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lombok.Data; 

@Data
public class AccessToMySQL {
	private static Connection accessConn;
	private static Connection mySqlConn;

	private static int rowsDeleted;
	private static int[] insertedRows;

	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static String DB_URL = "jdbc:mysql://localhost:3306/Nokona?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useServerPrepStmts=false&rewriteBatchedStatements=true";
	private static String USER_NAME = "root";
	private static String PASSWORD = "xyz";

	private static PreparedStatement psSelect;
	private static PreparedStatement psDelete;
	private static PreparedStatement psInsert;
	private static List<String> recordsIn;

	public static void main(String[] args) throws SQLException {
		long startTime = System.currentTimeMillis();
		connect();
		DatabaseMetaData dbmd = accessConn.getMetaData();
		String[] types = { "TABLE" };
		ResultSet rs = dbmd.getTables(null, null, "%", types);
		while (rs.next()) {
			System.out.println(rs.getString("TABLE_NAME"));
		}
		// doEmployees();
		// doLaborCodes();
		doOperations();
		long endTime = System.currentTimeMillis();
		try {
			mySqlConn.close();
			accessConn.close();
		} catch (SQLException e) {
			System.err.println("Close failed: " + e.getMessage());
		}
		System.out.println("Total time is " + (endTime - startTime));
	}

	public static void connect() {
		connectToAccess();
		connectToMySQL();

	}

	private static void connectToAccess() {
		String accessDB = "jdbc:ucanaccess://C://codebase//Nokona2019//Data//Nokona.mdb";
		try {
			accessConn = DriverManager.getConnection(accessDB);
		} catch (SQLException e) {
			System.err.println("Bad Access connection.  Aborting: " + e.getMessage());
			System.exit(1);
		}
		System.out.println("Access connection Success");
	}

	private static void connectToMySQL() {
		try {
			Class.forName(JDBC_DRIVER);
			mySqlConn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			mySqlConn.setAutoCommit(false);
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		System.out.println("MySQL connection Success");
	}

	public AccessToMySQL() {

	}

	public void clearMySQL() {

	}

	public void loadMySQL() {

	}

	public void loadTicket() {

	}

	public void loadModel() {

	}

	public static void doEmployees() {
		recordsIn = new ArrayList<>();
		try {
			psSelect = accessConn.prepareStatement("Select * from Employee order by Employee.key");
			ResultSet rs = psSelect.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
			}
			String key = rsmd.getColumnName(1);
			String lastName = rsmd.getColumnName(2);
			String firstName = rsmd.getColumnName(3);
			String barCodeId = rsmd.getColumnName(4);
			;
			String laborCode = rsmd.getColumnName(5);
			String empId = rsmd.getColumnName(6);
			String active = rsmd.getColumnName(7);
			//
			while (rs.next()) {
				String record = rs.getInt(key) + "~" + rs.getString(lastName) + "~" + rs.getString(firstName) + "~"
						+ rs.getInt(barCodeId) + "~" + rs.getString(laborCode) + "~" + rs.getString(empId) + "~"
						+ rs.getString(active);
				System.out.println(record);
				recordsIn.add(record);
			}
			psSelect.close();
		} catch (SQLException e) {

			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished loading employees.  Beginning deleting employees");
		try {
			psDelete = mySqlConn.prepareStatement("Delete from Employee");
			rowsDeleted = psDelete.executeUpdate();
			psDelete.close();
		} catch (SQLException e) {
			try {
				mySqlConn.rollback();
			} catch (SQLException e1) {
				System.err.println("Failed rollback: " + e1.getMessage());
			}
			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished deleting " + rowsDeleted + " employees.  Beginning storing employees");
		try {
			psInsert = mySqlConn.prepareStatement(
					"Insert into employee (employee.Key, LastName, FirstName, BarCodeID, LaborCode, EmpID, Active) "
							+ "values (?,?,?,?,?,?,?)");
			for (String record : recordsIn) {
				System.out.println(record);
				String[] fields = record.split("~");
				psInsert.setLong(1, Long.parseLong(fields[0]));
				psInsert.setString(2, fields[1]);
				psInsert.setString(3, fields[2]);
				psInsert.setLong(4, Long.parseLong(fields[3]));
				psInsert.setLong(5, Long.parseLong(fields[4]));
				psInsert.setString(6, fields[5]);
				psInsert.setLong(7, "TRUE".equals(fields[6]) ? 1 : 0);
				psInsert.addBatch();
			}
			insertedRows = psInsert.executeBatch();
			psInsert.close();
		} catch (SQLException e) {
			try {
				System.err.println(e.getMessage());
				mySqlConn.rollback();
			} catch (SQLException e1) {
				System.err.println("Failed rollback: " + e1.getMessage());
			}
		}
		System.out.println("Finished Inserting: ");
		for (int i : insertedRows) {
			System.out.print(i + " ");
		}

		try {
			mySqlConn.commit();
			System.out.println("Commit successful");
		} catch (SQLException e) {
			System.err.println("Failed commit: " + e.getMessage());
		}
	}

	public static void doLaborCodes() {

		recordsIn = new ArrayList<>();
		try {
			psSelect = accessConn.prepareStatement("Select * from LaborCode order by [Labor Code]");
			ResultSet rs = psSelect.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
			}
			// Labor Code - 5
			// Description - 12
			// Labor Rate - 3
			// Key - 4
			String laborCode = rsmd.getColumnName(1);
			String description = rsmd.getColumnName(2);
			String laborRate = rsmd.getColumnName(3);
			String key = rsmd.getColumnName(4);

			while (rs.next()) {
				String record = rs.getInt(laborCode) + "~" + rs.getString(description) + "~" + rs.getDouble(laborRate)
						+ "~" + rs.getInt(key);
				System.out.println(record);
				recordsIn.add(record);
			}
			psSelect.close();
		} catch (SQLException e) {

			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished loading laborcodes.  Beginning deleting laborcodes");
		try {
			psDelete = mySqlConn.prepareStatement("Delete from LaborCode");
			rowsDeleted = psDelete.executeUpdate();
			psDelete.close();
		} catch (SQLException e) {
			try {
				mySqlConn.rollback();
			} catch (SQLException e1) {
				System.err.println("Failed rollback: " + e1.getMessage());
			}
			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished deleting " + rowsDeleted + " laborcodes. Beginning storing laborcodes");
		try {
			psInsert = mySqlConn.prepareStatement(
					"Insert into LaborCode (LaborCode, Description, LaborRate, LaborCode.Key) " + "values (?,?,?,?)");
			for (String record : recordsIn) {
				System.out.println(record);
				String[] fields = record.split("~");
				psInsert.setLong(1, Long.parseLong(fields[0]));
				psInsert.setString(2, fields[1]);
				psInsert.setDouble(3, Double.parseDouble(fields[2]));
				psInsert.setLong(4, Long.parseLong(fields[3]));
				psInsert.addBatch();
			}
			insertedRows = psInsert.executeBatch();
			psInsert.close();
		} catch (SQLException e) {
			try {
				System.err.println(e.getMessage());
				mySqlConn.rollback();
			} catch (SQLException e1) {
				System.err.println("Failed rollback: " + e1.getMessage());
			}
		}
		System.out.println("Finished Inserting: ");
		for (int i : insertedRows) {
			System.out.print(i + " ");
		}

		try {
			mySqlConn.commit();
			System.out.println("Commit successful");
		} catch (SQLException e) {
			System.err.println("Failed commit: " + e.getMessage());
		}
	}

	public static void doOperations() {
		System.out.println("-----------------------------------");
		recordsIn = new ArrayList<>();
		try {
			psSelect = accessConn.prepareStatement(
					"Select opCode, Description, HourlyRateSAH, LaborCode, Operation.Key, LastStudyYear from Operation order by opcode");
			ResultSet rs = psSelect.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
			}
			// OpCode - 12
			// Description - 12
			// HourlyRateSAH - 8
			// LaborCode - 5
			// Key - 4
			// LastStudyYear - 5
			String opCode = rsmd.getColumnName(1);
			String description = rsmd.getColumnName(2);
			String hourlyRateSAH = rsmd.getColumnName(3);
			String laborCode = rsmd.getColumnName(4);
			String key = rsmd.getColumnName(5);
			String lastStudyYear = rsmd.getColumnName(6);

			//
			while (rs.next()) {
				String record = rs.getString(opCode) + "~" + rs.getString(description) + "~"
						+ rs.getDouble(hourlyRateSAH) + "~" + rs.getInt(laborCode) + "~" + rs.getInt(key) + "~"
						+ rs.getInt(lastStudyYear);
				;
				System.out.println(record);
				recordsIn.add(record);
			}
			psSelect.close();
		} catch (SQLException e) {

			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished loading opcodes. Beginning deleting opcodes");
		try {
			psDelete = mySqlConn.prepareStatement("Delete from Operation");
			rowsDeleted = psDelete.executeUpdate();
			psDelete.close();
		} catch (SQLException e) {
			try {
				mySqlConn.rollback();
			} catch (SQLException e1) {
				System.err.println("Failed rollback: " + e1.getMessage());
			}
			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished deleting " + rowsDeleted + " opcodes.	 Beginning storing opcodes");
		try {
			psInsert = mySqlConn.prepareStatement(
					"Insert into Operation (opCode, Description, HourlyRateSAH, LaborCode, Operation.Key, LastStudyYear) "
							+ "values (?,?,?,?,?,?)");
			for (String record : recordsIn) {
				System.out.println(record);
				String[] fields = record.split("~");
				psInsert.setString(1, fields[0]);
				psInsert.setString(2, fields[1]);
				psInsert.setDouble(3, Double.parseDouble(fields[2]));
				psInsert.setLong(4, Long.parseLong(fields[3]));
				psInsert.setLong(5, Long.parseLong(fields[4]));
				psInsert.setLong(6, Long.parseLong(fields[5]));
				psInsert.addBatch();
			}
			insertedRows = psInsert.executeBatch();
			psInsert.close();
		} catch (SQLException e) {
			try {
				System.err.println(e.getMessage());
				mySqlConn.rollback();
			} catch (SQLException e1) {
				System.err.println("Failed rollback: " + e1.getMessage());
			}
		}
		System.out.println("Finished Inserting: ");
		for (int i : insertedRows) {
			System.out.print(i + " ");
		}

		try {
			mySqlConn.commit();
			System.out.println("Commit successful");
		} catch (SQLException e) {
			System.err.println("Failed commit: " + e.getMessage());
		}
	}
}
