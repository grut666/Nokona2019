package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nokona.data.NokonaDatabaseEmp;
import com.nokona.data.NokonaDatabaseTicket;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.EmployeeFormatter;
import com.nokona.model.Employee;
import com.nokona.model.Segment;
import com.nokona.model.Ticket;
import com.nokona.validator.EmployeeValidator;


public class NokonaDAOTicket extends NokonaDAO  implements NokonaDatabaseTicket {
	public NokonaDAOTicket() throws DatabaseException {
		super();
		
	}

	PreparedStatement psGetEmployeeByKey;
	PreparedStatement psGetEmployeeByEmpId;
	PreparedStatement psGetEmployees;
	PreparedStatement psAddEmployee;
	PreparedStatement psUpdateEmployee;

	PreparedStatement psGetOperationByKey;
	PreparedStatement psGetOperationByOpCode;
	PreparedStatement psGetOperations;

	PreparedStatement psDelEmployeeByKey;
	PreparedStatement psDelEmployeeByEmpId;

	
//	@Override
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

//	@Override
	public Employee getEmployee(long key) throws DatabaseException {
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
	public Employee getEmployee(String empID) throws DatabaseException {
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
						"Update Employee Set LastName = ?, FirstName = ?, BarCodeID = ?, LaborCode = ?, EmpID = ?, Active = ? " +
							"WHERE Employee.KEY = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		Employee formattedEmployee = EmployeeFormatter.format(employeeIn);
		String validateMessage = EmployeeValidator.validateUpdate(formattedEmployee, conn);
		if (! "".equals(validateMessage)) {
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
			return getEmployee(formattedEmployee.getKey());
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}
	@Override
	public Employee addEmployee(Employee employeeIn) throws DatabaseException {

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
		Employee formattedEmployee = EmployeeFormatter.format(employeeIn);
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
					return getEmployee(generatedKeys.getLong(1));
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
	public void deleteEmployee(long key) throws DatabaseException {
		if (psDelEmployeeByKey == null) {
			try {
				psDelEmployeeByKey = conn.prepareStatement("Delete From Employee where Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psDelEmployeeByKey.setLong(1, key);
			int rowCount = psDelEmployeeByKey.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Employee key " + key + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}

	}

	@Override
	public void deleteEmployee(String empID) throws DatabaseException {
		if (empID == null) {
			throw new NullInputDataException("empID cannot be null");
		}
		if (psDelEmployeeByEmpId == null) {
			try {
				psDelEmployeeByEmpId = conn.prepareStatement("Delete From Employee where EmpID = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psDelEmployeeByEmpId.setString(1, empID);
			int rowCount = psDelEmployeeByEmpId.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Emp ID " + empID + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public List<Segment> getTicketSegment() throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Segment getTicketSegment(long key) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Segment getTicketSegment(String key) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ticket addTicket(Ticket ticket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ticket getTicket(String ticketIn) {
		// TODO Auto-generated method stub
		return null;
	}



}
