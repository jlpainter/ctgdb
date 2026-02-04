package com.jivecast.ctgdb.app;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.jivecast.ctgdb.om.AdverseEvent;
import com.jivecast.ctgdb.om.ClinicalTrial;
import com.jivecast.ctgdb.om.Condition;
import com.jivecast.ctgdb.om.Country;
import com.jivecast.ctgdb.om.Intervention;
import com.jivecast.ctgdb.om.StudyArm;
import com.jivecast.ctgdb.umls.Atom;
import com.jivecast.ctgdb.umls.UmlsLoader;
import com.jivecast.ctgdb.xml.StudyParser;

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
 * Extract the clinical trial data from XML source files and generate a database
 * import to represent the data
 * 
 * @author Jeffery Painter <jeff@jivecast.com>
 * @created: 2021-Sep-17
 *
 */
public class CreateDatabase {

	// Data path to our extracted all_public_data_xml.zip
	public static String DATA_PATH = "src/main/data/";

	// UMLS extract containing Meddra (MRCONSO.RRF, MRREL.RRF and MRHIER.RRF)
	public static String UMLS_PATH = "src/main/resources/umls/";

	// CSV output delimiter
	public static String DELIMITER = ",";

	// Debugging flags
	public static boolean PROCESS_ALL_DATA = true;

	// if we set above to false, then only process this many trials
	public static int max_test_process = 800;

	// fuzzy matching to Meddra takes longer to process
	public static boolean USE_FUZZY_MAP = true;
	private static final double FUZZY_MATCH_THRESHOLD = 0.70;

	// Use the sample data!
	private static final boolean USE_SAMPLE_DATA = false;

	// Output paths
	public static String OUTPUT_PATH = "src/main/resources/db_csv/";

	public static String DB_CLINICAL_TRIAL_CSV = OUTPUT_PATH + "clinical_trial.csv";
	public static String DB_INTERVENTIONS_CSV = OUTPUT_PATH + "interventions.csv";
	public static String DB_INTERVENTION_OTHER_NAMES_CSV = OUTPUT_PATH + "intervention_other_names.csv";
	public static String DB_COUNTRIES_CSV = OUTPUT_PATH + "countries.csv";
	public static String DB_CONDITION_CSV = OUTPUT_PATH + "conditions.csv";
	public static String DB_MEDDRA_CSV = OUTPUT_PATH + "meddra.csv";
	public static String DB_MEDDRA_HIER_CSV = OUTPUT_PATH + "meddra_hierarchy.csv";

	// linked tables
	public static String DB_CT_INTERVENTION_CSV = OUTPUT_PATH + "ct_intervention.csv";
	public static String DB_CT_ADVERSE_EVENTS_CSV = OUTPUT_PATH + "ct_adverse_events.csv";
	public static String DB_ADVERSE_EVENT_CSV = OUTPUT_PATH + "adverse_event.csv";
	public static String DB_CT_ARMS_CSV = OUTPUT_PATH + "ct_arms.csv";
	public static String DB_CT_CONDITIONS_CSV = OUTPUT_PATH + "ct_conditions.csv";
	public static String DB_CT_COUNTRY_CSV = OUTPUT_PATH + "ct_country.csv";

	// Unique genders
	public static String UNIQUE_GENDER = OUTPUT_PATH + "all_genders.txt";

