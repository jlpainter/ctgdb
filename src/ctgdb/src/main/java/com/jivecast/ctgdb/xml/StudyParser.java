package com.jivecast.ctgdb.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;

import com.jivecast.ctgdb.app.Util;
import com.jivecast.ctgdb.om.AdverseEvent;
import com.jivecast.ctgdb.om.ClinicalTrial;
import com.jivecast.ctgdb.om.Condition;
import com.jivecast.ctgdb.om.Country;
import com.jivecast.ctgdb.om.CtPhase;
import com.jivecast.ctgdb.om.CtStatus;
import com.jivecast.ctgdb.om.CtType;
import com.jivecast.ctgdb.om.Intervention;
import com.jivecast.ctgdb.om.StudyArm;
import com.jivecast.ctgdb.generated.clinicaltrials.ClinicalResultsStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.ClinicalStudy;
import com.jivecast.ctgdb.generated.clinicaltrials.EventCategoryStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.EventCategoryStruct.EventList;
import com.jivecast.ctgdb.generated.clinicaltrials.EventCountsStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.EventStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.EventsStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.EventsStruct.CategoryList;
import com.jivecast.ctgdb.generated.clinicaltrials.GroupStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.InterventionStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.MeasureCategoryStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.MeasureClassStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.MeasureStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.MeasurementStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.MilestoneStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.ParticipantsStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.PeriodStruct;
import com.jivecast.ctgdb.generated.clinicaltrials.SponsorStruct;

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
 * Parse a single CT xml file and convert to our object model
 * 
 * @author Jeffery Painter <jeff@jivecast.com>
 * @created: 2021-Sep-17
 * @modified: 2021-Oct-27
 *
 */
public class StudyParser {

	private JAXBContext jaxbContext;
	private Unmarshaller jaxbUnmarshaller;

	// Use our map to assign ethnicity to a
	// more general category for DB storage
	private HashMap<String, String> ethnicities;

	// Date formatter for CT dates
	SimpleDateFormat ctDateFormat = new SimpleDateFormat("MMMM dd, yyyy");

	/**
	 * Initialize the parser context
	 */
	public StudyParser() {
		try {
			jaxbContext = JAXBContext.newInstance(ClinicalStudy.class);
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			loadEthnicityMapping();
		} catch (Exception e) {
			System.err.println("Error initializing the XML parser: " + e.toString());
		}
	}

