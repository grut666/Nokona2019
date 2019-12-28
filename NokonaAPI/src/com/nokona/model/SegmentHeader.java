package com.nokona.model;

import java.util.Date;

public class SegmentHeader {

	private String segmentName;
	private String segmentDescription;
	private Date dateCreated;
	private Date dateDeleted;
	private boolean deleted;
	

	
	public SegmentHeader() {
		super();
	}
	public SegmentHeader(String segmentName, String segmentDescription, Date dateCreated, Date dateDeleted, boolean deleted) {
		super();
		this.setSegmentName(segmentName);
		this.setSegmentDescription(segmentDescription);
		this.setDateCreated(dateCreated);
		this.setDateDeleted(dateDeleted);
		this.setDeleted(deleted);

	}

	public String getSegmentName() {
		return segmentName;
	}
	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}
	public String getSegmentDescription() {
		return segmentDescription;
	}
	public void setSegmentDescription(String segmentDescription) {
		this.segmentDescription = segmentDescription;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public Date getDateDeleted() {
		return dateDeleted;
	}
	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}
	@Override
	public String toString() {
		return "SegmentHeader [segmentName=" + segmentName + ", segmentDescription=" + segmentDescription
				+ ", dateCreated=" + dateCreated + ", dateDeleted=" + dateDeleted + ", deleted=" + deleted + "]";
	}
	

	
	


}
