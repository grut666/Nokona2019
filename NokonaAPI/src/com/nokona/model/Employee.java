package com.nokona.model;

public class Employee {
	private long key;
	private String lastName;
	private String firstName;
	private int barCodeID;
	private int laborCode;
	private String empId;
	boolean active;
	
	public Employee() {
		this(-1, "", "", 0, 0, "", false);
	}
	public Employee(int key, String lastName, String firstName, int barCodeID, int laborCode, String empId, boolean active) {
		this.setKey(key);
		this.setLastName(lastName);
		this.setFirstName(firstName);
		this.setBarCodeID(barCodeID);
		this.setLaborCode(laborCode);
		this.setEmpId(empId);
		this.setActive(active);
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public int getBarCodeID() {
		return barCodeID;
	}
	public void setBarCodeID(int barCodeID) {
		this.barCodeID = barCodeID;
	}
	public int getLaborCode() {
		return laborCode;
	}
	public void setLaborCode(int laborCode) {
		this.laborCode = laborCode;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	@Override
	public String toString() {
		return "Employee [key=" + key + ", lastName=" + lastName + ", firstName=" + firstName + ", barCodeID="
				+ barCodeID + ", laborCode=" + laborCode + ", empId=" + empId + ", active=" + active + "]";
	}  
	
	
}
