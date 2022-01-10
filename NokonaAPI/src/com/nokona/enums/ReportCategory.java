package com.nokona.enums;

public enum ReportCategory {
		
		EMPLOYEE("EMPLOYEE"), JOB("JOB"), LABOR("LABOR"), OPERATION("OPERATION"), TICKET("TICKET") ;
	    private String 	category;
	    
		ReportCategory(String category) {
			this.category = category;
		}
		public String getCategory() {
			return category ;
		}
}