	public static void main(String[] args) {

		// output messages to console
		Logger log = new Logger();

		// Create UMLS loader and read MedDRA into memory
		UmlsLoader umls = new UmlsLoader();
		umls.loadMedDRA();
		HashMap<String, Atom> meddra = umls.getMedDRA();
		HashMap<String, HashMap<String, Boolean>> meddraHierarchy = umls.getMedDRAHierarchy();

		HashMap<String, String> genderMaps = getGenderMaps();

		try {
			// Initialize once
			StudyParser parser = new StudyParser();

			List<String> files = new ArrayList<>();

			// Use the sample data :-)
			if (USE_SAMPLE_DATA == true) {

				// sample data
				// String[] samples = new String[] { "NCT03960489.xml" };
				String[] samples = new String[] { "NCT02273726.xml" };

				// String [] samples = new String[] { "NCT02168842.xml", "NCT03773796.xml",
				// "NCT04456634.xml", "NCT04901897.xml", "NCT04993339.xml" };
				String sample_path = "src/main/sample_data/";
				for (String sample : samples) {
					files.add(sample_path + sample);
				}
			} else {
				// load all files
				files = getDataFiles();
				log.log("Total XML files to process: " + files.size());

			}

			// Register writers mapping to tables in our final database
			HashMap<String, PrintWriter> writers = new HashMap<>();

			// top level tables
			writers.put("clinical_trial", new PrintWriter(DB_CLINICAL_TRIAL_CSV));
			writers.put("interventions", new PrintWriter(DB_INTERVENTIONS_CSV));
			writers.put("intervention_other_names", new PrintWriter(DB_INTERVENTION_OTHER_NAMES_CSV));
			writers.put("countries", new PrintWriter(DB_COUNTRIES_CSV));
			writers.put("adverse_event", new PrintWriter(DB_ADVERSE_EVENT_CSV));
			writers.put("conditions", new PrintWriter(DB_CONDITION_CSV));
			writers.put("meddra", new PrintWriter(DB_MEDDRA_CSV));
			writers.put("meddra_hierarchy", new PrintWriter(DB_MEDDRA_HIER_CSV));

			// linked tables
			writers.put("ct_intervention", new PrintWriter(DB_CT_INTERVENTION_CSV));
			writers.put("ct_arms", new PrintWriter(DB_CT_ARMS_CSV));
			writers.put("ct_adverse_events", new PrintWriter(DB_CT_ADVERSE_EVENTS_CSV));
			writers.put("ct_conditions", new PrintWriter(DB_CT_CONDITIONS_CSV));
			writers.put("ct_country", new PrintWriter(DB_CT_COUNTRY_CSV));

			// Gender output
			PrintWriter genderOutputWriter = new PrintWriter(UNIQUE_GENDER);

			// write header lines for each file
			HashMap<String, String[]> csvHeaders = getHeaders();
			for (String tbl : writers.keySet()) {
				PrintWriter writer = writers.get(tbl);
				if (csvHeaders.containsKey(tbl)) {
					String[] headerCols = csvHeaders.get(tbl);
					String headerLine = String.join(DELIMITER, headerCols);
					writer.println(headerLine);
				} else {
					System.err.println("Missing header details for table: " + tbl);
				}
			}

			// Track these for uniqueness to store in database at the end
			HashMap<String, Country> uniqueCountries = new HashMap<>();
			HashMap<String, Condition> uniqueConditions = new HashMap<>();
			HashMap<String, AdverseEvent> uniqueAEs = new HashMap<>();
			HashMap<String, Intervention> uniqueInterventions = new HashMap<>();

			// we will create unique IDs for all trials to reference in database
			int trialRefId = 1;
			int studyArmRefId = 1;
			int ctAdverseEventRefId = 1;

			// process all
			for (String file : files) {

				// are we processing all data files?
				if (PROCESS_ALL_DATA == true || trialRefId < max_test_process) {
					// log.log("Trial: " + trialRefId);

					// Process a new trial
					ClinicalTrial trial = parser.convertXMLToObjModel(trialRefId, studyArmRefId, file,
							uniqueInterventions, uniqueCountries, uniqueConditions, uniqueAEs, genderOutputWriter,
							genderMaps);

					if (trial != null)
						ctAdverseEventRefId = processClinicalTrialToCSV(trial, writers, ctAdverseEventRefId);

					// progress tracker
					if (trialRefId % 50000 == 0)
						log.log("Trials processed... " + trialRefId);

					// did we get a record?
					if (trial != null) {
						// update number of study arms seen
						if (trial.getArms().size() > 0)
							studyArmRefId = studyArmRefId + trial.getArms().size();

						// next trial
						trialRefId++;
					}
				}
			}

			// debug - print all ethnicities for creating map file
//			for ( String eth : parser.getEthnicities().keySet() )
//			{
//				System.out.println(eth);
//			}

			// report some stats on data ingestion
			log.log("Unique countries: " + uniqueCountries.size());
			log.log("Unique interventions: " + uniqueInterventions.size());
			log.log("Unique conditions: " + uniqueConditions.size());
			log.log("Unique AEs: " + uniqueAEs.size());

			// output countries, conditions and AEs - these are global and shared among CTs
			// sort by value prior to writing to get all id's in order
			PrintWriter writer = writers.get("countries");
			for (String country : uniqueCountries.keySet()) {
				Country c = uniqueCountries.get(country);
				writer.println(c.getRefId() + DELIMITER + Util.cleanAndQuoteContent(c.getName()));
			}

			// Test that we have loaded meddra before processing
			if (meddra != null) {
				writer = writers.get("meddra");
				HashMap<String, Integer> meddraMap = new HashMap<String, Integer>();
				int meddraRefId = 1;
				for (String aui : meddra.keySet()) {

					Atom atom = meddra.get(aui);
					atom.setRefId(meddraRefId);

					// output CSV
					writer.println(atom.toCSV(DELIMITER));

					meddraMap.put(aui, meddraRefId);
					meddraRefId++;
				}

				// export parent-child relationships with Meddra terms
				writer = writers.get("meddra_hierarchy");
				for (String aui : meddra.keySet()) {

					// parent AUI
					if (meddraHierarchy.containsKey(aui)) {
						int parentId = meddraMap.get(aui);
						for (String childAui : meddraHierarchy.get(aui).keySet()) {
							int childId = meddraMap.get(childAui);
							writer.println(parentId + DELIMITER + childId);
						}
					}
				}
			}

			// Interventions being studied
			writer = writers.get("interventions");
			for (String treatment : uniqueInterventions.keySet()) {
				Intervention i = uniqueInterventions.get(treatment);

				// write intervention
				writer.println(i.getRefId() + DELIMITER + Util.cleanAndQuoteContent(i.getName()) + DELIMITER
						+ Util.cleanAndQuoteContent(i.getIType()));
			}

			// Alternate names for interventions
			writer = writers.get("intervention_other_names");
			for (String treatment : uniqueInterventions.keySet()) {
				Intervention i = uniqueInterventions.get(treatment);
				for (String altName : i.getOtherNames()) {
					// write intervention alternate name
					writer.println(i.getRefId() + DELIMITER + Util.cleanAndQuoteContent(altName));
				}
			}

			writer = writers.get("conditions");

			if (meddra != null) {
				// Use parallel processing streams to map all conditions first
				umls.setupMeddraBigrams();
				log.log("Performing condition maps in parallel...");
				List<Condition> conditionList = uniqueConditions.values().stream().collect(Collectors.toList());
				conditionList.parallelStream().forEach(c -> mapConditionToMeddra(umls, c));
				int unmappedConditions = 0;
				for (Condition c : conditionList) {
					int meddraId = c.getMeddraRefId();

					int exactMatch = 0;
					if (c.isExactMatch() == true)
						exactMatch = 1;

					// write conditions
					if (meddraId > 0)
						writer.println(c.getRefId() + DELIMITER + Util.cleanAndQuoteContent(c.getTerm()) + DELIMITER
								+ meddraId + DELIMITER + exactMatch);
					else {
						writer.println(c.getRefId() + DELIMITER + Util.cleanAndQuoteContent(c.getTerm()) + DELIMITER
								+ "" + DELIMITER + exactMatch);
						unmappedConditions++;
					}
				}

				log.log("Total conditions: " + uniqueConditions.size());
				log.log("Conditions unmapped: " + unmappedConditions);

				writer = writers.get("adverse_event");
				List<AdverseEvent> aeList = uniqueAEs.values().stream().collect(Collectors.toList());
				log.log("Performing AE maps in parallel...");
				aeList.parallelStream().forEach(ae -> mapAdverseEventToMeddra(umls, ae));

				for (AdverseEvent ae : aeList) {
					int meddraId = ae.getMeddraRefId();

					int exactMatch = 0;
					if (ae.isExactMatch() == true)
						exactMatch = 1;

					// write conditions
					if (meddraId > 0)
						writer.println(ae.getRefId() + DELIMITER + Util.cleanAndQuoteContent(ae.getTerm()) + DELIMITER
								+ meddraId + DELIMITER + exactMatch);
					else {
						writer.println(ae.getRefId() + DELIMITER + Util.cleanAndQuoteContent(ae.getTerm()) + DELIMITER
								+ "" + DELIMITER + exactMatch);
					}
				}
			}

			// Flush and close all CSV writers
			for (String table : writers.keySet()) {
				log.log("Closing CSV file for table: " + table);
				writer = writers.get(table);
				writer.flush();
				writer.close();
			}

			// Close gender output writer
			genderOutputWriter.flush();
			genderOutputWriter.close();

			// completed!
			log.log("Total trials to add to database: " + trialRefId);
			log.log("Done!");

		} catch (Exception e) {
			System.err.println("Error processing data: " + e.toString());
		}
	}

