package com.nokona.utilities;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nokona.constants.Constants;
import com.nokona.model.Employee;
import com.nokona.model.LaborCode;
import com.nokona.model.Operation;

//import lombok.Data; 

//@Data
public class MySqlToAccess {
	private static Connection accessConn;
	private static Connection mySqlConn;

	private static final String JDBC_DRIVER = Constants.MYSQL_JDBC_DRIVER;
	private static String MYSQL_DB_URL = "jdbc:mysql://localhost:3306/Nokona?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
	private static String USER_NAME = Constants.USER_NAME;
	private static String PASSWORD = Constants.PASSWORD;
	private static Logger logger = Logger.getLogger(MySqlToAccess.class.getName());

	// private static PreparedStatement psSelect;
	// private static PreparedStatement psDelete;
	// private static PreparedStatement psInsert;
	// private static PreparedStatement psUpdate;

	public static void main(String[] args) throws SQLException {

		connect();

		if (args.length == 0) {
			System.err.println("No arguments");
			return;
		}
		try {
			setUpLogger();
		} catch (SecurityException e1) {
			System.err.println(e1.getMessage());
			return;
		} catch (IOException e1) {
			System.err.println(e1.getMessage());
			return;
		}
		// Arguments should be in the form of "EMP_D, EMP_U, EMP_C, EMP_A" for delete,
		// update, create, all
		for (String operation : args) {
			String[] fields = operation.split("_");
			switch (fields[0]) {
			case "EMP":
				doEmployees(fields[1]);
				break;
			case "LABOR":
				doLaborCodes(fields[1]);
				break;
			case "OPERATION":
				doOperations(fields[1]);
				break;
			case "JOBHEADER":
				doJobHeaders(fields[1]);
				break;
			case "JOBDETAIL":
				doJobDetails(fields[1]);
				break;

			case "TICKETHEADER":
				doTicketHeaders(fields[1]);
				break;
			case "TICKETDETAIL":
				doTicketDetails(fields[1]);
				break;
			}

		}

		try {
			mySqlConn.close();
			accessConn.close();
		} catch (SQLException e) {
			System.err.println("Close failed: " + e.getMessage());
		}
	}

	private static void connect() {
		connectToAccess();
		connectToMySQL();

	}

	private static void connectToAccess() {
		// For local testing
		// String accessDB = "jdbc:ucanaccess://C:/codebase/Data/nokona.mdb";
		// For Nokona onsite testing
		// Don't use T: because the task scheduler cannot see mapped drives

		// String accessDB = "jdbc:ucanaccess://E:Apps/nokona/nokona.mdb";
		// String accessDB = "jdbc:ucanaccess://E:Apps/nokona/noktest.mdb";
		String accessDB = Constants.ACCESS_DB_URL;
		try {
			Class.forName(Constants.ACCESS_JDBC_DRIVER);
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
			mySqlConn = DriverManager.getConnection(MYSQL_DB_URL, USER_NAME, PASSWORD);
			mySqlConn.setAutoCommit(true);
		} catch (ClassNotFoundException e) {
			System.err.println("Error # 1: " + e.getMessage());
			System.exit(1);

		} catch (SQLException e) {
			System.err.println("Error # 2: " + e.getMessage());
			System.exit(1);
		}
		System.out.println("MySQL connection Success");
	}

	// private void clearMySQL() {
	//
	// }

	// private void loadMySQL() {
	//
	// }
	//

	// *************************************** TICKETHEADER
	// **********************************************
	private static void doTicketHeaders(String duca) {
		switch (duca) {
		case "D":
			doTicketHeaderDelete();
			break;
		case "U":
			doTicketHeaderUpdate();
			break;
		case "C":
			doTicketHeaderCreate();
			break;

		default:
			doTicketHeaderDelete();
			doTicketHeaderUpdate();
			doTicketHeaderCreate();
		}
	}

