package com.jivecast.ctgdb.om;

import lombok.Data;

@Data
public class AdverseEvent {

	int refId;
	boolean isSerious;
	String term;
	
	// umls meta-data
	int meddraRefId;
	boolean exactMatch;
	
	/**
	 * Initialize everything to empty strings
	 */
	public AdverseEvent() {
		this.refId = -1;
		this.isSerious = false;
		this.term = "";
		this.meddraRefId = -1;
		this.exactMatch = false;
	}	
	
}