	/**
	 * Map a condition to a MedDRA atom when possible
	 * 
	 * @param umls
	 * @param c
	 */
	private static void mapConditionToMeddra(UmlsLoader umls, Condition c) {

		// Initialize an atom and set link to -1
		Atom mdrPt = null;
		c.setMeddraRefId(-1);

		// Test if we are using fuzzy mapping or not
		if (USE_FUZZY_MAP == true) {
			mdrPt = umls.searchForMeddraAtom(c.getTerm());
			if (mdrPt != null) {
				// we want to track exact matches
				c.setMeddraRefId(mdrPt.getRefId());
				c.setExactMatch(true);
			} else {
				mdrPt = umls.findClosestMatchingMeddraAtom(c.getTerm(), FUZZY_MATCH_THRESHOLD);
				if (mdrPt != null) {
					// we want to track exact matches
					c.setMeddraRefId(mdrPt.getRefId());
					c.setExactMatch(false);
				}
			}
		} else {
			mdrPt = umls.searchForMeddraAtom(c.getTerm());
			if (mdrPt != null) {
				// we want to track exact matches
				c.setMeddraRefId(mdrPt.getRefId());
				c.setExactMatch(true);
			}
		}

		return;
	}

	/**
	 * Map a condition to a MedDRA atom when possible
	 * 
	 * @param umls
	 * @param ae
	 */
	private static void mapAdverseEventToMeddra(UmlsLoader umls, AdverseEvent ae) {

		// Initialize an atom and set link to -1
		Atom mdrPt = null;
		ae.setMeddraRefId(-1);

		// Test if we are using fuzzy mapping or not
		if (USE_FUZZY_MAP == true) {
			mdrPt = umls.searchForMeddraAtom(ae.getTerm());
			if (mdrPt != null) {
				// we want to track exact matches
				ae.setMeddraRefId(mdrPt.getRefId());
				ae.setExactMatch(true);
			} else {
				mdrPt = umls.findClosestMatchingMeddraAtom(ae.getTerm(), FUZZY_MATCH_THRESHOLD);
				if (mdrPt != null) {
					// we want to track exact matches
					ae.setMeddraRefId(mdrPt.getRefId());
					ae.setExactMatch(false);
				}
			}
		} else {
			mdrPt = umls.searchForMeddraAtom(ae.getTerm());
			if (mdrPt != null) {
				// we want to track exact matches
				ae.setMeddraRefId(mdrPt.getRefId());
				ae.setExactMatch(true);
			}
		}
		return;
	}