	protected static void doTicketHeaderDelete() {
		// Key Last Name First Name Bar Code ID Labor Code Emp ID Active
		List<Employee> recordsIn = new ArrayList<>();
		String query = "Select * from Employee_Log where ProcessedToAccess = 'N'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				Employee emp = new Employee(rs.getInt("TheKey"), rs.getString("LastName"), rs.getString("FirstName"),
						rs.getInt("BarCodeID"), rs.getInt("LaborCode"), rs.getString("EmpID"),
						"T".equals(rs.getString("Active")) ? true : false);
				recordsIn.add(emp);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
		query = "DELETE FROM EMPLOYEE WHERE EMPLOYEE.KEY = ?";
		try (PreparedStatement psDelete = accessConn.prepareStatement(query)) {
			for (Employee employee : recordsIn) {
				psDelete.setLong(1, employee.getKey());
				int rowsAffected = psDelete.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update Employee_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'D'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}

	protected static void doTicketHeaderUpdate() {
		// Key Last Name First Name Bar Code ID Labor Code Emp ID Active
		List<Employee> recordsIn = new ArrayList<>();
		String query = "Select * from Employee_Log where ProcessedToAccess = 'N' and TransactionType = 'U'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				Employee emp = new Employee(rs.getInt("TheKey"), rs.getString("LastName"), rs.getString("FirstName"),
						rs.getInt("BarCodeID"), rs.getInt("LaborCode"), rs.getString("EmpID"),
						"T".equals(rs.getString("Active")) ? true : false);
				recordsIn.add(emp);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}

