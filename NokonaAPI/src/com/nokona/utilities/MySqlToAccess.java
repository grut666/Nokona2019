package com.nokona.utilities;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nokona.model.Employee;

//import lombok.Data; 

//@Data
public class MySqlToAccess {
	private static Connection accessConn;
	private static Connection mySqlConn;

	private static int rowsDeleted;
	private static int[] insertedRows;

	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static String DB_URL = "jdbc:mysql://localhost:3306/Nokona?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useServerPrepStmts=false&rewriteBatchedStatements=true";
	private static String USER_NAME = "root";
	private static String PASSWORD = "xyz1234!";

	private static PreparedStatement psSelect;
	private static PreparedStatement psDelete;
	private static PreparedStatement psInsert;

	public static void main(String[] args) throws SQLException {
		// long startTime = System.currentTimeMillis();
		connect();
		// DatabaseMetaData dbmd = accessConn.getMetaData();
		// String[] types = { "TABLE" };
		// ResultSet rs = dbmd.getTables(null, null, "%", types);
		// while (rs.next()) {
		// Syste"
//			case "OPERATION":
//				doOperations();
//				break;
//			}
//		}

		try {
			mySqlConn.close();
			accessConn.close();
		} catch (SQLException e) {
			System.err.println("Close failed: " + e.getMessage());
		}
		System.out.println("Success open and close");
		
	}
	private static void doAll() {
		doEmployees();
//		doLaborCodes();
//		doOperations();
//		doTickets();
//		doJobs();
	}
	
	private static void doTickets() {
		doTicketHeaders();
		doTicketDetail();
	}

	private static void doJobs() {
		doJobHeaders();
		doJobDetails();
	}

	private static void connect() {
		connectToAccess();
		connectToMySQL();

	}

