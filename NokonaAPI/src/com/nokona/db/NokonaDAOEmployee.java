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

import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.EmployeeFormatter;
import com.nokona.model.Employee;

public class NokonaDAOEmployee extends NokonaDAO implements NokonaDatabaseEmp {

	public NokonaDAOEmployee() throws DatabaseException {
		super();

	}

	public NokonaDAOEmployee(String userName, String password) throws DatabaseException {
		super(userName, password);

	}

	@Override
	public List<Employee> getEmployees() throws DatabaseException {
		List<Employee> employees = new ArrayList<Employee>();

		try (PreparedStatement psGetEmployees = getConn().prepareStatement("Select * from Employee order by EmpID");) {
			try (ResultSet rs = psGetEmployees.executeQuery();) {

				while (rs.next()) {
					employees.add(convertEmployeeFromResultSet(rs));
				}
				return employees;
			}

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}

	}

	@Override
	public Employee getEmployeeByKey(long key) throws DatabaseException {
		Employee emp = null;
		try {
			PreparedStatement psGetEmployeeByKey = conn
					.prepareStatement("Select * from Employee where Employee.key = ?");
			psGetEmployeeByKey.setLong(1, key);
			try (ResultSet rs = psGetEmployeeByKey.executeQuery();) {

				if (rs.next()) {
					emp = convertEmployeeFromResultSet(rs);
				} else {
					throw new DataNotFoundException("Employee key " + key + " is not in DB");
				}
				return EmployeeFormatter.format(emp);
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}

	}
	
	@Override
	public Employee getEmployeeByBarCodeId(int barCodeId) throws DatabaseException {
		Employee emp = null;
		try {
			PreparedStatement psGetEmployeeByKey = conn
					.prepareStatement("Select * from Employee where barCodeId = ?");
			psGetEmployeeByKey.setInt(1, barCodeId);
			try (ResultSet rs = psGetEmployeeByKey.executeQuery();) {

				if (rs.next()) {
					emp = convertEmployeeFromResultSet(rs);
				} else {
					throw new DataNotFoundException("Employee bar code ID " + barCodeId + " is not in DB");
				}
				return EmployeeFormatter.format(emp);
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}

	}
	@Override
	public Employee getEmployee(String empID) throws DatabaseException, NullInputDataException {
		if (empID == null) {
			throw new NullInputDataException("empID cannot be null");
		}
		Employee emp = null;
		try (PreparedStatement psGetEmployeeByEmpId = conn
				.prepareStatement("Select * from Employee where Employee.EmpID = ?");) {
			psGetEmployeeByEmpId.setString(1, empID);
			try (ResultSet rs = psGetEmployeeByEmpId.executeQuery();) {
				if (rs.next()) {
					emp = convertEmployeeFromResultSet(rs);
				} else {
					throw new DataNotFoundException("Employee EmpID " + empID + " is not in DB");
				}
				return emp;
			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage(), e);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
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

		try (PreparedStatement psUpdateEmployee = conn.prepareStatement(
				"Update Employee Set LastName = ?, FirstName = ?, BarCodeID = ?, LaborCode = ?, EmpID = ?, Active = ? "
						+ "WHERE Employee.KEY = ?");) {
			Employee formattedEmployee = EmployeeFormatter.format(employeeIn);
//			String validateMessage = EmployeeValidator.validateUpdate(formattedEmployee, conn);
//			if (!"".equals(validateMessage)) {
//				throw new DatabaseException(validateMessage);
//			}
			psUpdateEmployee.setString(1, formattedEmployee.getLastName());
			psUpdateEmployee.setString(2, formattedEmployee.getFirstName());
			psUpdateEmployee.setInt(3, formattedEmployee.getBarCodeID());
			psUpdateEmployee.setInt(4, formattedEmployee.getLaborCode());
			psUpdateEmployee.setString(5, formattedEmployee.getEmpId());
			psUpdateEmployee.setInt(6, formattedEmployee.isActive() ? 1 : 0);
			psUpdateEmployee.setLong(7, formattedEmployee.getKey());
			int rowCount = psUpdateEmployee.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Updated " + rowCount + " rows");
			}
			return getEmployeeByKey(formattedEmployee.getKey());
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public Employee addEmployee(Employee employeeIn) throws DatabaseException {
		// Dupe Data Check
		if (employeeIn == null) {
			throw new NullInputDataException("Employee cannot be null");
		}
		Employee formattedEmployee = EmployeeFormatter.format(employeeIn);
//		try (PreparedStatement psAddEmployeeDupeCheck = conn
//				.prepareStatement("Select * from Employee where BarCodeID = ? or EmpID = ?");) {
//			psAddEmployeeDupeCheck.setInt(1, formattedEmployee.getBarCodeID());
//			psAddEmployeeDupeCheck.setString(2, formattedEmployee.getEmpId());
//			try (ResultSet rs = psAddEmployeeDupeCheck.executeQuery()) {
//				if (rs.next()) {
//					throw new DuplicateDataException("BarCodeID or EmpID is already in use");
//				}
//
//				try (PreparedStatement psAddEmployeeLaborCodeCheck = conn
//						.prepareStatement("Select * from LaborCode where LaborCode = ?")) {
//					psAddEmployeeLaborCodeCheck.setInt(1, formattedEmployee.getLaborCode());
//					try (ResultSet rs2 = psAddEmployeeLaborCodeCheck.executeQuery()) {
//						if (!rs2.next()) {
//							throw new DataNotFoundException("Invalid Labor Code " + employeeIn.getLaborCode());
//						}
//					}
//				}
//			}
//		} catch (SQLException e) {
//			System.err.println(e.getMessage());
//			throw new DatabaseException(e.getMessage());
//		}

		// -----------------------
		try (PreparedStatement psAddEmployee = conn.prepareStatement(
				"Insert into Employee (LastName, FirstName, BarCodeID, LaborCode, EmpID, Active) values (?,?,?,?,?,?)",
				PreparedStatement.RETURN_GENERATED_KEYS)) {
//			String validateMessage = EmployeeValidator.validateAdd(formattedEmployee, conn);
//			if (!"".equals(validateMessage)) {
//				throw new DatabaseException(validateMessage);
//			}
			psAddEmployee.setString(1, formattedEmployee.getLastName());
			psAddEmployee.setString(2, formattedEmployee.getFirstName());
			psAddEmployee.setInt(3, formattedEmployee.getBarCodeID());
			psAddEmployee.setInt(4, formattedEmployee.getLaborCode());
			psAddEmployee.setString(5, formattedEmployee.getEmpId());
			psAddEmployee.setInt(6, formattedEmployee.isActive() ? 1 : 0);
			psAddEmployee.executeUpdate();

//			if (rowCount != 1) {
//				throw new DatabaseException("Error. Inserted " + rowCount + " rows");
//			}
			Employee newEmp = new Employee();
			try (ResultSet generatedKeys = psAddEmployee.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newEmp.setKey(generatedKeys.getLong(1));
					// loggit("ADD", newEmp);
					return getEmployeeByKey(generatedKeys.getLong(1));
				} else {
					throw new SQLException("Creating user failed, no ID obtained.");
				}
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			if (e.getMessage().contains("Duplicate")) {
					throw new DuplicateDataException(e.getMessage());
			} else if (e.getMessage().contains("foreign key")) {
				throw new DataNotFoundException(e.getMessage());
			}
			throw new DatabaseException(e.getMessage());

		}
	}

	@Override
	public void deleteEmployeeByKey(long key) throws DatabaseException {
		try (PreparedStatement psDelEmployeeByKey = conn
				.prepareStatement("Delete From Employee where Employee.Key = ?");) {
			psDelEmployeeByKey.setLong(1, key);
			int rowCount = psDelEmployeeByKey.executeUpdate();
			if (rowCount == 0) {
				// conn.rollback();
				throw new DataNotFoundException("Error.  Delete Employee key " + key + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public void deleteEmployee(String empId) throws DatabaseException {
		try (PreparedStatement psDelEmployeeByEmpId = conn.prepareStatement("Delete From Employee where empID = ?")) {
			psDelEmployeeByEmpId.setString(1, empId);
			int rowCount = psDelEmployeeByEmpId.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Employee empId " + empId + " failed");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}

}
