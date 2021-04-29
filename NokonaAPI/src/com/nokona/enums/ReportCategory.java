package com.nokona.enums;

public enum ReportCategory {
		
		EMPLOYEE("EMPLOYEE"), JOB("JOB"), LABEL("LABEL"), OPERATION("OPERATION"), TICKET("TICKET") ;
	    private String 	category;
	    
		ReportCategory(String category) {
			this.category = category;
		}
		public String getCategory() {
			return category ;
		}
}