package com.nokona.data;

import java.util.List;

import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Employee;

public interface NokonaDatabaseEmp  {
	List<Employee> getEmployees() throws DatabaseException;
	Employee getEmployee(long key) throws DatabaseException;
	Employee getEmployee(String empID) throws DatabaseException;
	Employee updateEmployee(Employee employee) throws DatabaseException;
	Employee addEmployee(Employee employee) throws DatabaseException;
	void deleteEmployee(long key) throws DatabaseException;
	void deleteEmployee(String empID) throws DatabaseException;	
}
