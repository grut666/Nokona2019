package com.nokona.enums;

public enum ReportCategory {
		
		EMPLOYEE("EMPLOYEE"), Employee("Employee"), JOB("JOB"), Job("Job"), LABOR("LABOR"), Labor("Labor"),
		OPERATION("OPERATION"), Operation("Operation"), TICKET("TICKET"), Ticket("Ticket") ;
	    private String 	category;
	    
		ReportCategory(String category) {
			this.category = category.toUpperCase();
		}
		public String getCategory() {
			return category ;
		}
}