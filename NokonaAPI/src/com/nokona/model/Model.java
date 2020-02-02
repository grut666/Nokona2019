package com.nokona.model;

import java.util.List;

public class Model {
	private ModelHeader header;
	private List<ModelDetail> details;
	public Model(ModelHeader header, List<ModelDetail> details ) {
		this.header = header;
		this.details=details;
	}
	public ModelHeader getHeader() {
		return header;
	}
	public void setHeader(ModelHeader header) {
		this.header = header;
	}
	public List<ModelDetail> getDetails() {
		return details;
	}
	public void setDetails(List<ModelDetail> details) {
		this.details = details;
	}
	@Override
	public String toString() {
		return "Model [header=" + header + ", details=" + details + "]";
	}


	
}

