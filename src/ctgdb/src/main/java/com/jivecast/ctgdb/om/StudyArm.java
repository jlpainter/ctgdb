package com.jivecast.ctgdb.om;

import java.util.HashMap;

import lombok.Data;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Study arms are linked to a trial and tell us who the participants are
 * 
 * @author Jeffery Painter <jeff@jivecast.com>
 * @created: 2021-Oct-28
 *
 */

@Data
public class StudyArm {

	int refId;
	int trialId;
	int groupId;
	String groupName;
	String title;
	String description;
	
	// demographic data for the study arm
	double meanAge;
	double stdDev;
	int femalePatients;
	int malePatients;
	
	// ethnicity
	int ethnicityAsian;
	int ethnicityBlack;
	int ethnicityCaucasian;
	int ethnicityHispanic;
	int ethnicityIndian;
	int ethnicityMiddleEast;
	int ethnicityMultiple;
	int ethnicityNativeAmerican;
	int ethnicityNativeHawaiian;
	int ethnicityOther;
	
	// placebo group
	boolean isPlacebo;
	
	// healthy patient group?
	boolean isHealthy;
	
	// count patients
	int patientsStarted;
	int patientsCompleted;
	int patientsNotCompleted;
	int patientsLost;
	int patientsWithdrawn;
	int patientsDied;
	
	// adverse events
	HashMap<AdverseEvent, Integer> adverseEvents;
	
	
	/**
	 * Initialize the object
	 */
	public StudyArm() {
		
		this.refId = 0;
		this.trialId = 0;
		this.groupId = 0;
		this.groupName = "";
		this.title = "";
		this.description = "";
		this.isPlacebo = false;
		this.isHealthy = false;
		this.meanAge = -1;
		this.stdDev = -1;
		this.femalePatients = 0;
		this.malePatients = 0;
		this.patientsStarted = 0;
		this.patientsCompleted = 0;
		this.patientsNotCompleted = 0;
		this.patientsLost = 0;
		this.patientsWithdrawn = 0;
		this.patientsDied = 0;
		this.ethnicityAsian = 0;
		this.ethnicityBlack = 0;
		this.ethnicityCaucasian = 0;
		this.ethnicityHispanic = 0;
		this.ethnicityIndian = 0;
		this.ethnicityMiddleEast = 0;
		this.ethnicityMultiple = 0;
		this.ethnicityNativeAmerican = 0;
		this.ethnicityNativeHawaiian = 0;
		this.ethnicityOther = 0;
		
		// link adverse events here
		this.adverseEvents = new HashMap<>();
	}
	
	/**
	 * Convert ARM to CSV format
	 * @param delimiter
	 * @return
	 */
	public String toCSV(String delimiter) {
		
		StringBuilder csv = new StringBuilder();
		csv.append(this.getRefId());
		csv.append(delimiter);
		
		csv.append(this.getTrialId());
		csv.append(delimiter);

		csv.append(this.getGroupId());
		csv.append(delimiter);

		csv.append(this.getTitle());
		csv.append(delimiter);

		csv.append(this.getDescription());
		csv.append(delimiter);

		csv.append(this.getFemalePatients());
		csv.append(delimiter);

		csv.append(this.getMalePatients());
		csv.append(delimiter);

		csv.append(this.getEthnicityAsian());
		csv.append(delimiter);

		csv.append(this.getEthnicityBlack());
		csv.append(delimiter);
		
		csv.append(this.getEthnicityCaucasian());
		csv.append(delimiter);

		csv.append(this.getEthnicityHispanic());
		csv.append(delimiter);

		csv.append(this.getEthnicityIndian());
		csv.append(delimiter);

		csv.append(this.getEthnicityMiddleEast());
		csv.append(delimiter);

		csv.append(this.getEthnicityMultiple());
		csv.append(delimiter);

		csv.append(this.getEthnicityNativeAmerican());
		csv.append(delimiter);

		csv.append(this.getEthnicityNativeHawaiian());
		csv.append(delimiter);
		
		csv.append(this.getEthnicityOther());
		csv.append(delimiter);

		csv.append(this.getMeanAge());
		csv.append(delimiter);

		csv.append(this.getStdDev());
		csv.append(delimiter);

		if ( this.isPlacebo() == true )
			csv.append(1);
		else
			csv.append(0);
		csv.append(delimiter);
		
		if ( this.isHealthy == true )
			csv.append(1);
		else
			csv.append(0);
		csv.append(delimiter);

		csv.append(this.getPatientsStarted());
		csv.append(delimiter);

		csv.append(this.getPatientsCompleted());
		csv.append(delimiter);

		csv.append(this.getPatientsLost());
		csv.append(delimiter);

		csv.append(this.getPatientsWithdrawn());
		csv.append(delimiter);

		csv.append(this.getPatientsDied());
		return csv.toString();
	}
	
}
