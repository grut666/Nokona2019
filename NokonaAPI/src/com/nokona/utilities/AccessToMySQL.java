package com.nokona.utilities;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.SysexMessage;

import org.apache.commons.lang.StringUtils;

import com.nokona.enums.JobType;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.model.JobHeader;

import com.nokona.constants.Constants;
//import lombok.Data; 

//@Data
public class AccessToMySQL {
	private static Connection accessConn;
	private static Connection mySqlConn;

	private static int rowsDeleted;
	private static int[] insertedRows;

	private static PreparedStatement psSelect;
	private static PreparedStatement psDelete;
	private static PreparedStatement psInsert;
	private static List<String> recordsIn;
	private static Set<String> testSet = new HashSet<>();
	private static Map<String, Long> mapIn;

	public static void main(String[] args) throws SQLException {
		long startTime = System.currentTimeMillis();
		connect();
		DatabaseMetaData dbmd = accessConn.getMetaData();
		String[] types = { "TABLE" };
		ResultSet rs = dbmd.getTables(null, null, "%", types);
		while (rs.next()) {
			System.out.println(rs.getString("TABLE_NAME"));
		}
		// doLaborCodes();
		// doEmployees();

		// doOperations();
		// doTickets();
		doJobs();
		long endTime = System.currentTimeMillis();
		try {
			mySqlConn.close();
			accessConn.close();
		} catch (SQLException e) {
			System.err.println("Close failed: " + e.getMessage());
		}
		System.out.println("Total time is " + (endTime - startTime));
	}

	public static void doTickets() {
		// doTicketHeaders();
		doTicketDetail();
	}

	public static void doJobs() {
		// Must run both
		doJobHeaders();
		doJobDetails();
	}

	public static void connect() {
		connectToAccess();
		connectToMySQL();

	}

