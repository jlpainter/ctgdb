package com.jivecast.ctgdb.om;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import com.jivecast.ctgdb.app.Util;

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
 * Data model for in memory representation of our clinical trial will provide
 * conversion to SQL methods
 * 
 * @author Jeffery Painter <jeff@jivecast.com>
 * @created: 2021-Sep-17
 *
 */

@Data
public class ClinicalTrial {

	int refId;
	String studyBriefTitle;
	String studyOfficialTitle;

	// who is running the trial?
	String leadSponsor;
	String collaborator;

	CtPhase studyPhase;
	CtType studyType;
	CtStatus studyStatus;

	String nctId;
	String studyId;
	String url;

	LocalDate startDate;
	LocalDate endDate;
	long trialLengthInDays;
	int totalPatients;
	int placeboPatients;
	int totalOtherEvents;
	int totalSeriousEvents;
	int patsAffectedSeriousAEs;
	int patsAffectedOtherAEs;

	// recruitment data
	int minimumAge;
	int maximumAge;
	boolean healthyPatientsAccepted;
	boolean isComplete;

	// linked tables
	List<Country> countries;
	List<Condition> conditions;
	List<Intervention> interventions;
	List<StudyArm> arms;
	List<AdverseEvent> adverseEvents;

	public ClinicalTrial(int refId) {
		this.refId = refId;
		this.studyBriefTitle = "";
		this.studyOfficialTitle = "";
		this.nctId = "";
		this.studyId = "";

		this.studyPhase = null;
		this.studyType = null;
		this.studyStatus = null;

		this.startDate = null;
		this.endDate = null;
		this.trialLengthInDays = 0;
		this.totalPatients = 0;
		this.placeboPatients = 0;
		this.isComplete = false;

		// event counts
		this.totalOtherEvents = 0;
		this.totalSeriousEvents = 0;
		this.patsAffectedSeriousAEs = 0;
		this.patsAffectedOtherAEs = 0;

		// linked tables
		this.countries = new ArrayList<>();
		this.conditions = new ArrayList<>();
		this.interventions = new ArrayList<>();
		this.adverseEvents = new ArrayList<>();
		this.arms = new ArrayList<>();

	}

	/**
	 * Does the study include female patients?
	 * 
	 * @return
	 */
	public boolean hasFemale() {
		for (StudyArm arm : this.arms)
			if (arm.getFemalePatients() > 0)
				return true;
		return false;
	}

	/**
	 * Does the study include female patients?
	 * 
	 * @return
	 */
	public boolean hasMale() {
		for (StudyArm arm : this.arms)
			if (arm.getMalePatients() > 0)
				return true;
		return false;
	}

	public String toCSV(String delimiter) {

		StringBuilder csv = new StringBuilder();
		csv.append(this.getRefId());
		csv.append(delimiter);

		csv.append(this.getStudyStatus().getRefId());
		csv.append(delimiter);

		csv.append(this.getStudyType().getRefId());
		csv.append(delimiter);

		csv.append(this.getStudyPhase().getRefId());
		csv.append(delimiter);

		csv.append(this.getStudyBriefTitle());
		csv.append(delimiter);

		csv.append(this.getStudyOfficialTitle());
		csv.append(delimiter);

		csv.append(this.getLeadSponsor());
		csv.append(delimiter);

		csv.append(this.getCollaborator());
		csv.append(delimiter);

		csv.append(this.getUrl());
		csv.append(delimiter);

		csv.append(this.getNctId());
		csv.append(delimiter);

		csv.append(this.getStudyId());
		csv.append(delimiter);

		// Parse dates for CSV output
		// LocalDate toString() default format works for us yyyy-MM-dd
		String startDateStr = "";
		String endDateStr = "";
		if ( this.getStartDate() != null )
			startDateStr = this.getStartDate().toString();

		if ( this.getEndDate() != null )
			endDateStr = this.getEndDate().toString();

		csv.append(Util.cleanAndQuoteContent(startDateStr));
		csv.append(delimiter);

		csv.append(Util.cleanAndQuoteContent(endDateStr));
		csv.append(delimiter);

		csv.append(this.getTrialLengthInDays());
		csv.append(delimiter);

		csv.append(this.getTotalPatients());
		csv.append(delimiter);

		csv.append(this.getMinimumAge());
		csv.append(delimiter);

		csv.append(this.getMaximumAge());
		csv.append(delimiter);

		if (this.hasFemale() == true)
			csv.append(1);
		else
			csv.append(0);
		csv.append(delimiter);

		if (this.hasMale() == true)
			csv.append(1);
		else
			csv.append(0);
		csv.append(delimiter);

		if (this.isHealthyPatientsAccepted() == true)
			csv.append(1);
		else
			csv.append(0);
		csv.append(delimiter);
		
		// trial completed?
		if (this.isComplete() == true )
			csv.append(1);
		else
			csv.append(0);

		return csv.toString();

	}

}
