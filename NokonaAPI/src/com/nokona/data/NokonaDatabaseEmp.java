package com.nokona.data;

import java.util.List;

import com.nokona.model.Employee;

import com.nokona.exceptions.DatabaseException;

public interface NokonaDatabaseEmp extends NokonaDatabase {

	List<Employee> getEmployees() throws DatabaseException;

	Employee getEmployeeByKey(long key) throws DatabaseException;
	
	Employee getEmployeeByBarCodeId(int barCodeId) throws DatabaseException;

	Employee getEmployee(String empID) throws DatabaseException;

	Employee updateEmployee(Employee employee) throws DatabaseException;

	Employee addEmployee(Employee employee) throws DatabaseException;

	void deleteEmployeeByKey(long key) throws DatabaseException;

	void deleteEmployee(String empID) throws DatabaseException;
}