	private static void connectToAccess() {
		String accessDB = Constants.ACCESS_DB_URL;
		// String accessDB = "jdbc:ucanaccess://C://codebase//Data//nokona.mdb";
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
			Class.forName(Constants.MYSQL_JDBC_DRIVER);
			mySqlConn = DriverManager.getConnection(Constants.MYSQL_DB_URL, Constants.USER_NAME, Constants.PASSWORD);
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

	private static void doTicketHeaders() {
		try {
			recordsIn = new ArrayList<>();
			psSelect = accessConn.prepareStatement("Select * from TicketHeader Order By TicketHeader.Key");
			ResultSet rs = psSelect.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
			}
			// Key - 4
			// Job - 12
			// DateCreated - 93
			// Status - 12
			// DateOfStatus - 93
			// Quantity - 5
			String key = rsmd.getColumnName(1);
			String job = rsmd.getColumnName(2);
			String dateCreated = rsmd.getColumnName(3);
			String status = rsmd.getColumnName(4);
			String dateOfStatus = rsmd.getColumnName(5);
			String quantity = rsmd.getColumnName(6);

			while (rs.next()) {
				String record = rs.getInt(key) + "~" + rs.getString(job).trim() + "~" + rs.getDate(dateCreated) + "~"
						+ rs.getString(status).trim() + "~" + rs.getDate(dateOfStatus) + "~" + rs.getInt(quantity);
				System.out.println(record);
				recordsIn.add(record);
			}
			System.out.println("Records In is " + recordsIn.size());
			psSelect.close();
		} catch (SQLException e) {

			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished loading TicketHeaders.  Beginning deleting TicketHeaders");
		try {
			psDelete = mySqlConn.prepareStatement("Truncate TicketHeader");
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
		System.out.println("Finished deleting " + rowsDeleted + " TicketHeaders.  Beginning storing TicketHeaders");
		try {
			psInsert = mySqlConn.prepareStatement(
					"Insert into TicketHeader (TicketHeader.Key, JobId, CreatedDate, Status, StatusDate, Quantity, Description) "
							+ "values (?,?,?,?,?,?,?)");
			for (String record : recordsIn) {
				System.out.println(record);

				String[] fields = record.split("~");
				JobHeader jh = fetchAJobHeader(fields[1]);
				String jobDescription;
				if (jh == null) {
					jobDescription = "";
				} else {
					jobDescription = jh.getDescription();
				}
				psInsert.setLong(1, Long.parseLong(fields[0]));
				psInsert.setString(2, fields[1]);
				psInsert.setDate(3, DateUtilities.stringToSQLDate(fields[2]));
				psInsert.setString(4, fields[3]);
				psInsert.setDate(5, DateUtilities.stringToSQLDate(fields[4]));
				psInsert.setLong(6, Long.parseLong(fields[5]));
				psInsert.setString(7, jobDescription);
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
		System.out.println();
		System.out.println(insertedRows.length + " Rows inserted");

		try {
			mySqlConn.commit();
			System.out.println("Commit successful");
		} catch (SQLException e) {
			System.err.println("Failed commit: " + e.getMessage());
		}

	}

	private static void doTicketDetail() {
		try {
			recordsIn = new ArrayList<>();
			psSelect = accessConn.prepareStatement(
					"Select TD.Key, TD.Operation,TD.Sequence, StatusDate, Status, TD.Quantity, OP.HourlyRateSAH, [Bar Code ID], "
							+ " LC.[Labor Rate],  OP.LaborCode, OP.Description, LC.Description, "
							+ " JO.StdQuantity from TicketDetail TD" + " join operation OP on OP.opcode = TD.operation "
							+ " join ticketHeader TH on TD.Key = TH.Key "
							+ " join laborcode LC on OP.LaborCode = LC.[Labor Code]"
							+ " join job JO on JO.JobCode = TH.Job Order By TD.Key, TD.Sequence");
			ResultSet rs = psSelect.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
			}
			// Key - 4
			// Operation - 12
			// Sequence - 5
			// StatusDate - 93
			// Status - 12
			// Quantity - 5
			// Rate - 8
			// Bar Code ID - 5
			// LaborRate - 3
			//
			String key = rsmd.getColumnName(1);
			String operation = rsmd.getColumnName(2);
			String sequence = rsmd.getColumnName(3);
			String statusDate = rsmd.getColumnName(4);
			String status = rsmd.getColumnName(5);
			String actualQuantity = rsmd.getColumnName(6);
			String hourlyRateSAH = rsmd.getColumnName(7);
			String barCodeId = rsmd.getColumnName(8);
			String laborRate = rsmd.getColumnName(9);
			String laborCode = rsmd.getColumnName(10);
			String laborDesc = rsmd.getColumnName(11);
			String opDesc = rsmd.getColumnName(12);
			String stdQuantity = rsmd.getColumnName(13);

			// while (rs.next()) {
			// String record = rs.getInt(key) + "~" + rs.getString(operation).trim() + "~" +
			// rs.getInt(sequence) + "~"
			// + rs.getDate(statusDate) + "~" + rs.getString(status).trim() + "~" +
			// rs.getInt(actualQuantity) + "~"
			// + rs.getDouble(hourlyRateSAH) + "~" + rs.getInt(barCodeId) + "~" +
			// rs.getDouble(laborRate) +
			// "~" + rs.getInt(laborCode) + "~" + rs.getString(laborDesc) + "~" +
			// rs.getString(opDesc) + "~" + rs.getInt(stdQuantity);
			//
			// System.out.println(record);
			// recordsIn.add(record);
			// }
			while (rs.next()) {
				String record = rs.getInt(1) + "~" + rs.getString(2).trim() + "~" + rs.getInt(3) + "~" + rs.getDate(4)
						+ "~" + rs.getString(5).trim() + "~" + rs.getInt(6) + "~" + rs.getDouble(7) + "~" + rs.getInt(8)
						+ "~" + rs.getDouble(9) + "~" + rs.getInt(10) + "~" + rs.getString(11) + "~" + rs.getString(12)
						+ "~" + rs.getInt(13);

				System.out.println(record);
				recordsIn.add(record);
			}
			System.out.println("Records In is " + recordsIn.size());
			psSelect.close();
		} catch (SQLException e) {

			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished loading TicketDetails.  Beginning deleting TicketDetails");
		try {
			psDelete = mySqlConn.prepareStatement("Truncate TicketDetail");
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
		System.out.println("Finished deleting " + rowsDeleted + " TicketDetails.  Beginning storing TicketDetails");
		try {
			// Key - 4
			// Operation - 12
			// Sequence - 5
			// StatusDate - 93
			// Status - 12
			// Quantity - 5
			// Rate - 8
			// Bar Code ID - 5
			// LaborRate - 3
			psInsert = mySqlConn.prepareStatement(
					"Insert into TicketDetail (TicketDetail.Key, OpCode, Sequence, StatusDate, Status, StandardQuantity, HourlyRateSAH, BarCodeID, LaborRate, "
							+ "UpdatedSequence, ActualQuantity, OperationDescription, LaborDescription, LaborCode) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			String lastKey = "";
			for (String record : recordsIn) {
				System.out.println(record);
				String[] fields = record.split("~");
				String thisKey = fields[0] + fields[1] + fields[2];
				// The following was put in because of duplicates in Access DB that are being
				// rejected from MySQL
				if (thisKey.equals(lastKey)) {
					continue;
				}
				lastKey = thisKey;
				psInsert.setLong(1, Long.parseLong(fields[0]));
				psInsert.setString(2, fields[1]);
				psInsert.setLong(3, Long.parseLong(fields[2]));
				psInsert.setDate(4, DateUtilities.stringToSQLDate(fields[3]));
				psInsert.setString(5, fields[4]);
				psInsert.setLong(6, Long.parseLong(fields[12]));
				psInsert.setDouble(7, Double.parseDouble(fields[6]));
				psInsert.setLong(8, Long.parseLong(fields[7]));
				psInsert.setDouble(9, Double.parseDouble(fields[8]));
				psInsert.setLong(10, Long.parseLong(fields[2])); // Set updatedsequence to sequence
				psInsert.setLong(11, Integer.parseInt(fields[5]));
				psInsert.setString(12, fields[10]);
				psInsert.setString(13, fields[11]);
				psInsert.setLong(14, Long.parseLong(fields[9]));
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
		System.out.println();
		System.out.println(insertedRows.length + " Rows inserted");

		try {
			mySqlConn.commit();
			System.out.println("Commit successful");
		} catch (SQLException e) {
			System.err.println("Failed commit: " + e.getMessage());
		}

	}

	private static void doJobHeaders() {
		try {
			recordsIn = new ArrayList<>();
			psSelect = accessConn.prepareStatement(
					"Select Job.key, jobcode, upper(description), category, stdquantity from Job Order By JobCode, Job.Key");
			ResultSet rs = psSelect.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
			}
			// Key - 4
			// JobCode - 12
			// Description - 12
			// Category - 12
			// StdQuantity - 5
			String key = rsmd.getColumnName(1);
			String job = rsmd.getColumnName(2);
			String description = rsmd.getColumnName(3);
			String category = rsmd.getColumnName(4);
			String stdquantity = rsmd.getColumnName(5);
			int counter = 01;
			String lastJob = "";
			mapIn = new HashMap<>();
			while (rs.next()) {
				String thisJob = rs.getString(job).trim();
				if (thisJob.equals(lastJob)) {
					thisJob = thisJob + "-" + String.format("%02d", ++counter);
					// System.out.println("This job is " + thisJob);
				} else {
					counter = 1;
					lastJob = thisJob;
				}
				String record = rs.getLong(key) + "~" + thisJob + "~" + rs.getString(description).trim() + "~"
						+ rs.getString(category).trim() + "~" + rs.getInt(stdquantity);
				// System.out.println(record);
				recordsIn.add(record);
			}
			System.out.println("Records In is " + recordsIn.size());
			psSelect.close();
		} catch (SQLException e) {

			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished loading JobHeaders.  Beginning deleting JobHeaders");
		try {
			psDelete = mySqlConn.prepareStatement("Truncate JobHeader");
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
		// Key - 4
		// JobCode - 12
		// Description - 12
		// Category - 12
		// StdQuantity - 5
		System.out.println("Finished deleting " + rowsDeleted + " JobHeaders.  Beginning storing JobHeaders");
		try {
			psInsert = mySqlConn.prepareStatement(
					"Insert into JobHeader (JobHeader.Key, JobId, Description, JobType, StandardQuantity) "
							+ "values (?,?,?,?,?)");
			String lastKey = "";
			for (String record : recordsIn) {
				// System.out.println(record);
				String[] fields = record.split("~");
				String thisKey = fields[1];
				// The following was put in because of duplicates in Access DB that are being
				// rejected from MySQL
				if (thisKey.equalsIgnoreCase(lastKey)) {
					System.out.println("Skipping: " + thisKey);
					continue;
				}
				lastKey = thisKey;
				psInsert.setLong(1, Long.parseLong(fields[0]));
				psInsert.setString(2, fields[1]);
				psInsert.setString(3, fields[2]);
				psInsert.setString(4, fields[3]);
				psInsert.setLong(5, Long.parseLong(fields[4]));
				String mappedJob = fields[1];
				mappedJob = StringUtils.removeEnd(mappedJob, "-LH");
				mappedJob = StringUtils.removeEnd(mappedJob, "-RH");
				mapIn.put(mappedJob, Long.parseLong(fields[0]));
				// System.err.println("Map key is " + mappedJob);

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
		System.out.println("Finished Inserting Job Headers: ");
		for (int i : insertedRows) {
			System.out.print(i + " ");
		}
		System.out.println();
		System.out.println(insertedRows.length + " Rows inserted");

		try {
			mySqlConn.commit();
			System.out.println("Commit successful");
		} catch (SQLException e) {
			System.err.println("Failed commit: " + e.getMessage());
		}

	}

	private static void doJobDetails() {
		try {
			recordsIn = new ArrayList<>();
			psSelect = accessConn.prepareStatement("Select * from SegmentOp order by segment, sequence, operation");
			// psSelect = accessConn.prepareStatement(
			// "Select * from SegmentOp where segment in (select jobcode from job) order by
			// segment, sequence, operation");
			// "Select * from SegmentOp order by segment, sequence, operation");
			// Working on this to fix the job issue
			// psSelect = accessConn.prepareStatement(
			// "Select JS.Job, JS.Segment, JS.Sequence, SO.Segment, So.Sequence from
			// JobSegment JS join Segment S on JS.Segment = S.SegmentName "
			// + "join SegmentOP SO on JS.Segment = SO.Segment order by JS.Job, JS.Sequence,
			// SO.Sequence");
			ResultSet rs = psSelect.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				System.out.println(rsmd.getColumnName(i) + " - " + rsmd.getColumnType(i));
			}
			// Segment - 12
			// Operation - 12
			// Sequence - 5

			String jobid = rsmd.getColumnName(1);
			String operation = rsmd.getColumnName(2);
			String sequence = rsmd.getColumnName(3);
			String test;
			while (rs.next()) {
				test = rs.getString(jobid).trim();
				test = StringUtils.removeEnd(test, "-LH");
				test = StringUtils.removeEnd(test, "-RH");
				if ( ! testSet.contains(test)) {
					testSet.add(test);
				}
				
				String record = rs.getString(jobid).trim() + "~" + rs.getString(operation).trim() + "~"
						+ rs.getLong(sequence);
				System.err.println("record is " + record);
//				System.err.println("Sequence is " + rs.getLong(sequence));
				// if (record.startsWith("A")) {
				// System.out.println("Record is " + record);
				// }
				recordsIn.add(record);
			}
			System.out.println("Records In is " + recordsIn.size());
			// psSelect.close();
		} catch (SQLException e) {

			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished loading JobDetails.  Beginning deleting JobDetails");
		try {
			psDelete = mySqlConn.prepareStatement("Truncate JobDetail");
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

		System.out.println("Finished deleting " + rowsDeleted + " JobDetails.  Beginning storing JobDetails");
		try {
			psInsert = mySqlConn.prepareStatement(
					"Insert into JobDetail (JobId, OpCode, Sequence, JobDetail.Key) " + "values (?,?,?,?)");
			// String lastKey = "";;
			for (String record : recordsIn) {
				// System.out.println(record);
				String[] fields = record.split("~");
				fields[0] = StringUtils.removeEnd(fields[0], "-LH");
				fields[0] = StringUtils.removeEnd(fields[0], "-RH");
				System.out.println("After Strip: " + record + ":" + fields[0]);
				String thisKey = fields[0];
				// if (thisKey.equals(lastKey)) {
				// continue;
				// }
				// lastKey = thisKey;
				psInsert.setString(1, fields[0]);
				psInsert.setString(2, fields[1]);
				psInsert.setLong(3, Long.parseLong(fields[2]));
				if (mapIn.containsKey(thisKey)) {
					psInsert.setLong(4, mapIn.get(thisKey));
				} else {
					System.err.println("No Key match for " + fields[0]);
					continue;
				}

				try {
					psInsert.executeUpdate();
				} catch (SQLException sqe) {
					System.err.println("InsertException: " + sqe.getMessage());
				}
			}
			// insertedRows = psInsert.executeBatch();
			// psInsert.close();
		} catch (SQLException e) {
			try {
				System.err.println(e.getMessage());
				mySqlConn.rollback();
			} catch (SQLException e1) {
				System.err.println("Failed rollback: " + e1.getMessage());
			}
		}
		System.out.println("Finished Inserting Job Details: ");
		// for (int i : insertedRows) {
		// System.out.print(i + " ");
		// }
		// System.out.println();
		// System.out.println(insertedRows.length + " Rows inserted");

		try {
			mySqlConn.commit();
			System.out.println("Commit successful");
		} catch (SQLException e) {
			System.err.println("Failed commit: " + e.getMessage());
		}

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

			String laborCode = rsmd.getColumnName(5);
			String empId = rsmd.getColumnName(6);
			String active = rsmd.getColumnName(7);
			//
			while (rs.next()) {
				String record = rs.getInt(key) + "~" + rs.getString(lastName).trim() + "~"
						+ rs.getString(firstName).trim() + "~" + rs.getInt(barCodeId) + "~" + rs.getInt(laborCode) + "~"
						+ rs.getString(empId).trim() + "~" + rs.getString(active);
				System.out.println(record);
				recordsIn.add(record);
			}
			System.out.println("Records In is " + recordsIn.size());
			psSelect.close();
		} catch (SQLException e) {

			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished loading employees.  Beginning deleting employees");
		try {
			psDelete = mySqlConn.prepareStatement("Truncate Employee");
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
				if ("FALSE".equals(fields[6]) && !"GUI10".equals(fields[5])) {
					continue;
				}
				if ("Last Name".equals(fields[1])) {
					continue;
				}
				psInsert.setLong(1, Long.parseLong(fields[0]));
				psInsert.setString(2, fields[1]);
				psInsert.setString(3, fields[2]);
				if (fields[3].equals("1045")) {
					fields[5] = "VIL30";
				}
				// if (fields[3].equals("2535")) {
				// fields[5] = "THO21";
				// }
				// if (fields[3].equals("3901")) {
				// fields[5] = "MOR11";
				// }

				psInsert.setLong(4, Long.parseLong(fields[3]));
				psInsert.setLong(5, Long.parseLong(fields[4]));
				psInsert.setString(6, fields[5]);
				psInsert.setLong(7, "TRUE".equals(fields[6]) ? 1 : 0);
				psInsert.executeUpdate();
				// psInsert.addBatch();
			}
			// insertedRows = psInsert.executeBatch();
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
		// for (int i : insertedRows) {
		// System.out.print(i + " ");
		// }
		System.out.println();
		// System.out.println(insertedRows.length + " Rows inserted");

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
				String record = rs.getInt(laborCode) + "~" + rs.getString(description).trim() + "~"
						+ rs.getDouble(laborRate) + "~" + rs.getInt(key);
				System.out.println(record);
				recordsIn.add(record);
			}
			System.out.println("Records In is " + recordsIn.size());
			psSelect.close();
		} catch (SQLException e) {

			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished loading laborcodes.  Beginning deleting laborcodes");
		try {
			psDelete = mySqlConn.prepareStatement("delete from LaborCode WHERE LABORRATE > -1");
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
		System.out.println();
		System.out.println(insertedRows.length + " Rows inserted");

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
				String record = rs.getString(opCode).trim() + "~" + rs.getString(description).trim() + "~"
						+ rs.getDouble(hourlyRateSAH) + "~" + rs.getInt(laborCode) + "~" + rs.getInt(key) + "~"
						+ rs.getInt(lastStudyYear);
				;
				// System.out.println(record);
				recordsIn.add(record);
			}
			System.out.println("Records In is " + recordsIn.size());
			psSelect.close();
		} catch (SQLException e) {

			System.err.println(e.getMessage());
			return;
		}
		System.out.println("Finished loading opcodes. Beginning deleting opcodes");
		try {
			psDelete = mySqlConn.prepareStatement("Truncate Operation");
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
				// System.out.println(record);
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
		System.out.println();
		System.out.println(insertedRows.length + " Rows inserted");

		try {
			mySqlConn.commit();
			System.out.println("Commit successful");
		} catch (SQLException e) {
			System.err.println("Failed commit: " + e.getMessage());
		}
	}

	public static JobHeader fetchAJobHeader(String jobId) {
		JobHeader jobHeader = null;
		try (PreparedStatement psGetJobHeaderByJobId = mySqlConn
				.prepareStatement("Select * from JobHeader where JobID = ?")) {
			System.err.println("-----------------JOB ID IS:" + jobId + ":-----------------");
			psGetJobHeaderByJobId.setString(1, jobId);
			try (ResultSet rs = psGetJobHeaderByJobId.executeQuery();) {

				if (rs.next()) {
					jobHeader = convertJobHeaderFromResultSet(rs);
				} else {

					jobHeader = null;
				}
			}
			// xxxxxx
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return jobHeader;
	}

	public static JobHeader convertJobHeaderFromResultSet(ResultSet rs) throws SQLException {
		int key = rs.getInt("Key");
		String jobId = rs.getString("JobID");
		String description = rs.getString("Description");

		int standardQuantity = rs.getInt("standardQuantity");
		String jobTypeString = rs.getString("JobType");
		JobType jobType = JobType.UNKNOWN;
		if (JobType.contains(jobTypeString)) {
			jobType = JobType.valueOf(jobTypeString);
		}

		return new JobHeader(key, jobId, description, standardQuantity, jobType);
	}

}
