package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nokona.data.NokonaDatabaseEmp;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.InvalidInsertException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.EmployeeFormatter;
import com.nokona.model.Employee;
import com.nokona.validator.EmployeeValidator;


public class NokonaDAOEmployee extends NokonaDAO implements NokonaDatabaseEmp {
	public NokonaDAOEmployee() throws DatabaseException {
		super();

	}
	public NokonaDAOEmployee(String userName, String password) throws DatabaseException {
		super(userName, password); 

	}

	PreparedStatement psGetEmployeeByKey;
	PreparedStatement psGetEmployeeByEmpId;
	PreparedStatement psGetEmployees;
	PreparedStatement psAddEmployee;
	PreparedStatement psAddEmployeeDupeCheck;
	PreparedStatement psAddEmployeeLaborCodeCheck;
	PreparedStatement psUpdateEmployee;

	PreparedStatement psGetOperationByKey;
	PreparedStatement psGetOperationByOpCode;
	PreparedStatement psGetOperations;

	PreparedStatement psMoveDeletedEmployeeByKey;
	PreparedStatement psMoveDeletedEmployeeByEmpId;
	PreparedStatement psDelEmployeeByKey;
	PreparedStatement psDelEmployeeByEmpId;

	@Override
	public List<Employee> getEmployees() throws DatabaseException {
		List<Employee> employees = new ArrayList<Employee>();
		if (psGetEmployees == null) { 
			try {
				psGetEmployees = getConn().prepareStatement("Select * from Employee order by EmpID");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			ResultSet rs = psGetEmployees.executeQuery();
			while (rs.next()) {
				employees.add(convertEmployeeFromResultSet(rs));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return employees;
	}

	@Override
	public Employee getEmployeeByKey(long key) throws DatabaseException {
		Employee emp = null;
		if (psGetEmployeeByKey == null) {
			try {
				psGetEmployeeByKey = conn.prepareStatement("Select * from Employee where Employee.key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			psGetEmployeeByKey.setLong(1, key);
			ResultSet rs = psGetEmployeeByKey.executeQuery();
			if (rs.next()) {
				emp = convertEmployeeFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Employee key " + key + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
		return EmployeeFormatter.format(emp);

	}

	@Override
	public Employee getEmployee(String empID) throws DatabaseException, NullInputDataException {
		if (empID == null) {
			throw new NullInputDataException("empID cannot be null");
		}
		Employee emp = null;
		if (psGetEmployeeByEmpId == null) {
			try {
				psGetEmployeeByEmpId = conn.prepareStatement("Select * from Employee where Employee.EmpID = ?");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage(), e); 
			}
		}
		try {
			psGetEmployeeByEmpId.setString(1, empID);
			ResultSet rs = psGetEmployeeByEmpId.executeQuery();
			if (rs.next()) {
				emp = convertEmployeeFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Employee EmpID " + empID + " is not in DB");
			}
		} catch (SQLException e) {

			throw new DatabaseException(e.getMessage(), e);
		}
		return emp;
	}

	private Employee convertEmployeeFromResultSet(ResultSet rs) throws SQLException {
		int key = rs.getInt("Key");
		String lName = rs.getString("LastName");
		String fName = rs.getString("FirstName");
		int barCodeId = rs.getInt("BarCodeID");
		int laborCode = rs.getInt("LaborCode");
		String empId = rs.getString("EmpID");
		boolean active = (rs.getInt("Active") > 0) ? true : false;
		return EmployeeFormatter.format(new Employee(key, lName, fName, barCodeId, laborCode, empId, active));
	}

	@Override
	public Employee updateEmployee(Employee employeeIn) throws DatabaseException {

		if (psUpdateEmployee == null) {
			try {
				psUpdateEmployee = conn.prepareStatement(
						"Update Employee Set LastName = ?, FirstName = ?, BarCodeID = ?, LaborCode = ?, EmpID = ?, Active = ? "
								+ "WHERE Employee.KEY = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		Employee formattedEmployee = EmployeeFormatter.format(employeeIn);
		String validateMessage = EmployeeValidator.validateUpdate(formattedEmployee, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
			psUpdateEmployee.setString(1, formattedEmployee.getLastName());
			psUpdateEmployee.setString(2, formattedEmployee.getFirstName());
			psUpdateEmployee.setInt(3, formattedEmployee.getBarCodeID());
			psUpdateEmployee.setInt(4, formattedEmployee.getLaborCode());
			psUpdateEmployee.setString(5, formattedEmployee.getEmpId());
			psUpdateEmployee.setInt(6, formattedEmployee.isActive() ? 1 : 0);
			psUpdateEmployee.setLong(7, formattedEmployee.getKey());
			int rowCount = psUpdateEmployee.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			return getEmployeeByKey(formattedEmployee.getKey());

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public Employee addEmployee(Employee employeeIn) throws DatabaseException {
		// Dupe Data Check
		if (employeeIn == null) {
			throw new NullInputDataException("Employee cannot be null");
		}
		Employee formattedEmployee = EmployeeFormatter.format(employeeIn);
		if (psAddEmployeeDupeCheck == null) {
			try {
				psAddEmployeeDupeCheck = conn
						.prepareStatement("Select * from Employee where BarCodeID = ? or EmpID = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
		psAddEmployeeDupeCheck.setInt(1, formattedEmployee.getBarCodeID());
		psAddEmployeeDupeCheck.setString(2, formattedEmployee.getEmpId());
		ResultSet rs = psAddEmployeeDupeCheck.executeQuery();
		if (rs.next()) {
			throw new DuplicateDataException("BarCodeID or EmpID is already in use");
		}
		}
		catch(SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
		// LaborCode Check
		if (psAddEmployeeLaborCodeCheck == null) {
			try {
				psAddEmployeeLaborCodeCheck = conn.prepareStatement("Select * from LaborCode where LaborCode = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage()); 
			}
		}
		try {
			psAddEmployeeLaborCodeCheck.setInt(1, formattedEmployee.getLaborCode());
			ResultSet rs2 = psAddEmployeeLaborCodeCheck.executeQuery();
			if (!rs2.next()) {
				throw new DataNotFoundException("Invalid Labor Code " + employeeIn.getLaborCode());
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);

		}
		if (psAddEmployee == null) {
			try {
				psAddEmployee = conn.prepareStatement(
						"Insert into Employee (LastName, FirstName, BarCodeID, LaborCode, EmpID, Active) values (?,?,?,?,?,?)",
						PreparedStatement.RETURN_GENERATED_KEYS);

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}

		String validateMessage = EmployeeValidator.validateAdd(formattedEmployee, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
			psAddEmployee.setString(1, formattedEmployee.getLastName());
			psAddEmployee.setString(2, formattedEmployee.getFirstName());
			psAddEmployee.setInt(3, formattedEmployee.getBarCodeID());
			psAddEmployee.setInt(4, formattedEmployee.getLaborCode());
			psAddEmployee.setString(5, formattedEmployee.getEmpId());
			psAddEmployee.setInt(6, formattedEmployee.isActive() ? 1 : 0);
			int rowCount = psAddEmployee.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			Employee newEmp = new Employee();
			try (ResultSet generatedKeys = psAddEmployee.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newEmp.setKey(generatedKeys.getLong(1));
					return getEmployeeByKey(generatedKeys.getLong(1));
				} else {
					throw new SQLException("Creating user failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteEmployeeByKey(long key) throws DatabaseException {

		
		if (psDelEmployeeByKey == null) {
			try {
				psDelEmployeeByKey = conn.prepareStatement("Delete From Employee where Employee.Key = ?");
				psMoveDeletedEmployeeByKey = conn.prepareStatement("INSERT INTO Deleted_Employee (Deleted_Employee.key, LastName, FirstName, BarCodeID, LaborCode, EmpID, Active) " + 
						"  SELECT Employee.key, LastName, FirstName, BarCodeID, LaborCode, EmpID, Active FROM Employee WHERE Employee.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedEmployeeByKey.setLong(1, key);
			int rowCount = psMoveDeletedEmployeeByKey.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException("Key "+ key + " could not be inserted into delete table");
			}
			psDelEmployeeByKey.setLong(1, key);
			rowCount = psDelEmployeeByKey.executeUpdate();

			if (rowCount == 0) {
				conn.rollback();
				throw new DataNotFoundException("Error.  Delete Employee key " + key + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}

	}

	@Override
	public void deleteEmployee(String empId) throws DatabaseException {

		
		if (psDelEmployeeByEmpId == null) {
			try {
				psDelEmployeeByEmpId = conn.prepareStatement("Delete From Employee where empID = ?");
				psMoveDeletedEmployeeByEmpId = conn.prepareStatement("INSERT INTO Deleted_Employee (Deleted_Employee.key, LastName, FirstName, BarCodeID, LaborCode, EmpID, Active) " + 
						"  SELECT Employee.key, LastName, FirstName, BarCodeID, LaborCode, EmpID, Active FROM Employee WHERE empid = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedEmployeeByEmpId.setString(1, empId);
			int rowCount = psMoveDeletedEmployeeByEmpId.executeUpdate();
			if (rowCount == 0) {
				throw new InvalidInsertException("Empid "+ empId + " could not be inserted into delete table");
			}
			psDelEmployeeByEmpId.setString(1,empId);
			rowCount = psDelEmployeeByEmpId.executeUpdate();

			if (rowCount == 0) {
				conn.rollback();
				throw new DataNotFoundException("Error.  Delete Employee empId " + empId + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}
}