	/**
	 * Update all related tables for a single clinical trial
	 * 
	 * @param trial
	 * @param writers
	 */
	private static int processClinicalTrialToCSV(ClinicalTrial trial, HashMap<String, PrintWriter> writers,
			int ctAdverseEventRefId) {

		// System.out.println("Writing trial data... " + trial.getRefId());

		writers.get("clinical_trial").println(trial.toCSV(DELIMITER));

		// Write the linked tables out to CSV
		for (Intervention i : trial.getInterventions()) {
			String csv = trial.getRefId() + "," + i.getRefId();
			writers.get("ct_intervention").println(csv);
		}

		for (StudyArm arm : trial.getArms()) {
			String csv = arm.toCSV(DELIMITER);
			writers.get("ct_arms").println(csv);

			// extract adverse events for this arm
			for (AdverseEvent ae : arm.getAdverseEvents().keySet()) {
				int count = arm.getAdverseEvents().get(ae);
				if (count > 0) {
					// generate CSV output
					// ID|TRIAL_ID|ARM_ID|AE_ID|IS_SERIOUS|COUNT
					StringBuilder aeCsv = new StringBuilder();

					aeCsv.append(ctAdverseEventRefId);
					aeCsv.append(DELIMITER);

					aeCsv.append(trial.getRefId());
					aeCsv.append(DELIMITER);

					aeCsv.append(arm.getRefId());
					aeCsv.append(DELIMITER);

					aeCsv.append(ae.getRefId());
					aeCsv.append(DELIMITER);

					// is this a serious event?
					if (ae.isSerious() == true)
						aeCsv.append(1);
					else
						aeCsv.append(0);
					aeCsv.append(DELIMITER);

					// total count
					aeCsv.append(count);

					// Record the adverse event
					writers.get("ct_adverse_events").println(aeCsv.toString());

					// next arm AE id
					ctAdverseEventRefId++;
				}
			}
		}

		for (Country country : trial.getCountries()) {
			String csv = trial.getRefId() + "," + country.getRefId();
			writers.get("ct_country").println(csv);
		}

		for (Condition c : trial.getConditions()) {
			String csv = trial.getRefId() + "," + c.getRefId();
			writers.get("ct_conditions").println(csv);
		}

		return ctAdverseEventRefId;
	}

