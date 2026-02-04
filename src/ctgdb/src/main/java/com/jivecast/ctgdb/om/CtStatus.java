package com.jivecast.ctgdb.om;

import lombok.Data;

@Data
public class CtStatus {
	int refId;
	String ctStatus;
	
	public CtStatus()
	{
		this.refId = -1;
		this.ctStatus = "";
	}
	
	public CtStatus(int refId, String ctStatus)
	{
		this.refId = refId;
		this.ctStatus = ctStatus;
	}	
}