	private static void connectToAccess() {
// For local testing		
//	String accessDB = "jdbc:ucanaccess://C:/codebase/Data/nokona.mdb";
//      For Nokona onsite testing
		String accessDB = "jdbc:ucanaccess://T:/nokna/nokona.mdb";
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			accessConn = DriverManager.getConnection(accessDB);
		} catch (SQLException e) {
			System.err.println("Bad Access connection.  Aborting: " + e.getMessage());
			System.exit(1);
		} catch (ClassNotFoundException e) {
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

	

	private void clearMySQL() {

	}

	private void loadMySQL() {

	}

	private static void doTicketHeaders() {
//		try {
//			recordsIn = new ArrayList<>();
//			psSelect = accessConn.prepareStatement("Select * from TicketHeader Order By TicketHeader.Key");
//			ResultSet rs = psSelect.executeQuery();
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int columnCount = rsmd.getColumnCount();
//			for (int i = 1; i <= columnCount; i++) {
//				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
//			}
//			// Key - 4
//			// Job - 12
//			// DateCreated - 93
//			// Status - 12
//			// DateOfStatus - 93
//			// Quantity - 5
//			String key = rsmd.getColumnName(1);
//			String job = rsmd.getColumnName(2);
//			String dateCreated = rsmd.getColumnName(3);
//			String status = rsmd.getColumnName(4);
//			String dateOfStatus = rsmd.getColumnName(5);
//			String quantity = rsmd.getColumnName(6);
//
//			while (rs.next()) {
//				String record = rs.getInt(key) + "~" + rs.getString(job).trim() + "~" + rs.getDate(dateCreated) + "~"
//						+ rs.getString(status).trim() + "~" + rs.getDate(dateOfStatus) + "~" + rs.getInt(quantity);
//				System.out.println(record);
//				recordsIn.add(record);
//			}
//			System.out.println("Records In is " + recordsIn.size());
//			psSelect.close();
//		} catch (SQLException e) {
//
//			System.err.println(e.getMessage());
//			return;
//		}
//		System.out.println("Finished loading TicketHeaders.  Beginning deleting TicketHeaders");
//		try {
//			psDelete = mySqlConn.prepareStatement("Truncate TicketHeader");
//			rowsDeleted = psDelete.executeUpdate();
//			psDelete.close();
//		} catch (SQLException e) {
//			try {
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//			System.err.println(e.getMessage());
//			return;
//		}
//		System.out.println("Finished deleting " + rowsDeleted + " TicketHeaders.  Beginning storing TicketHeaders");
//		try {
//			psInsert = mySqlConn.prepareStatement(
//					"Insert into TicketHeader (TicketHeader.Key, JobId, CreatedDate, Status, StatusDate, Quantity) "
//							+ "values (?,?,?,?,?,?)");
//			for (String record : recordsIn) {
//				System.out.println(record);
//				String[] fields = record.split("~");
//				psInsert.setLong(1, Long.parseLong(fields[0]));
//				psInsert.setString(2, fields[1]);
//				psInsert.setDate(3, DateUtilities.stringToSQLDate(fields[2]));
//				psInsert.setString(4, fields[3]);
//				psInsert.setDate(5, DateUtilities.stringToSQLDate(fields[4]));
//				psInsert.setLong(6, Long.parseLong(fields[5]));
//				psInsert.addBatch();
//			}
//			insertedRows = psInsert.executeBatch();
//			psInsert.close();
//		} catch (SQLException e) {
//			try {
//				System.err.println(e.getMessage());
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//		}
//		System.out.println("Finished Inserting: ");
//		for (int i : insertedRows) {
//			System.out.print(i + " ");
//		}
//		System.out.println();
//		System.out.println(insertedRows.length + " Rows inserted");
//
//		try {
//			mySqlConn.commit();
//			System.out.println("Commit successful");
//		} catch (SQLException e) {
//			System.err.println("Failed commit: " + e.getMessage());
//		}

	}

	private static void doTicketDetail() {
//		try {
//			recordsIn = new ArrayList<>();
//			psSelect = accessConn.prepareStatement(
//					"Select TicketDetail.Key, Operation, Sequence, StatusDate, Status, Quantity, Rate, [Bar Code ID], LaborRate from TicketDetail Order By TicketDetail.Key");
//			ResultSet rs = psSelect.executeQuery();
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int columnCount = rsmd.getColumnCount();
//			for (int i = 1; i <= columnCount; i++) {
//				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
//			}
//			// Key - 4
//			// Operation - 12
//			// Sequence - 5
//			// StatusDate - 93
//			// Status - 12
//			// Quantity - 5
//			// Rate - 8
//			// Bar Code ID - 5
//			// LaborRate - 3
//			//
//			String key = rsmd.getColumnName(1);
//			String operation = rsmd.getColumnName(2);
//			String sequence = rsmd.getColumnName(3);
//			String statusDate = rsmd.getColumnName(4);
//			String status = rsmd.getColumnName(5);
//			String quantity = rsmd.getColumnName(6);
//			String rate = rsmd.getColumnName(7);
//			String barCodeId = rsmd.getColumnName(8);
//			String laborRate = rsmd.getColumnName(9);
//
//			while (rs.next()) {
//				String record = rs.getInt(key) + "~" + rs.getString(operation).trim() + "~" + rs.getInt(sequence) + "~"
//						+ rs.getDate(statusDate) + "~" + rs.getString(status).trim() + "~" + rs.getInt(quantity) + "~"
//						+ rs.getDouble(rate) + "~" + rs.getInt(barCodeId) + "~" + rs.getDouble(laborRate);
//
//				System.out.println(record);
//				recordsIn.add(record);
//			}
//			System.out.println("Records In is " + recordsIn.size());
//			psSelect.close();
//		} catch (SQLException e) {
//
//			System.err.println(e.getMessage());
//			return;
//		}
//		System.out.println("Finished loading TicketDetails.  Beginning deleting TicketDetails");
//		try {
//			psDelete = mySqlConn.prepareStatement("Truncate TicketDetail");
//			rowsDeleted = psDelete.executeUpdate();
//			psDelete.close();
//		} catch (SQLException e) {
//			try {
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//			System.err.println(e.getMessage());
//			return;
//		}
//		System.out.println("Finished deleting " + rowsDeleted + " TicketDetails.  Beginning storing TicketDetails");
//		try {
//			// Key - 4
//			// Operation - 12
//			// Sequence - 5
//			// StatusDate - 93
//			// Status - 12
//			// Quantity - 5
//			// Rate - 8
//			// Bar Code ID - 5
//			// LaborRate - 3
//			psInsert = mySqlConn.prepareStatement(
//					"Insert into TicketDetail (TicketDetail.Key, OpCode, Sequence, StatusDate, Status, Quantity, HourlyRateSAH, BarCodeID, LaborRate, UpdatedSequence) "
//							+ "values (?,?,?,?,?,?,?,?,?,?)");
//			for (String record : recordsIn) {
//				System.out.println(record);
//				String[] fields = record.split("~");
//				psInsert.setLong(1, Long.parseLong(fields[0]));
//				psInsert.setString(2, fields[1]);
//				psInsert.setLong(3, Long.parseLong(fields[2]));
//				psInsert.setDate(4, DateUtilities.stringToSQLDate(fields[3]));
//				psInsert.setString(5, fields[4]);
//				psInsert.setLong(6, Long.parseLong(fields[5]));
//				psInsert.setDouble(7, Double.parseDouble(fields[6]));
//				psInsert.setLong(8, Long.parseLong(fields[7]));
//				psInsert.setDouble(9, Double.parseDouble(fields[8]));
//				psInsert.setLong(10, Long.parseLong(fields[2])); // Set updatedsequence to sequence
//				psInsert.addBatch();
//			}
//			insertedRows = psInsert.executeBatch();
//			psInsert.close();
//		} catch (SQLException e) {
//			try {
//				System.err.println(e.getMessage());
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//		}
//		System.out.println("Finished Inserting: ");
//		for (int i : insertedRows) {
//			System.out.print(i + " ");
//		}
//		System.out.println();
//		System.out.println(insertedRows.length + " Rows inserted");
//
//		try {
//			mySqlConn.commit();
//			System.out.println("Commit successful");
//		} catch (SQLException e) {
//			System.err.println("Failed commit: " + e.getMessage());
//		}

	}

	private static void doJobHeaders() {
//		try {
//			recordsIn = new ArrayList<>();
//			psSelect = accessConn.prepareStatement(
//					"Select Job.key, jobcode, upper(description), category, stdquantity from Job Order By Job.Key");
//			ResultSet rs = psSelect.executeQuery();
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int columnCount = rsmd.getColumnCount();
//			for (int i = 1; i <= columnCount; i++) {
//				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
//			}
//			// Key - 4
//			// JobCode - 12
//			// Description - 12
//			// Category - 12
//			// StdQuantity - 5
//			String key = rsmd.getColumnName(1);
//			String job = rsmd.getColumnName(2);
//			String description = rsmd.getColumnName(3);
//			String category = rsmd.getColumnName(4);
//			String stdquantity = rsmd.getColumnName(5);
//
//			while (rs.next()) {
//				String record = rs.getInt(key) + "~" + rs.getString(job).trim() + "~" + rs.getString(description).trim()
//						+ "~" + rs.getString(category).trim() + "~" + rs.getInt(stdquantity);
//				System.out.println(record);
//				recordsIn.add(record);
//			}
//			System.out.println("Records In is " + recordsIn.size());
//			psSelect.close();
//		} catch (SQLException e) {
//
//			System.err.println(e.getMessage());
//			return;
//		}
//		System.out.println("Finished loading JobHeaders.  Beginning deleting JobHeaders");
//		try {
//			psDelete = mySqlConn.prepareStatement("Truncate JobHeader");
//			rowsDeleted = psDelete.executeUpdate();
//			psDelete.close();
//		} catch (SQLException e) {
//			try {
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//			System.err.println(e.getMessage());
//			return;
//		}
//		// Key - 4
//		// JobCode - 12
//		// Description - 12
//		// Category - 12
//		// StdQuantity - 5
//		System.out.println("Finished deleting " + rowsDeleted + " JobHeaders.  Beginning storing JobHeaders");
//		try {
//			psInsert = mySqlConn.prepareStatement(
//					"Insert into JobHeader (JobHeader.Key, JobId, Description, JobType, StandardQuantity) "
//							+ "values (?,?,?,?,?)");
//			for (String record : recordsIn) {
//				System.out.println(record);
//				String[] fields = record.split("~");
//				psInsert.setLong(1, Long.parseLong(fields[0]));
//				psInsert.setString(2, fields[1]);
//				psInsert.setString(3, fields[2]);
//				psInsert.setString(4, fields[3]);
//				psInsert.setLong(5, Long.parseLong(fields[4]));
//
//				psInsert.addBatch();
//			}
//			insertedRows = psInsert.executeBatch();
//			psInsert.close();
//		} catch (SQLException e) {
//			try {
//				System.err.println(e.getMessage());
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//		}
//		System.out.println("Finished Inserting: ");
//		for (int i : insertedRows) {
//			System.out.print(i + " ");
//		}
//		System.out.println();
//		System.out.println(insertedRows.length + " Rows inserted");
//
//		try {
//			mySqlConn.commit();
//			System.out.println("Commit successful");
//		} catch (SQLException e) {
//			System.err.println("Failed commit: " + e.getMessage());
//		}

	}

	private static void doJobDetails() {
//		try {
//			recordsIn = new ArrayList<>();
//			// psSelect = accessConn.prepareStatement("Select * from SegmentOp order by
//			// segment, sequence, operation");
//			psSelect = accessConn.prepareStatement(
//					"Select * from SegmentOp where segment in (select jobcode from job) order by segment, sequence, operation");
//
//			ResultSet rs = psSelect.executeQuery();
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int columnCount = rsmd.getColumnCount();
//			for (int i = 1; i <= columnCount; i++) {
//				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
//			}
//			// Segment - 12
//			// Operation - 12
//			// Sequence - 5
//
//			String jobid = rsmd.getColumnName(1);
//			String operation = rsmd.getColumnName(2);
//			String sequence = rsmd.getColumnName(3);
//
//			while (rs.next()) {
//				String record = rs.getString(jobid).trim() + "~" + rs.getString(operation).trim() + "~"
//						+ rs.getLong(sequence);
//				System.out.println(record);
//				recordsIn.add(record);
//			}
//			System.out.println("Records In is " + recordsIn.size());
//			// psSelect.close();
//		} catch (SQLException e) {
//
//			System.err.println(e.getMessage());
//			return;
//		}
//		System.out.println("Finished loading JobDetails.  Beginning deleting JobDetails");
//		try {
//			psDelete = mySqlConn.prepareStatement("Truncate JobDetail");
//			rowsDeleted = psDelete.executeUpdate();
//			psDelete.close();
//		} catch (SQLException e) {
//			try {
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//			System.err.println(e.getMessage());
//			return;
//		}
//
//		System.out.println("Finished deleting " + rowsDeleted + " JobDetails.  Beginning storing JobDetails");
//		try {
//			psInsert = mySqlConn
//					.prepareStatement("Insert into JobDetail (JobId, OpCode, Sequence) " + "values (?,?,?)");
//			for (String record : recordsIn) {
//				System.out.println(record);
//				String[] fields = record.split("~");
//
//				psInsert.setString(1, fields[0]);
//				psInsert.setString(2, fields[1]);
//				psInsert.setLong(3, Long.parseLong(fields[2]));
//				psInsert.addBatch();
//			}
//			insertedRows = psInsert.executeBatch();
//			psInsert.close();
//		} catch (SQLException e) {
//			try {
//				System.err.println(e.getMessage());
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//		}
//		System.out.println("Finished Inserting: ");
//		for (int i : insertedRows) {
//			System.out.print(i + " ");
//		}
//		System.out.println();
//		System.out.println(insertedRows.length + " Rows inserted");
//
//		try {
//			mySqlConn.commit();
//			System.out.println("Commit successful");
//		} catch (SQLException e) {
//			System.err.println("Failed commit: " + e.getMessage());
//		}

	}

	public static void doEmployees() {
		// Transfer records get created whenever anyone modifies an Employee record
		// This job will run every x minutes and check for any new updates
		// New updates (U, I, or D) will be applied to Access DB (so, there may be a lag as we transition the system from old to new)
		
		// U = Updated
		// I = Inserted
		// D = Deleted
		// Transfer records will be deleted upon success
		List<Employee>recordsIn = new ArrayList<>();
		List<String> modType = new ArrayList<>();
		List<Integer> autoKeys = new ArrayList<>();
		try {
			psSelect = mySqlConn
					.prepareStatement("Select * from Transfer_Employee where UDorI != ' ' order by Autokey");
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

			String laborCode = rsmd.getColumnName(5);
			String empId = rsmd.getColumnName(6);
			String active = rsmd.getColumnName(7);
			String mod = rsmd.getColumnName(8);
			String autoKey = rsmd.getColumnName(9);
			//
//			private long key;
//			private String lastName;
//			private String firstName;
//			private int barCodeID;
//			private int laborCode;
//			private String empId;
//			boolean active;
			while (rs.next()) {
				Employee emp = new Employee (rs.getInt(key),rs.getString(lastName),
						rs.getString(firstName), rs.getInt(barCodeId), rs.getInt(laborCode),
						rs.getString(empId), "T".equals(rs.getString(active)) ? true : false);
				recordsIn.add(emp);
				String modValue = rs.getString(mod);
				modType.add(modValue);
				autoKeys.add(rs.getInt(autoKey));
				System.out.println(emp + "  " + modValue);
			}
			psSelect.close();
			String updateString = null;
			for(int i = 0; i < autoKeys.size(); i++) {
				switch (modType.get(i)) {
				case "UPDATE":
					updateString = "Update Employee set xxx to yyy where Key = ?";

					
					break;
				case "ADD":
					updateString = "Insert into Employee set xxx to yyy where Key = ?";
					break;

				case "DELETE_BY_ID":
					updateString = "Delete Employee where Emp_ID = ?";
					break;

				case "DELETE_BY_KEY":
					updateString = "Delete Employee where Key = ?";
					break;
				}				
				
			}
			accessConn.prepareStatement(updateString);
			// Assuming success, kill all of the transfer_employee records
			psSelect = mySqlConn
					.prepareStatement("Delete from Transfer_Employee where UDorI != ' '");
			psSelect.executeUpdate();
			
			System.out.println("Records In is " + recordsIn.size());
			psSelect.close();
		} catch (SQLException e) {

			System.err.println(e.getMessage());
			return;
		}
		
	}

	public static void doLaborCodes() {

//		recordsIn = new ArrayList<>();
//		try {
//			psSelect = accessConn.prepareStatement("Select * from LaborCode order by [Labor Code]");
//			ResultSet rs = psSelect.executeQuery();
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int columnCount = rsmd.getColumnCount();
//			for (int i = 1; i <= columnCount; i++) {
//				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
//			}
//			// Labor Code - 5
//			// Description - 12
//			// Labor Rate - 3
//			// Key - 4
//			String laborCode = rsmd.getColumnName(1);
//			String description = rsmd.getColumnName(2);
//			String laborRate = rsmd.getColumnName(3);
//			String key = rsmd.getColumnName(4);
//
//			while (rs.next()) {
//				String record = rs.getInt(laborCode) + "~" + rs.getString(description).trim() + "~"
//						+ rs.getDouble(laborRate) + "~" + rs.getInt(key);
//				System.out.println(record);
//				recordsIn.add(record);
//			}
//			System.out.println("Records In is " + recordsIn.size());
//			psSelect.close();
//		} catch (SQLException e) {
//
//			System.err.println(e.getMessage());
//			return;
//		}
//		System.out.println("Finished loading laborcodes.  Beginning deleting laborcodes");
//		try {
//			psDelete = mySqlConn.prepareStatement("Truncate LaborCode");
//			rowsDeleted = psDelete.executeUpdate();
//			psDelete.close();
//		} catch (SQLException e) {
//			try {
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//			System.err.println(e.getMessage());
//			return;
//		}
//		System.out.println("Finished deleting " + rowsDeleted + " laborcodes. Beginning storing laborcodes");
//		try {
//			psInsert = mySqlConn.prepareStatement(
//					"Insert into LaborCode (LaborCode, Description, LaborRate, LaborCode.Key) " + "values (?,?,?,?)");
//			for (String record : recordsIn) {
//				System.out.println(record);
//				String[] fields = record.split("~");
//				psInsert.setLong(1, Long.parseLong(fields[0]));
//				psInsert.setString(2, fields[1]);
//				psInsert.setDouble(3, Double.parseDouble(fields[2]));
//				psInsert.setLong(4, Long.parseLong(fields[3]));
//				psInsert.addBatch();
//			}
//			insertedRows = psInsert.executeBatch();
//			psInsert.close();
//		} catch (SQLException e) {
//			try {
//				System.err.println(e.getMessage());
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//		}
//		System.out.println("Finished Inserting: ");
//		for (int i : insertedRows) {
//			System.out.print(i + " ");
//		}
//		System.out.println();
//		System.out.println(insertedRows.length + " Rows inserted");
//
//		try {
//			mySqlConn.commit();
//			System.out.println("Commit successful");
//		} catch (SQLException e) {
//			System.err.println("Failed commit: " + e.getMessage());
//		}
	}

	public static void doOperations() {
//		System.out.println("-----------------------------------");
//		recordsIn = new ArrayList<>();
//		try {
//			psSelect = accessConn.prepareStatement(
//					"Select opCode, Description, HourlyRateSAH, LaborCode, Operation.Key, LastStudyYear from Operation order by opcode");
//			ResultSet rs = psSelect.executeQuery();
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int columnCount = rsmd.getColumnCount();
//			for (int i = 1; i <= columnCount; i++) {
//				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
//			}
//			// OpCode - 12
//			// Description - 12
//			// HourlyRateSAH - 8
//			// LaborCode - 5
//			// Key - 4
//			// LastStudyYear - 5
//			String opCode = rsmd.getColumnName(1);
//			String description = rsmd.getColumnName(2);
//			String hourlyRateSAH = rsmd.getColumnName(3);
//			String laborCode = rsmd.getColumnName(4);
//			String key = rsmd.getColumnName(5);
//			String lastStudyYear = rsmd.getColumnName(6);
//
//			//
//			while (rs.next()) {
//				String record = rs.getString(opCode).trim() + "~" + rs.getString(description).trim() + "~"
//						+ rs.getDouble(hourlyRateSAH) + "~" + rs.getInt(laborCode) + "~" + rs.getInt(key) + "~"
//						+ rs.getInt(lastStudyYear);
//				;
//				// System.out.println(record);
//				recordsIn.add(record);
//			}
//			System.out.println("Records In is " + recordsIn.size());
//			psSelect.close();
//		} catch (SQLException e) {
//
//			System.err.println(e.getMessage());
//			return;
//		}
//		System.out.println("Finished loading opcodes. Beginning deleting opcodes");
//		try {
//			psDelete = mySqlConn.prepareStatement("Truncate Operation");
//			rowsDeleted = psDelete.executeUpdate();
//			psDelete.close();
//		} catch (SQLException e) {
//			try {
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//			System.err.println(e.getMessage());
//			return;
//		}
//		System.out.println("Finished deleting " + rowsDeleted + " opcodes.	 Beginning storing opcodes");
//		try {
//			psInsert = mySqlConn.prepareStatement(
//					"Insert into Operation (opCode, Description, HourlyRateSAH, LaborCode, Operation.Key, LastStudyYear) "
//							+ "values (?,?,?,?,?,?)");
//			for (String record : recordsIn) {
//				// System.out.println(record);
//				String[] fields = record.split("~");
//				psInsert.setString(1, fields[0]);
//				psInsert.setString(2, fields[1]);
//				psInsert.setDouble(3, Double.parseDouble(fields[2]));
//				psInsert.setLong(4, Long.parseLong(fields[3]));
//				psInsert.setLong(5, Long.parseLong(fields[4]));
//				psInsert.setLong(6, Long.parseLong(fields[5]));
//				psInsert.addBatch();
//			}
//			insertedRows = psInsert.executeBatch();
//			psInsert.close();
//		} catch (SQLException e) {
//			try {
//				System.err.println(e.getMessage());
//				mySqlConn.rollback();
//			} catch (SQLException e1) {
//				System.err.println("Failed rollback: " + e1.getMessage());
//			}
//		}
//		System.out.println("Finished Inserting: ");
//		for (int i : insertedRows) {
//			System.out.print(i + " ");
//		}
//		System.out.println();
//		System.out.println(insertedRows.length + " Rows inserted");
//
//		try {
//			mySqlConn.commit();
//			System.out.println("Commit successful");
//		} catch (SQLException e) {
//			System.err.println("Failed commit: " + e.getMessage());
//		}
	}

}