	/**
	 * All unique gender values were extracted and this map was built by manual
	 * inspection of the types listed in the raw XML files
	 * 
	 * @return
	 */
	private static HashMap<String, String> getGenderMaps() {

		HashMap<String, String> genders = new HashMap<>();
		
		// Both male and female found
		genders.put("both men and women", "both");
		genders.put("male and female", "both");
		genders.put("female-male", "both");

		// Male labels found
		genders.put("boy", "male");
		genders.put("cisgender man", "male");
		genders.put("male", "male");
		genders.put("male children", "male");
		genders.put("male-male", "male");
		genders.put("male only", "male");
		genders.put("males", "male");
		genders.put("man", "male");
		genders.put("men", "male");
		genders.put("number of male participants per arm", "male");

		// Female labels found
		genders.put("famale", "female");
		genders.put("female", "female");
		genders.put("female - child-bearing potential", "female");
		genders.put("female children", "female");
		genders.put("female-female", "female");
		genders.put("number of female participants per arm", "female");
		genders.put("woman", "female");
		genders.put("women", "female");
		genders.put("female - non child-bearing potential", "female");
		genders.put("females", "female");
		genders.put("girl", "female");
		return genders;
	}

	/**
	 * Find all clinicaltrials.gov XML files to process
	 * 
	 * @return
	 */
	public static List<String> getDataFiles() {
		List<String> dataFiles = new ArrayList<>();
		try {
			// Top level directory contains sub-directories
			File dataDirectory = new File(DATA_PATH);
			String[] pathnames = dataDirectory.list();
			for (String path : pathnames) {
				if (path.startsWith("NCT")) {
					// System.out.println(path);

					String spath = DATA_PATH + path;
					File subdir = new File(spath);
					String[] datafiles = subdir.list();
					for (String filename : datafiles) {
						// extract the XML files only
						if (filename.contains("xml")) {
							String filePath = spath + "/" + filename;
							// System.out.println(filePath);
							dataFiles.add(filePath);
						}
					}

				}
			}

		} catch (Exception e) {
			System.err.println("Error getting XML file list: " + e.toString());
		}
		return dataFiles;
	}

	/**
	 * Headers for each table define all column names This should align the with
	 * definitions found in ct_db.sql file
	 * 
	 * @return
	 */
	public static HashMap<String, String[]> getHeaders() {
		HashMap<String, String[]> headers = new HashMap<String, String[]>();
		headers.put("meddra", new String[] { "id", "aui", "cui", "tty", "code", "term" });
		headers.put("meddra_hierarchy", new String[] { "parent_id", "child_id" });
		headers.put("countries", new String[] { "id", "country" });
		headers.put("ct_country", new String[] { "trial_id", "country_id" });
		headers.put("conditions", new String[] { "id", "term", "meddra_id", "exact_match" });
		headers.put("ct_conditions", new String[] { "trial_id", "condition_id" });
		headers.put("interventions", new String[] { "id", "name", "int_type" });
		headers.put("intervention_other_names", new String[] { "intervention_id", "other_name" });
		headers.put("ct_intervention", new String[] { "trial_id", "intervention_id" });
		headers.put("clinical_trial",
				new String[] { "id", "status_id", "type_id", "phase_id", "brief_title", "full_title ", "study_sponsor",
						"study_collab", "url", "nct_id", "study_id", "start_date", "end_date", "trial_length_days",
						"pats_enrolled", "min_age", "max_age", "has_female_pats", "has_male_pats", "has_healthy_pats",
						"is_complete" });
		headers.put("ct_arms",
				new String[] { "arm_id", "trial_id", "group_id", "group_title", "group_desc", "female", "male", "asian",
						"black", "caucasian", "hispanic", "indian", "middle_east", "multiple_eth", "native_american",
						"native_hawaiian", "other_eth", "mean_age", "std_dev", "is_placebo", "is_healthy",
						"pats_started", "pats_complete", "pats_lost", "pats_withdraw", "pats_died" });
		headers.put("adverse_event", new String[] { "id", "adverse_event", "meddra_id", "exact_match" });
		headers.put("ct_adverse_events",
				new String[] { "id", "trial_id", "arm_id", "ae_id", "is_serious", "pats_affected" });
		return headers;
	}
}