	private void loadEthnicityMapping() {
		this.ethnicities = new HashMap<String, String>();

		try {
			// File generated using Python script and debug output in CreateDatabase.java
			File file = new File("src/main/resources/clinicaltrials.gov/all_reported_ethnicities_mapped.txt");
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] data = line.split("\t");
					String eth = data[0];
					String ethCat = data[1];
					if (eth.length() > 0)
						this.ethnicities.put(eth, ethCat);
				}
			}

		} catch (Exception e) {
			System.err.println("Error loading ethnicity map! " + e.toString());
		}

		return;
	}

	public HashMap<String, String> getEthnicities() {
		return this.ethnicities;
	}

	public LocalDate parseCTDate(String strDate) {
		try {
			// date format: December 20, 2019
			Calendar cal = Calendar.getInstance();
			cal.setTime(ctDateFormat.parse(strDate));

			// Convert to LocalDate obj
			TimeZone tz = cal.getTimeZone();
			ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
			return LocalDateTime.ofInstant(cal.toInstant(), zid).toLocalDate();
		} catch (Exception dateFormatError) {
			// pass
		}
		return null;
	}

	/**
	 * Extract the data we want to visualize and put into CSV format
	 * 
	 * Row format (just a draft to see if we can get this working)
	 * 
	 * STUDY_ID, STUDY_TITLE, START_DATE, END_DATE, NUM_PARTICIPANTS,
	 * UNIQUE_PATIENTS_AFFECTED_OTHER, TOTAL_OTHER_EVENTS,
	 * UNIQUE_PATIENTS_AFFECTED_SERIOUS, TOTAL_SERIOUS_EVENTS
	 * 
	 * @return
	 */
	public ClinicalTrial convertXMLToObjModel(int trial_ref_id, int study_arm_ref_id, String datafile,
			HashMap<String, Intervention> uniqueInterventions, HashMap<String, Country> uniqueCountries,
			HashMap<String, Condition> uniqueConditions, HashMap<String, AdverseEvent> adverseEvents, PrintWriter genderOutputWriter, HashMap<String, String> genderMaps) {
		try {

			File xmlFile = new File(datafile);

			// Use the jaxb helper to parse the xml file
			ClinicalStudy study = (ClinicalStudy) jaxbUnmarshaller.unmarshal(xmlFile);

			// valid study to include?
			boolean validStudy = true;

			// Reasons not to include a study
			// 1. withheld (not run)
			if (study.getOverallStatus().contentEquals("Withheld"))
				validStudy = false;

			// 2. No eligibility defined
			if (study.getEligibility() == null)
				validStudy = false;

			// 3. Must have conditions to study
			List<Condition> studyConditions = getStudyConditions(uniqueConditions, study);
			if (studyConditions.size() == 0)
				validStudy = false;

			// 4. Only process valid studies
			if (validStudy == false) {
				return null;
			} else {

				// what do we want to extract from the study?
				String studyBriefTitle = study.getBriefTitle();
				String studyOfficialTitle = study.getOfficialTitle();

				// study sponsor
				String studySponsor = study.getSponsors().getLeadSponsor().getAgency().toString();
				String studyCollaborators = getCollaborators(study);

				// Does this study include healthy patients?
				boolean hasHealthy = hasHealthPatients(study);

				// Create our clinical trial OM which to be returned
				ClinicalTrial trial = new ClinicalTrial(trial_ref_id);
				trial.setStudyBriefTitle(Util.cleanAndQuoteContent(studyBriefTitle));
				trial.setStudyOfficialTitle(Util.cleanAndQuoteContent(studyOfficialTitle));
				trial.setHealthyPatientsAccepted(hasHealthy);
				trial.setLeadSponsor(Util.cleanAndQuoteContent(studySponsor));
				trial.setCollaborator(Util.cleanAndQuoteContent(studyCollaborators));

				HashMap<Integer, StudyArm> studyArms = new HashMap<>();

				// Age data for eligibility
				int minimumAge = getAge(study.getEligibility().getMinimumAge());
				int miximumAge = getAge(study.getEligibility().getMaximumAge());
				trial.setMinimumAge(minimumAge);
				trial.setMaximumAge(miximumAge);

				// Study identifiers
				String url = study.getRequiredHeader().getUrl().toString();
				String nctId = study.getIdInfo().getNctId();
				String studyId = study.getIdInfo().getOrgStudyId();

				// Get study dates
				LocalDate startDate = null;
				if (study.getStartDate() != null)
					startDate = parseCTDate(study.getStartDate().getValue().toString());

				LocalDate endDate = null;
				if (study.getCompletionDate() != null)
					endDate = parseCTDate(study.getCompletionDate().getValue().toString());

				long trialLengthDays = 0;
				if (endDate != null && startDate != null) {
					if (startDate.isBefore(endDate)) {
						trialLengthDays = startDate.until(endDate, java.time.temporal.ChronoUnit.DAYS);
					}
				}

				// How many patients were enrolled?
				int totalPatients = 0;
				if (study.getEnrollment() != null)
					totalPatients = study.getEnrollment().getValue().intValue();

				// System.out.println("Enrolled patients: " + totalPatients);

				// Phase of the study (e.g. 1, 2, 3, 4)
				String studyPhase = "NA";
				if (study.getPhase() != null) {
					studyPhase = study.getPhase().value();

					// quick fix
					if (studyPhase.toLowerCase().contentEquals("not applicable")
							|| studyPhase.toLowerCase().contentEquals("n/a"))
						studyPhase = "NA";
				}

				// Type of study (intervention - clinical trial, observational)
				String studyType = "Unknown";
				if (study.getStudyType() != null)
					studyType = study.getStudyType().value();

				String studyStatus = "Unknown";
				if (study.getOverallStatus() != null)
					studyStatus = study.getOverallStatus().toString();

				// Where is the study taking place?
				List<Country> studyCountries = getStudyCountries(uniqueCountries, study);

				// interventions are the drug under study
				List<Intervention> interventions = getStudyInterventions(uniqueInterventions, study);

				// If the study has results, we can extract arms and AE data
				ClinicalResultsStruct results = null;

				// unique number of patients who experienced adverse events
				int patsAffectedOtherAEs = 0;
				int totalOtherEvents = 0;
				int patsAffectedSeriousAEs = 0;
				int totalSeriousEvents = 0;

				if (study.getClinicalResults() != null) {
					results = study.getClinicalResults();

					// First, find the patient groups
					HashMap<Integer, String> patientGroups = new HashMap<>();

					// track patient counts
					HashMap<Integer, Integer> patientStartedGroupCounts = new HashMap<>();
					HashMap<Integer, Integer> patientCompletedGroupCounts = new HashMap<>();
					HashMap<Integer, Integer> patientNotcompletedGroupCounts = new HashMap<>();

					List<GroupStruct> myGroupList = results.getParticipantFlow().getGroupList().getGroup();
					for (GroupStruct group : myGroupList) {

						// group IDs have prefixes attached for different measure points.. need just the
						// number
						int groupId = Integer.parseInt(group.getGroupId().substring(1));
						String groupTitle = group.getTitle();
						patientGroups.put(groupId, groupTitle);

						// track
						patientStartedGroupCounts.put(groupId, 0);
						patientCompletedGroupCounts.put(groupId, 0);
						patientNotcompletedGroupCounts.put(groupId, 0);

						// create a new study arm
						StudyArm arm = new StudyArm();

						// set primary and foreign keys
						arm.setRefId(study_arm_ref_id);
						arm.setTrialId(trial_ref_id);

						// set group identifier (study arm group number)
						arm.setGroupId(groupId);

						arm.setTitle(Util.cleanAndQuoteContent(group.getTitle()));
						arm.setDescription(Util.cleanAndQuoteContent(group.getDescription()));

						// Placebo group?
						if (arm.getTitle().toLowerCase().contains("placebo"))
							arm.setPlacebo(true);

						if (arm.getDescription().toLowerCase().contains("placebo"))
							arm.setPlacebo(true);

						// Healthy group?
						if (arm.getTitle().toLowerCase().contains("healthy"))
							arm.setHealthy(true);

						if (arm.getDescription().toLowerCase().contains("healthy"))
							arm.setHealthy(true);

						// add study arm
						studyArms.put(groupId, arm);
						study_arm_ref_id++;
					}

					//
					// once a patient is in a group, they can be labeled
					// as STARTED, COMPLETED, NOT COMPLETED, WITHDRAWN, DECEASED or Lost etc.
					// completed + not completed should equal to started for a completed study
					//
					List<PeriodStruct> myPeriodList = results.getParticipantFlow().getPeriodList().getPeriod();
					for (PeriodStruct period : myPeriodList) {

						// There can be multiple periods in a study.. not sure how to handle this
						// but looking for 'overall study' leaves out a BUNCH of data points
						// In theory, this SHOULD get the periods in order and use the
						// last data points found
						String periodName = period.getTitle().toLowerCase().trim();

						// get total patient counts that were in study
						List<MilestoneStruct> milestones = period.getMilestoneList().getMilestone();
						for (MilestoneStruct milestone : milestones) {
							// Milestone we are at?
							String mTitle = milestone.getTitle().toLowerCase().trim();
							for (ParticipantsStruct g : milestone.getParticipantsList().getParticipants()) {
								int groupId = Integer.parseInt(g.getGroupId().substring(1));
								if (studyArms.containsKey(groupId)) {
									StudyArm arm = studyArms.get(groupId);
									try {

										int count = 0;
										// This could be set to NA in XML files
										if (!g.getCount().contentEquals("NA"))
											count = Integer.parseInt(g.getCount());

										// milestone to check
										if (mTitle.contentEquals("started"))
											arm.setPatientsStarted(count);

										if (mTitle.contentEquals("completed"))
											arm.setPatientsCompleted(count);

										if (mTitle.contentEquals("not completed"))
											arm.setPatientsNotCompleted(count);

										if (mTitle.contentEquals("withdrawn"))
											arm.setPatientsWithdrawn(count);

										if (mTitle.contentEquals("lost"))
											arm.setPatientsLost(count);

										if (mTitle.contentEquals("deceased"))
											arm.setPatientsDied(count);
									} catch (Exception intFormatError) {
										System.err.println("Unable to parse count from milestone data: "
												+ intFormatError.toString());
									}

								}
							}
						}
					}

					// Update the arms with age, gender and ethnicity data
					setArmAgeGenderEthnicty(studyArms, results, genderOutputWriter, genderMaps);

					//
					// Studies can have patients who are either on-treatment or on placebo...
					// There can also be multiple groups (arms) of a study:
					// e.g. group 1 has patients on 10mg of medication
					// group 2 has patients on 20mg of medication
					//
					// I think for our investigation, we just want to know
					// that patient had an observable adverse event occur
					//
					// breaking down into groups would take a bit more effort
					// unless someone has time to dig into this a bit further...
					//

					// count events from all groups in the study
					HashMap<String, Integer> otherEventCounts = new HashMap<>();
					if (results.getReportedEvents() != null) {
						if (results.getReportedEvents().getOtherEvents() != null) {
							EventsStruct otherEvents = results.getReportedEvents().getOtherEvents();
							patsAffectedOtherAEs = getEvents(false, otherEvents, otherEventCounts, studyArms,
									adverseEvents);
						}
					}

					// Count up all the 'other' events (non-serious)
					for (String event : otherEventCounts.keySet()) {
						totalOtherEvents += otherEventCounts.get(event);
					}

					// Serious events
					HashMap<String, Integer> seriousEventCounts = new HashMap<>();
					if (results.getReportedEvents() != null) {
						if (results.getReportedEvents().getSeriousEvents() != null) {
							EventsStruct seriousEvents = results.getReportedEvents().getSeriousEvents();
							patsAffectedSeriousAEs = getEvents(true, seriousEvents, seriousEventCounts, studyArms,
									adverseEvents);
						}
					}

					// Count up all serious events and get total
					for (String event : seriousEventCounts.keySet()) {
						totalSeriousEvents += seriousEventCounts.get(event);
					}
				}

				// Set the CT phase, type and status
				setStudyPhaseTypeStatus(trial, studyPhase, studyType, studyStatus);

				trial.setTotalPatients(totalPatients);
				trial.setStudyId(Util.cleanAndQuoteContent(studyId));
				trial.setNctId(Util.cleanAndQuoteContent(nctId));
				trial.setUrl(Util.cleanAndQuoteContent(url));
				trial.setCountries(studyCountries);

				// Date and trial length
				trial.setStartDate(startDate);
				trial.setEndDate(endDate);
				trial.setTrialLengthInDays(trialLengthDays);

				trial.setPatsAffectedOtherAEs(patsAffectedOtherAEs);
				trial.setTotalOtherEvents(totalOtherEvents);
				trial.setPatsAffectedSeriousAEs(patsAffectedSeriousAEs);
				trial.setTotalSeriousEvents(totalSeriousEvents);

				// linked tables
				trial.setCountries(studyCountries);
				trial.setConditions(studyConditions);
				trial.setInterventions(interventions);
				trial.setArms(studyArms.values().stream().collect(Collectors.toList()));

				return trial;

			} // Valid study

		} catch (

		Exception e) {
			System.err.println("Error: [" + datafile + "] " + trial_ref_id + " " + e.toString());
		}

		return null;

	}

	/**
	 * Update study arms with appropriate meta-data
	 * 
	 * @param studyArms
	 * @param results
	 */
	private void setArmAgeGenderEthnicty(HashMap<Integer, StudyArm> studyArms, ClinicalResultsStruct results, PrintWriter genderOutputWriter, HashMap<String, String> genderMap) {
		// Count and age data from baseline enrollment
		for (MeasureStruct measure : results.getBaseline().getMeasureList().getMeasure()) {
			String title = measure.getTitle().toLowerCase().trim();
			// String unit = measure.getUnits();

			// ethnicity data is messy!
			setEthnicityData(studyArms, measure, title);

			// gender data
			setGenderData(studyArms, measure, title, genderOutputWriter, genderMap);

			// age data
			setAgeData(studyArms, measure, title);
		}

		// clean up arm data
		for (int grpId : studyArms.keySet()) {
			StudyArm arm = studyArms.get(grpId);
			int mfCount = arm.getFemalePatients() + arm.getMalePatients();
			if (mfCount != arm.getPatientsStarted()) {

				// System.err.println("Gender count does not match total start count!");
				// this is due to missing data from the overall status report
				// which should load before the gender counts... :-(
				if (arm.getPatientsStarted() == 0) {
					// update with our Male/Female count at this point...
					arm.setPatientsStarted(mfCount);
				}

			}

			int ethCount = arm.getEthnicityAsian() + arm.getEthnicityBlack() + arm.getEthnicityCaucasian()
					+ arm.getEthnicityHispanic() + arm.getEthnicityIndian() + arm.getEthnicityMiddleEast()
					+ arm.getEthnicityMultiple() + arm.getEthnicityNativeAmerican() + arm.getEthnicityNativeHawaiian()
					+ arm.getEthnicityOther();

			// we can at least do some data checking and update ethnicity to other
			// if the ethnicity count is less than total number of patients
			// who started
			if (ethCount < arm.getPatientsStarted()) {
				// System.err.println("Ethnic count does not match total start count!
				// Reassigning to 'other' category");
				int other = arm.getPatientsStarted() - ethCount + arm.getEthnicityOther();
				arm.setEthnicityOther(other);

			}

			// The data is messy! This does happen, but not going to try and solve it here
			// today
			// else {
			// if (ethCount > mfCount) {
			// System.err.println("More ethnicities than patients started!");
			// }
			// }

		}
	}

	private List<Intervention> getStudyInterventions(HashMap<String, Intervention> uniqueInterventions,
			ClinicalStudy study) {
		List<Intervention> interventions = new ArrayList<>();
		List<InterventionStruct> drugs = study.getIntervention();
		if (drugs != null) {
			for (InterventionStruct studyIntervention : drugs) {
				// what kind of intervention is being studied?
				String interventionType = studyIntervention.getInterventionType().name().toString();
				String interventionName = studyIntervention.getInterventionName().toString();

				Intervention i = new Intervention();

				if (uniqueInterventions.containsKey(interventionName) == false) {
					int refId = uniqueInterventions.size() + 1;
					i.setRefId(refId);
					i.setIType(interventionType);
					i.setName(interventionName);

					// If this is a drug, then we are interested in other names
					// as these could represent trade and brand names after the
					// drug has been approved (e.g. sample case given by
					// colleague was to search for brand name: 'Blenrep' and return
					// trials that were focused on 'Belantamab'
					if (interventionType.toLowerCase().trim().contentEquals("drug")) {
						for (String otherName : studyIntervention.getOtherName()) {
							i.getOtherNames().add(otherName);
						}
					}

					// cache in global table
					uniqueInterventions.put(interventionName, i);

				} else {
					// get cached version
					i = uniqueInterventions.get(interventionName);
				}

				// track all interventions
				interventions.add(i);

			}
		}
		return interventions;
	}

	/**
	 * Extract study countries
	 * 
	 * @param uniqueCountries
	 * @param study
	 * @return
	 */
	private List<Country> getStudyCountries(HashMap<String, Country> uniqueCountries, ClinicalStudy study) {

		List<Country> studyCountries = new ArrayList<>();
		if (study.getLocationCountries() != null) {
			for (String countryName : study.getLocationCountries().getCountry()) {
				if (StringUtils.isNotEmpty(countryName)) {

					if (uniqueCountries.containsKey(countryName) == false) {
						// generate unique ID for the country to store in our database at the end
						int refId = uniqueCountries.size() + 1;
						Country ctry = new Country();
						ctry.setRefId(refId);
						ctry.setName(countryName);

						uniqueCountries.put(countryName, ctry);
						studyCountries.add(ctry);
					} else {
						Country ctry = uniqueCountries.get(countryName);
						studyCountries.add(ctry);
					}
				}
			}
		}
		return studyCountries;
	}

	/**
	 * Parse age data
	 * 
	 * @param strAge
	 * @return
	 */
	private int getAge(String strAge) {

		int age = -1;
		strAge = strAge.toLowerCase().trim();
		if (strAge.contentEquals("n/a") || strAge.contentEquals("na"))
			return age;
		if (strAge.contains("years")) {
			strAge = strAge.replace("years", "").trim();
			try {
				age = Integer.parseInt(strAge);
			} catch (Exception e) {
				System.err.println("Error formatting age data: " + strAge);
			}
		}
		return age;
	}

	/**
	 * Test if study includes healthy volunteers
	 * 
	 * @param study
	 * @return
	 */
	private boolean hasHealthPatients(ClinicalStudy study) {
		boolean hasHealthy = false;
		if (study.getEligibility() != null) {
			if (study.getEligibility().getHealthyVolunteers() != null) {
				String healthy = study.getEligibility().getHealthyVolunteers().toString().toLowerCase().trim();
				if (healthy.contains("accepts healthy"))
					hasHealthy = true;
			}
		}
		return hasHealthy;
	}

	/**
	 * Extract unique study conditions
	 */
	private List<Condition> getStudyConditions(HashMap<String, Condition> uniqueConditions, ClinicalStudy study) {

		//
		// Are any conditions being studied? If not, then skip
		//
		// Note: While there are Mesh terms assigned under conditionBrowse node
		// the following warning messages are in the XML files
		// so it is better to use the listed conditions than
		// these mesh mappings. We will use our own alignment
		// via UMLS to assign conditions to Meddra or Mesh terms instead
		//
		// <!-- CAUTION: The following MeSH terms are assigned with an imperfect
		// algorithm -->
		//

		List<Condition> studyConditions = new ArrayList<>();
		if (study.getCondition() != null) {

			for (String condition : study.getCondition()) {
				if (StringUtils.isNotEmpty(condition)) {

					// Store as lower case to keep unique terms
					String lcCondition = condition.toLowerCase().trim();
					if (uniqueConditions.containsKey(lcCondition) == false) {
						// generate unique ID
						int refId = uniqueConditions.size() + 1;

						// Create condition object
						Condition con = new Condition();
						con.setRefId(refId);
						con.setTerm(condition);

						// store in table
						uniqueConditions.put(lcCondition, con);
						studyConditions.add(con);

					} else {
						// get object from previously loaded condition
						Condition con = uniqueConditions.get(lcCondition);
						studyConditions.add(con);
					}
				}
			}
		}
		return studyConditions;
	}

	/**
	 * Set the trial study phase, type and status
	 * 
	 * @param trial
	 * @param studyPhase
	 * @param studyType
	 * @param studyStatus
	 */
	private void setStudyPhaseTypeStatus(ClinicalTrial trial, String studyPhase, String studyType, String studyStatus) {
		// phase
		if (!StringUtils.isEmpty(studyPhase)) {
			if (getStudyPhases().containsKey(studyPhase) == true)
				trial.setStudyPhase(getStudyPhases().get(studyPhase));
			else
				System.err.println("Missing phase: " + studyPhase);
		}

		// type
		if (!StringUtils.isEmpty(studyType)) {

			// clean up
			if (studyType.toLowerCase().contains("observational"))
				studyType = "Observational";
			if (getStudyTypes().containsKey(studyType) == true)
				trial.setStudyType(getStudyTypes().get(studyType));
			else
				System.err.println("Missing study type: " + studyType);
		}

		// status
		if (!StringUtils.isEmpty(studyStatus)) {

			// clean up
			if (studyStatus.toLowerCase().contains("temporarily not available"))
				studyStatus = "Unknown";

			if (studyStatus.toLowerCase().contains("unknown"))
				studyStatus = "Unknown";

			if (studyStatus.toLowerCase().contains("no longer"))
				studyStatus = "Terminated";

			if (studyStatus.toLowerCase().contains("approved for marketing"))
				studyStatus = "Recruiting";

			if (getStudyStatus().containsKey(studyStatus) == true)
				trial.setStudyStatus(getStudyStatus().get(studyStatus));
			else
				System.err.println("Missing study status: " + studyStatus);

			// update if completed!
			if (studyStatus.toLowerCase().contains("complete"))
				trial.setComplete(true);
		}
	}

	private String getCollaborators(ClinicalStudy study) {
		String studyCollaborators = "";
		StringBuilder studyCollab = new StringBuilder();
		if (study.getSponsors().getCollaborator() != null && study.getSponsors().getCollaborator().size() > 0)
			for (SponsorStruct collaborator : study.getSponsors().getCollaborator()) {
				studyCollab.append(";");
				studyCollab.append(collaborator.getAgency().toString());
			}

		// remove leading semi-colon
		if (studyCollab.length() > 0) {

			studyCollaborators = studyCollab.toString().substring(1);
		}
		return studyCollaborators;
	}

	private void setAgeData(HashMap<Integer, StudyArm> arms, MeasureStruct measure, String title) {
		try {
			if (title.toLowerCase().trim().contentEquals("age")) {

				// get age data from category list
				for (MeasureClassStruct mstruct : measure.getClassList().getClazz()) {
					// extract age data
					for (MeasureCategoryStruct subgroup : mstruct.getCategoryList().getCategory()) {
						List<MeasurementStruct> measures = subgroup.getMeasurementList().getMeasurement();
						for (MeasurementStruct m : measures) {
							try {
								int grpId = Integer.parseInt(m.getGroupId().substring(1));
								if (arms.containsKey(grpId)) {
									StudyArm arm = arms.get(grpId);
									double mean_age = Double.parseDouble(m.getMeasureValue());
									double std_dev = Double.parseDouble(m.getSpread());
									arm.setMeanAge(mean_age);
									arm.setStdDev(std_dev);
								}
							} catch (Exception e) {
								// skip
							}
						}
					}
				}

			}
		} catch (Exception e) {
			// System.err.println("Error loading age data: " + e.toString());
		}
	}

	private void setGenderData(HashMap<Integer, StudyArm> arms, MeasureStruct measure, String title, PrintWriter genderOutputWriter, HashMap<String, String> genderMap) {
		try {
			if (title.contains("sex") || title.contains("gender")) {
				// extract gender data
				for (MeasureClassStruct mstruct : measure.getClassList().getClazz()) {
					for (MeasureCategoryStruct subgroup : mstruct.getCategoryList().getCategory()) {

						String subTitle = subgroup.getTitle().toLowerCase().trim();
						
						List<MeasurementStruct> measures = subgroup.getMeasurementList().getMeasurement();
						for (MeasurementStruct m : measures) {
							try {
								int grpId = Integer.parseInt(m.getGroupId().substring(1));
								if (arms.containsKey(grpId)) {
									StudyArm arm = arms.get(grpId);
									int count = Integer.parseInt(m.getMeasureValue());

									// Capture all the gender's for future analysis
									genderOutputWriter.write(subTitle + "\n");
									
									if ( genderMap.containsKey(subTitle) )
									{
										String gender = genderMap.get(subTitle);

										// Even split
										if ( gender.contentEquals("both") )
										{
											int half = (int) Math.floor(count/2.0);
											arm.setFemalePatients(half);
											arm.setMalePatients(half);
										}
										
										// Female count
										if (gender.contentEquals("female"))
											arm.setFemalePatients(count);
	
										// Male count
										if (gender.contentEquals("male"))
											arm.setMalePatients(count);
									}

								}
							} catch (Exception e) {
								// skip
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// System.err.println("Error loading gender: " + e.toString());
		}
	}

	private void setEthnicityData(HashMap<Integer, StudyArm> arms, MeasureStruct measure, String title) {
		try {
			// test if ethnicity was collected

			// Customized list
			if (title.contains("ethnicity") && title.contains("customize")) {

				// extract ethnicity data
				for (MeasureClassStruct mstruct : measure.getClassList().getClazz()) {

					// In this version, the ethnicity title is at the top
					if (mstruct.getTitle() != null) {
						String subTitle = mstruct.getTitle().toLowerCase().trim();
						String ethnicity = "other";

						// Lookup ethnicity category
						if (this.ethnicities.containsKey(subTitle) == true)
							ethnicity = this.ethnicities.get(subTitle);

						for (MeasureCategoryStruct subgroup : mstruct.getCategoryList().getCategory()) {

							List<MeasurementStruct> measures = subgroup.getMeasurementList().getMeasurement();
							for (MeasurementStruct m : measures) {
								try {
									int grpId = Integer.parseInt(m.getGroupId().substring(1));
									if (arms.containsKey(grpId)) {
										StudyArm arm = arms.get(grpId);
										int count = Integer.parseInt(m.getMeasureValue());

										// Ethnic category levels
										if (ethnicity.contentEquals("asian"))
											arm.setEthnicityAsian(count);

										if (ethnicity.contentEquals("black"))
											arm.setEthnicityBlack(count);

										if (ethnicity.contentEquals("caucasian"))
											arm.setEthnicityCaucasian(count);

										if (ethnicity.contentEquals("hispanic"))
											arm.setEthnicityHispanic(count);

										if (ethnicity.contentEquals("indian"))
											arm.setEthnicityIndian(count);

										if (ethnicity.contentEquals("middle eastern"))
											arm.setEthnicityMiddleEast(count);

										if (ethnicity.contentEquals("multiple/biracial"))
											arm.setEthnicityMultiple(count);

										if (ethnicity.contentEquals("native american"))
											arm.setEthnicityNativeAmerican(count);

										if (ethnicity.contentEquals("native hawaiian"))
											arm.setEthnicityNativeHawaiian(count);

										if (ethnicity.contentEquals("other"))
											arm.setEthnicityOther(count);

									}
								} catch (Exception e) {
									// skip
								}
							}
						}
					} else {
						// secondary format? This doesn't look like there
						// is any actual data to extract investigating the following
						// CT: https://clinicaltrials.gov/show/NCT04186481
						// which led us here... skip for now!
					}
				}

			}

			// non-customized!
			if (title.contains("ethnicity") && !title.contains("not collect") && !title.contains("customize")) {
				// extract ethnicity data
				for (MeasureClassStruct mstruct : measure.getClassList().getClazz()) {

					// Some of the entries are putting the ethnicity at this level..
					boolean topLevel = false;
					if (mstruct.getTitle() != null) {
						String mTitle = mstruct.getTitle().toLowerCase().trim();
						String ethnicity = "other";
						if (this.ethnicities.containsKey(mTitle) == true) {
							ethnicity = this.ethnicities.get(mTitle);
							topLevel = true;
						}

						for (MeasureCategoryStruct subgroup : mstruct.getCategoryList().getCategory()) {
							List<MeasurementStruct> measures = subgroup.getMeasurementList().getMeasurement();

							for (MeasurementStruct m : measures) {
								try {
									int grpId = Integer.parseInt(m.getGroupId().substring(1));
									if (arms.containsKey(grpId)) {
										StudyArm arm = arms.get(grpId);
										int count = Integer.parseInt(m.getMeasureValue());

										// Ethnic category levels
										if (ethnicity.contentEquals("asian"))
											arm.setEthnicityAsian(count);

										if (ethnicity.contentEquals("black"))
											arm.setEthnicityBlack(count);

										if (ethnicity.contentEquals("caucasian"))
											arm.setEthnicityCaucasian(count);

										if (ethnicity.contentEquals("hispanic"))
											arm.setEthnicityHispanic(count);

										if (ethnicity.contentEquals("indian"))
											arm.setEthnicityIndian(count);

										if (ethnicity.contentEquals("middle eastern"))
											arm.setEthnicityMiddleEast(count);

										if (ethnicity.contentEquals("multiple/biracial"))
											arm.setEthnicityMultiple(count);

										if (ethnicity.contentEquals("native american"))
											arm.setEthnicityNativeAmerican(count);

										if (ethnicity.contentEquals("native hawaiian"))
											arm.setEthnicityNativeHawaiian(count);

										if (ethnicity.contentEquals("other"))
											arm.setEthnicityOther(count);

									}
								} catch (Exception e) {
									// skip
								}
							}

						}

					}

					if (topLevel == false) {

						for (MeasureCategoryStruct subgroup : mstruct.getCategoryList().getCategory()) {

							if (subgroup.getTitle() != null) {
								String subTitle = subgroup.getTitle().toLowerCase().trim();
								String ethnicity = "other";

								// Lookup ethnicity category
								if (this.ethnicities.containsKey(subTitle) == true)
									ethnicity = this.ethnicities.get(subTitle);

								List<MeasurementStruct> measures = subgroup.getMeasurementList().getMeasurement();
								for (MeasurementStruct m : measures) {
									try {
										int grpId = Integer.parseInt(m.getGroupId().substring(1));
										if (arms.containsKey(grpId)) {
											StudyArm arm = arms.get(grpId);
											int count = Integer.parseInt(m.getMeasureValue());

											// Ethnic category levels
											if (ethnicity.contentEquals("asian"))
												arm.setEthnicityAsian(count);

											if (ethnicity.contentEquals("black"))
												arm.setEthnicityBlack(count);

											if (ethnicity.contentEquals("caucasian"))
												arm.setEthnicityCaucasian(count);

											if (ethnicity.contentEquals("hispanic"))
												arm.setEthnicityHispanic(count);

											if (ethnicity.contentEquals("indian"))
												arm.setEthnicityIndian(count);

											if (ethnicity.contentEquals("middle eastern"))
												arm.setEthnicityMiddleEast(count);

											if (ethnicity.contentEquals("multiple/biracial"))
												arm.setEthnicityMultiple(count);

											if (ethnicity.contentEquals("native american"))
												arm.setEthnicityNativeAmerican(count);

											if (ethnicity.contentEquals("native hawaiian"))
												arm.setEthnicityNativeHawaiian(count);

											if (ethnicity.contentEquals("other"))
												arm.setEthnicityOther(count);

										}
									} catch (Exception e) {
										// skip
									}
								}
							}
						}
					}

				}
			}
		} catch (Exception e) {

			// There are still a couple errors here, but I think we got most of the
			// various input forms working

			// Suppress the warnings for now...
			// System.err.println("Error loading ethnicities: " + e.toString());
		}
	}

	/**
	 * Extract events from the EventsStruct and update the hashmap with key-value
	 * pairs (key = event name as a string, value = count)
	 * 
	 * @param events
	 * @param eventCounts
	 * @return
	 */
	private int getEvents(boolean isSerious, EventsStruct events, HashMap<String, Integer> eventCounts,
			HashMap<Integer, StudyArm> studyArms, HashMap<String, AdverseEvent> adverseEvents) {
		int totalPatientsAffected = 0;

		// Extract event counts
		CategoryList categoryList = events.getCategoryList();
		for (EventCategoryStruct category : categoryList.getCategory()) {

			// category of event
			String catName = category.getTitle();
			// System.out.println(catName);

			if (category.getEventList() != null) {
				EventList eventList = category.getEventList();
				int c = 0;
				for (EventStruct event : eventList.getEvent()) {
					// what is the event we are looking at
					String eventName = "";
					c++;
					if (event.getSubTitle() != null) {
						eventName = event.getSubTitle().getValue();

						// skip totals
						if (eventName.toLowerCase().contains("total") == false) {

							// have we seen this adverse event?
							AdverseEvent ae = null;

							// Store as lower case to help with matching to Meddra PT terms
							String lcEvent = eventName.toLowerCase().trim();
							if (adverseEvents.containsKey(lcEvent) == false) {
								// generate unique ID
								int refId = adverseEvents.size() + 1;
								ae = new AdverseEvent();
								ae.setRefId(refId);
								ae.setTerm(eventName);
								ae.setSerious(isSerious);
								adverseEvents.put(lcEvent, ae);
							} else {
								// pull from hashmap lookup
								ae = adverseEvents.get(lcEvent);
							}

							if (eventCounts.containsKey(eventName) == false)
								eventCounts.put(eventName, 0);

							// get counts for each group in study
							for (EventCountsStruct count : event.getCounts()) {

								int groupId = Integer.parseInt(count.getGroupId().substring(1));
								if (studyArms.containsKey(groupId)) {
									int affected = 0;

									StudyArm arm = studyArms.get(groupId);
									HashMap<AdverseEvent, Integer> armEvents = arm.getAdverseEvents();
									if (armEvents.containsKey(ae) == false) {
										armEvents.put(ae, 0);
									} else {
										affected = armEvents.get(ae);
									}

									// update total affected
									if (count.getSubjectsAffected() != null)
										affected = affected + count.getSubjectsAffected().intValue();

									armEvents.put(ae, affected);

									/*
									 * skipping at risk for now... int atRisk = 0; try { atRisk =
									 * count.getSubjectsAtRisk().intValue(); } catch (Exception g) { }
									 */

									// track all patients affected by this AE
									eventCounts.put(eventName, eventCounts.get(eventName) + affected);
								}
							}
							
						} else {
							// we want the patients affected count from total
							// get counts for each group in study
							for (EventCountsStruct count : event.getCounts()) {
								// String group = count.getGroupId();

								// String group = count.getGroupId();
								int affected = 0;
								int atRisk = 0;

								// these can be null...
								try {
									affected = count.getSubjectsAffected().intValue();
								} catch (Exception g) {
								}
								try {
									atRisk = count.getSubjectsAtRisk().intValue();
								} catch (Exception g) {
								}

								totalPatientsAffected = totalPatientsAffected + affected;
							}
						}
					}
				}
			}

		}
		return totalPatientsAffected;
	}

	/**
	 * List of all study phases (pre-loaded in our SQL build script)
	 * 
	 * @return
	 */
	public static HashMap<String, CtPhase> getStudyPhases() {

		HashMap<String, CtPhase> allPhases = new HashMap<>();
		String[] status = new String[] { "Early Phase 1", "Phase 1", "Phase 1/Phase 2", "Phase 2", "Phase 2/Phase 3",
				"Phase 3", "Phase 3/Phase 4", "Phase 4", "NA" };

		int idx = 0;
		for (String s : status)
			allPhases.put(s, new CtPhase(++idx, s));

		return allPhases;
	}

	/**
	 * List of all study types (pre-loaded in our SQL build script)
	 * 
	 * @return
	 */
	public static HashMap<String, CtType> getStudyTypes() {

		HashMap<String, CtType> allTypes = new HashMap<>();
		String[] status = new String[] { "Interventional", "Observational", "Patient Registries", "Expanded Access",
				"Unknown" };

		int idx = 0;
		for (String s : status)
			allTypes.put(s, new CtType(++idx, s));

		return allTypes;
	}

	/**
	 * List of all study status labels (pre-loaded in our SQL build script)
	 * 
	 * @return
	 */
	public HashMap<String, CtStatus> getStudyStatus() {

		HashMap<String, CtStatus> allStatus = new HashMap<>();
		String[] status = new String[] { "Not yet recruiting", "Recruiting", "Enrolling by invitation",
				"Active, not recruiting", "Suspended", "Terminated", "Completed", "Withdrawn", "Available", "Unknown" };

		int idx = 0;
		for (String s : status)
			allStatus.put(s, new CtStatus(++idx, s));

		return allStatus;
	}
}