		query = "Update EMPLOYEE Set  [Last Name] = ?, [First Name] = ?, [Bar Code ID] = ?, [Labor Code] = ?, "
				+ "[EMP ID] = ?, Active = ? where EMPLOYEE.KEY = ?";
		try (PreparedStatement psUpdate = accessConn.prepareStatement(query)) {
			for (Employee employee : recordsIn) {
				psUpdate.setString(1, employee.getLastName());
				psUpdate.setString(2, employee.getFirstName());
				psUpdate.setLong(3, employee.getBarCodeID());
				psUpdate.setLong(4, employee.getLaborCode());
				psUpdate.setString(5, employee.getEmpId());
				psUpdate.setString(6, employee.isActive() ? "T" : "F");
				psUpdate.setLong(7, employee.getKey());

				int rowsAffected = psUpdate.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update Employee_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'U'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}

	protected static void doTicketHeaderCreate() {
		System.out.println("Entering Create ********");

		List<Employee> recordsIn = new ArrayList<>();
		String query = "Select * from Employee_Log where ProcessedToAccess = 'N' and TransactionType = 'C'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				Employee emp = new Employee(rs.getInt("TheKey"), rs.getString("LastName"), rs.getString("FirstName"),
						rs.getInt("BarCodeID"), rs.getInt("LaborCode"), rs.getString("EmpID"),
						"T".equals(rs.getString("Active")) ? true : false);
				recordsIn.add(emp);
			}
		} catch (SQLException ex) {
			System.out.println("MySQL Error: " + query + ":" + ex.getMessage());

			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}

		query = "Insert INTO EMPLOYEE ([Last Name], [First Name], [Bar Code ID], [Labor Code], "
				+ "[EMP ID], Active) values (?,?,?,?,?,?,?)";
		try (PreparedStatement psInsert = accessConn.prepareStatement(query)) {
			for (Employee employee : recordsIn) {
				psInsert.setString(1, employee.getLastName());
				psInsert.setString(2, employee.getFirstName());
				psInsert.setLong(3, employee.getBarCodeID());
				psInsert.setLong(4, employee.getLaborCode());
				psInsert.setString(5, employee.getEmpId());
				psInsert.setString(6, employee.isActive() ? "T" : "F");

				int rowsAffected = psInsert.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			System.out.println("Access Error: " + query + ":" + ex.getMessage());
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update Employee_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'C'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				System.out.println("MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			System.out.println("MySQL Error: " + query + ":" + ex.getMessage());
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}

	private static void doTicketDetails(String duca) {

	}

	private static void doJobHeaders(String duca) {

	}

	private static void doJobDetails(String duca) {

	}

	// *************************************** EMPLOYEE
	// **********************************************
	public static void doEmployees(String duca) { // delete, update, create, all

		switch (duca) {
		case "D":
			doEmployeeDelete();
			break;
		case "U":
			doEmployeeUpdate();
			break;
		case "C":
			doEmployeeCreate();
			break;

		default:
			doEmployeeDelete();
			doEmployeeUpdate();
			doEmployeeCreate();
		}

	}

	protected static void doEmployeeDelete() {
		// Key Last Name First Name Bar Code ID Labor Code Emp ID Active
		List<Employee> recordsIn = new ArrayList<>();
		String query = "Select * from Employee_Log where ProcessedToAccess = 'N'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				Employee emp = new Employee(rs.getInt("TheKey"), rs.getString("LastName"), rs.getString("FirstName"),
						rs.getInt("BarCodeID"), rs.getInt("LaborCode"), rs.getString("EmpID"),
						"T".equals(rs.getString("Active")) ? true : false);
				recordsIn.add(emp);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
		query = "DELETE FROM EMPLOYEE WHERE EMPLOYEE.KEY = ?";
		try (PreparedStatement psDelete = accessConn.prepareStatement(query)) {
			for (Employee employee : recordsIn) {
				psDelete.setLong(1, employee.getKey());
				int rowsAffected = psDelete.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update Employee_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'D'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}

	protected static void doEmployeeUpdate() {
		// Key Last Name First Name Bar Code ID Labor Code Emp ID Active
		List<Employee> recordsIn = new ArrayList<>();
		String query = "Select * from Employee_Log where ProcessedToAccess = 'N' and TransactionType = 'U'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				Employee emp = new Employee(rs.getInt("TheKey"), rs.getString("LastName"), rs.getString("FirstName"),
						rs.getInt("BarCodeID"), rs.getInt("LaborCode"), rs.getString("EmpID"),
						"T".equals(rs.getString("Active")) ? true : false);
				recordsIn.add(emp);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}

		query = "Update EMPLOYEE Set  [Last Name] = ?, [First Name] = ?, [Bar Code ID] = ?, [Labor Code] = ?, "
				+ "[EMP ID] = ?, Active = ? where EMPLOYEE.KEY = ?";
		try (PreparedStatement psUpdate = accessConn.prepareStatement(query)) {
			for (Employee employee : recordsIn) {
				psUpdate.setString(1, employee.getLastName());
				psUpdate.setString(2, employee.getFirstName());
				psUpdate.setLong(3, employee.getBarCodeID());
				psUpdate.setLong(4, employee.getLaborCode());
				psUpdate.setString(5, employee.getEmpId());
				psUpdate.setString(6, employee.isActive() ? "T" : "F");
				psUpdate.setLong(7, employee.getKey());

				int rowsAffected = psUpdate.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update Employee_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'U'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}

	protected static void doEmployeeCreate() {
		System.out.println("Entering Create ********");

		List<Employee> recordsIn = new ArrayList<>();
		String query = "Select * from Employee_Log where ProcessedToAccess = 'N' and TransactionType = 'C'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				Employee emp = new Employee(rs.getInt("TheKey"), rs.getString("LastName"), rs.getString("FirstName"),
						rs.getInt("BarCodeID"), rs.getInt("LaborCode"), rs.getString("EmpID"),
						"T".equals(rs.getString("Active")) ? true : false);
				recordsIn.add(emp);
			}
		} catch (SQLException ex) {
			System.out.println("MySQL Error: " + query + ":" + ex.getMessage());

			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}

		query = "Insert INTO EMPLOYEE ([Last Name], [First Name], [Bar Code ID], [Labor Code], "
				+ "[EMP ID], Active) values (?,?,?,?,?,?,?)";
		try (PreparedStatement psInsert = accessConn.prepareStatement(query)) {
			for (Employee employee : recordsIn) {
				psInsert.setString(1, employee.getLastName());
				psInsert.setString(2, employee.getFirstName());
				psInsert.setLong(3, employee.getBarCodeID());
				psInsert.setLong(4, employee.getLaborCode());
				psInsert.setString(5, employee.getEmpId());
				psInsert.setString(6, employee.isActive() ? "T" : "F");

				int rowsAffected = psInsert.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			System.out.println("Access Error: " + query + ":" + ex.getMessage());
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update Employee_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'C'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				System.out.println("MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			System.out.println("MySQL Error: " + query + ":" + ex.getMessage());
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}

	// *************************************** LABOR CODES
	// **********************************************
	public static void doLaborCodes(String duca) { // delete, update, create, all

		switch (duca) {
		case "D":
			doLaborCodeDelete();
			break;
		case "U":
			doLaborCodeUpdate();
			break;
		case "C":
			doLaborCodeCreate();
			break;
		default:
			doLaborCodeDelete();
			doLaborCodeUpdate();
			doLaborCodeCreate();
		}

	}
	// LaborCode Description LaborRate Key --- From Access

	protected static void doLaborCodeDelete() {
		// Key Last Name First Name Bar Code ID Labor Code Emp ID Active
		List<LaborCode> recordsIn = new ArrayList<>();
		String query = "Select * from LaborCode_Log where ProcessedToAccess = 'N'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				LaborCode laborCode = new LaborCode(rs.getInt("TheKey"), rs.getInt("LaborCode"),
						rs.getString("Description"), rs.getDouble("LaborRate"));
				recordsIn.add(laborCode);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
		query = "DELETE FROM LaborCode WHERE LaborCode.KEY = ?";
		try (PreparedStatement psDelete = accessConn.prepareStatement(query)) {
			for (LaborCode laborCode : recordsIn) {
				psDelete.setLong(1, laborCode.getKey());
				int rowsAffected = psDelete.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update LaborCode_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'D'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}

	protected static void doLaborCodeUpdate() {
		// LaborCode Description LaborRate Key --- From Access
		List<LaborCode> recordsIn = new ArrayList<>();
		String query = "Select * from LaborCode_Log where ProcessedToAccess = 'N' and TransactionType = 'U'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				LaborCode laborCode = new LaborCode(rs.getInt("TheKey"), rs.getInt("LaborCode"),
						rs.getString("Description"), rs.getDouble("LaborRate"));
				recordsIn.add(laborCode);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}

		query = "Update LaborCode Set  LaborCode = ?, Description = ?, LaborRate = ?  " + " where LaborKey.KEY = ?";
		try (PreparedStatement psUpdate = accessConn.prepareStatement(query)) {
			for (LaborCode laborCode : recordsIn) {
				psUpdate.setLong(1, laborCode.getLaborCode());
				psUpdate.setString(2, laborCode.getDescription());
				psUpdate.setDouble(3, laborCode.getRate());
				psUpdate.setLong(4, laborCode.getKey());

				int rowsAffected = psUpdate.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update LaborCode_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'U'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}

	protected static void doLaborCodeCreate() {
		// LaborCode Description LaborRate Key --- From Access
		System.out.println("Entering Create ********");

		List<LaborCode> recordsIn = new ArrayList<>();
		String query = "Select * from LaborCode_Log where ProcessedToAccess = 'N' and TransactionType = 'C'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				LaborCode laborCode = new LaborCode(rs.getInt("TheKey"), rs.getInt("LaborCode"),
						rs.getString("Description"), rs.getDouble("LaborRate"));
				recordsIn.add(laborCode);
			}
		} catch (SQLException ex) {
			System.out.println("MySQL Error: " + query + ":" + ex.getMessage());

			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}

		query = "Insert INTO LaborCode (LaborCode, Description LaborRate) " + " values (?,?,?)";
		try (PreparedStatement psInsert = accessConn.prepareStatement(query)) {
			for (LaborCode laborCode : recordsIn) {
				psInsert.setLong(1, laborCode.getLaborCode());
				psInsert.setString(2, laborCode.getDescription());
				psInsert.setDouble(3, laborCode.getRate());

				int rowsAffected = psInsert.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			System.out.println("Access Error: " + query + ":" + ex.getMessage());
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update LaborCode_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'C'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				System.out.println("MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			System.out.println("MySQL Error: " + query + ":" + ex.getMessage());
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}
	// OpCode Description HourlyRateSAH LaborCode Key LastStudyYear

	// *************************************** OPERATION
	// **********************************************
	public static void doOperations(String duca) { // delete, update, create, all

		switch (duca) {
		case "D":
			doOperationDelete();
			break;
		case "U":
			doOperationUpdate();
			break;
		case "C":
			doOperationCreate();
			break;
		default:
			doOperationDelete();
			doOperationUpdate();
			doOperationCreate();
		}

	}

	protected static void doOperationDelete() {
		// OpCode Description HourlyRateSAH LaborCode Key LastStudyYear
		List<Operation> recordsIn = new ArrayList<>();
		String query = "Select * from Operation_Log where ProcessedToAccess = 'N'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				Operation operation = new Operation(rs.getLong("TheKey"), rs.getString("OpCode"),
						rs.getString("Description"), rs.getInt("LaborCode"), rs.getDouble("HourlyRateSAH"),
						rs.getInt("LastStudyYear"));
				recordsIn.add(operation);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
		query = "DELETE FROM OPERATION WHERE OPERATION.KEY = ?";
		try (PreparedStatement psDelete = accessConn.prepareStatement(query)) {
			for (Operation operation : recordsIn) {
				psDelete.setLong(1, operation.getKey());
				int rowsAffected = psDelete.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update Operation_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'D'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}

	protected static void doOperationUpdate() {
		// Key OpCode Description LaborCode HourlyRateSAH LastStudyYear
		List<Operation> recordsIn = new ArrayList<>();
		String query = "Select * from Operation_Log where ProcessedToAccess = 'N' and TransactionType = 'U'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				Operation operation = new Operation(rs.getLong("TheKey"), rs.getString("OpCode"),
						rs.getString("Description"), rs.getInt("LaborCode"), rs.getDouble("HourlyRateSAH"),
						rs.getInt("LastStudyYear"));
				recordsIn.add(operation);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}

		query = "Update OPERATION Set  OpCode = ?, Description = ?, LevelCode = ?, HourlyRateSAH = ?, "
				+ "LastStudyYear = ? where EMPLOYEE.KEY = ?";
		try (PreparedStatement psUpdate = accessConn.prepareStatement(query)) {
			for (Operation operation : recordsIn) {
				psUpdate.setString(1, operation.getOpCode());
				psUpdate.setString(2, operation.getDescription());
				psUpdate.setInt(3, operation.getLevelCode());
				psUpdate.setDouble(4, operation.getHourlyRateSAH());
				psUpdate.setInt(5, operation.getLastStudyYear());
				psUpdate.setLong(6, operation.getKey());

				int rowsAffected = psUpdate.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update Operation_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'U'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}

	protected static void doOperationCreate() {
		// Key OpCode Description LaborCode HourlyRateSAH LastStudyYear
		System.out.println("Entering Create ********");

		List<Operation> recordsIn = new ArrayList<>();
		String query = "Select * from Operation_Log where ProcessedToAccess = 'N' and TransactionType = 'C'";
		try (PreparedStatement psSelect = mySqlConn.prepareStatement(query)) {

			ResultSet rs = psSelect.executeQuery();

			while (rs.next()) { // Should be only 1, but not sure if that will always be the case
				Operation operation = new Operation(rs.getLong("TheKey"), rs.getString("OpCode"),
						rs.getString("Description"), rs.getInt("LaborCode"), rs.getDouble("HourlyRateSAH"),
						rs.getInt("LastStudyYear"));
				recordsIn.add(operation);
			}
		} catch (SQLException ex) {
			System.out.println("MySQL Error: " + query + ":" + ex.getMessage());

			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}

		query = "Insert INTO Operation (OpCode, Description, LevelCode, HourlyRateSAH, "
				+ "LastStudyYear, Level) values (?,?,?,?,?)";
		try (PreparedStatement psInsert = accessConn.prepareStatement(query)) {
			for (Operation operation : recordsIn) {
				psInsert.setString(1, operation.getOpCode());
				psInsert.setString(2, operation.getDescription());
				psInsert.setInt(3, operation.getLevelCode());
				psInsert.setDouble(4, operation.getHourlyRateSAH());
				psInsert.setInt(5, operation.getLastStudyYear());

				int rowsAffected = psInsert.executeUpdate();
				if (rowsAffected == 0) {
					logger.log(Level.SEVERE, "Access Error: " + query + ": Rows Affected = " + rowsAffected);
				}
			}

		} catch (SQLException ex) {
			System.out.println("Access Error: " + query + ":" + ex.getMessage());
			logger.log(Level.SEVERE, "Access Error: " + query + ":" + ex.getMessage());
		}
		query = "Update Operation_Log Set ProcessedToAccess = 'Y' where ProcessedToAccess = 'N' and TransactionType = 'C'";
		try (PreparedStatement psUpdate = mySqlConn.prepareStatement(query)) {
			int rowsAffected = psUpdate.executeUpdate();
			if (rowsAffected == 0) {
				System.out.println("MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
				logger.log(Level.SEVERE, "MySQL Error: " + query + ": Rows Affected = " + rowsAffected);
			}
		} catch (SQLException ex) {
			System.out.println("MySQL Error: " + query + ":" + ex.getMessage());
			logger.log(Level.SEVERE, "MySQL Error: " + query + ":" + ex.getMessage());
		}
	}

	protected static void setUpLogger() throws SecurityException, IOException {
		Logger logger = Logger.getLogger(MySqlToAccess.class.getName());
		FileHandler fh;
		// This block configure the logger with handler and formatter
		fh = new FileHandler("./Transfer.log");
		logger.addHandler(fh);

	}

}
