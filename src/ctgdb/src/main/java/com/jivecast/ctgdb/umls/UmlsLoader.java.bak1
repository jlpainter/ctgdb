package com.jivecast.ctgdb.umls;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.jivecast.ctgdb.app.Logger;

/*
 *  ------------------------------------------------------------------
 *  Jivecast
 *
 *  Copyright (c) 2003-2021 Jivecast. All Rights Reserved. Permission
 *  to copy, modify and distribute this software and code
 *  included and its documentation (collectively, the "PROGRAM") for
 *  any purpose is hereby prohibited.
 *
 *  THE PROGRAM IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EITHER EXPRESSED OR IMPLIED, INCLUDING, WITHOUT
 *  LIMITATION, WARRANTIES THAT THE PROGRAM IS FREE OF
 *  DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR
 *  NON-INFRINGING. THE ENTIRE RISK AS TO THE QUALITY AND
 *  PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD ANY PART
 *  OF THE PROGRAM PROVE DEFECTIVE IN ANY RESPECT, YOU
 *  (NOT JIVECAST) ASSUME THE COST OF ANY NECESSARY SERVICING,
 *  REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES
 *  AN ESSENTIAL PART OF THIS LICENSE. NO USE OF
 *  THE PROGRAM IS AUTHORIZED HEREUNDER EXCEPT
 *  UNDER THIS DISCLAIMER.
 *
 *  ------------------------------------------------------------------
 */

//
// Original author: Jeffery Painter <jeff@jivecast.com>
// All rights reserved.
//

public class UmlsLoader {

	// Terminology root terms
	public static final String MDR_ROOT = "A1605890";
	public static final String WHOART_ROOT = "A1605890";
	public static final String COSTART_ROOT = "A1605890";

	// Data file definitions - files are gzipped to save space
	private String MRCONSO = "MRCONSO.RRF.gz";
	private String MRREL = "MRREL.RRF.gz";
	private String MRHIER = "MRHIER.RRF.gz";
	private String DATA_PATH = "src/main/resources/umls/";

	// MedDRA
	private HashMap<String, List<String>> MEDDRA_AUI_PATHS;
	private HashMap<String, Atom> MEDDRA_ATOMS;
	private HashMap<String, HashMap<String, Boolean>> MEDDRA_CUIS;
	private HashMap<String, BiGramSequence> MEDDRA_BIGRAM;
	private HashMap<String, List<String>> MEDDRA_TERMS;

	// store child-parent AUIs for quick lookup
	private HashMap<String, HashMap<String, Boolean>> MEDDRA_HIER;

	// cache the paths for meddra PTs
	private HashMap<String, String> MEDDRA_AUI_HLT;
	private HashMap<String, String> MEDDRA_AUI_HLGT;
	private HashMap<String, String> MEDDRA_AUI_SOC;


	// WHO-ART
	private HashMap<String, Atom> WHO_ART_ATOMS;
	private HashMap<String, HashMap<String, Boolean>> WHO_ART_CUIS;
	private HashMap<String, BiGramSequence> WHO_ART_BIGRAM;
	private HashMap<String, List<String>> WHO_ART_TERMS;

	// COSTART
	private HashMap<String, Atom> COSTART_ATOMS;
	private HashMap<String, HashMap<String, Boolean>> COSTART_CUIS;
	private HashMap<String, BiGramSequence> COSTART_BIGRAM;
	private HashMap<String, List<String>> COSTART_TERMS;
	
	private Logger log;

	public UmlsLoader() {

		this.MEDDRA_ATOMS = new HashMap<>();
		this.MEDDRA_BIGRAM = new HashMap<>();
		this.MEDDRA_TERMS = new HashMap<>();
		this.MEDDRA_AUI_PATHS = new HashMap<>();
		this.MEDDRA_CUIS = new HashMap<>();
		this.MEDDRA_HIER = new HashMap<>();

		// cached lookups
		this.MEDDRA_AUI_HLT = new HashMap<>();
		this.MEDDRA_AUI_HLGT = new HashMap<>();
		this.MEDDRA_AUI_SOC = new HashMap<>();

		
		// WHO-ART
		this.WHO_ART_ATOMS = new HashMap<>();
		this.WHO_ART_CUIS = new HashMap<>();
		this.WHO_ART_BIGRAM = new HashMap<>();
		this.WHO_ART_TERMS = new HashMap<>();

		// COSTART
		this.COSTART_ATOMS = new HashMap<>();
		this.COSTART_CUIS = new HashMap<>();
		this.COSTART_BIGRAM = new HashMap<>();
		this.COSTART_TERMS = new HashMap<>();
		
		
		// create logger instance
		this.log = new Logger();
	}

	public HashMap<String, Atom> getMedDRA() {
		if (this.MEDDRA_ATOMS.size() == 0)
			this.loadMedDRA();
		return this.MEDDRA_ATOMS;
	}

	/**
	 * Load the meddra code set from the MRCONSO file
	 */
	public void loadMedDRA() {
		try {
			// Load Meddra from MRCONSO
			String infile = DATA_PATH + MRCONSO;

			// See if we have a gzip'd file or not
			InputStream fileInputStream = null;
			if (infile.toLowerCase().endsWith("gz")) {
				System.out.println("Decompressing archive file on the fly...");
				fileInputStream = new GZIPInputStream(new FileInputStream(infile));
			} else {
				fileInputStream = new FileInputStream(infile);
			}

			final Reader reader = new InputStreamReader(fileInputStream);
			CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter('|').withQuote(null));
			for (CSVRecord record : parser) {
				// Source
				String sourceName = record.get(11);
				if (sourceName.equals("MDR")) {

					String cui = record.get(0);
					String aui = record.get(7);
					String tty = record.get(12);
					String code = record.get(13);
					String term = record.get(14);

					Atom atom = new Atom();
					atom.setAui(aui);
					atom.setCui(cui);
					atom.setCode(code);
					atom.setTerm(term);
					atom.setTty(tty);
					this.MEDDRA_ATOMS.put(aui, atom);

					// cache CUI lookups
					if (this.MEDDRA_CUIS.containsKey(cui) == false)
						this.MEDDRA_CUIS.put(cui, new HashMap<String, Boolean>());

					// link AUI set to the CUI
					this.MEDDRA_CUIS.get(cui).put(aui, true);

					// term search support
					String lcTerm = term.toLowerCase().trim();
					if (this.MEDDRA_TERMS.containsKey(lcTerm) == false)
						this.MEDDRA_TERMS.put(lcTerm, new ArrayList<String>());

					this.MEDDRA_TERMS.get(lcTerm).add(aui);
				}
			}

			// close input stream
			reader.close();
			parser.close();

		} catch (Exception e) {
			System.err.println("Error loading Meddra: " + e.toString());
		}
	}

	public List<Atom> getMeddraSOC(Atom atom) {
		HashMap<String, Atom> atoms = new HashMap<>();
		for (Atom a : this.getMeddraParents(atom))
			if (a.getTty().equals("OS"))
				atoms.put(a.getAui(), a);
		return atoms.values().stream().collect(Collectors.toList());
	}

	public String getMeddraSocString(Atom atom) {

		if (this.MEDDRA_AUI_SOC.containsKey(atom.getAui()))
			return this.MEDDRA_AUI_SOC.get(atom.getAui());
		else {
			String result = "";
			StringBuilder sb = new StringBuilder();
			for (Atom hlt : this.getMeddraHLT(atom)) {
				List<Atom> hlgts = this.getMeddraHLGT(atom);
				for (Atom hlgt : hlgts) {
					List<Atom> socs = this.getMeddraSOC(hlgt);
					for (Atom soc : socs) {
						sb.append(soc.getTerm());
						sb.append(";");
					}
				}
			}
			String r = sb.toString();
			result = r.substring(0, r.length() - 1);
			this.MEDDRA_AUI_SOC.put(atom.getAui(), result);
			return result;
		}
	}

	public String getMeddraHlgtString(Atom atom) {

		if (this.MEDDRA_AUI_HLGT.containsKey(atom.getAui()))
			return this.MEDDRA_AUI_HLGT.get(atom.getAui());
		else {
			String result = "";
			StringBuilder sb = new StringBuilder();
			for (Atom hlt : this.getMeddraHLT(atom)) {
				List<Atom> hlgts = this.getMeddraHLGT(atom);
				for (Atom hlgt : hlgts) {
					sb.append(hlgt.getTerm());
					sb.append(";");
				}
			}
			String r = sb.toString();
			result = r.substring(0, r.length() - 1);
			this.MEDDRA_AUI_HLGT.put(atom.getAui(), result);
			return result;
		}
	}

	public String getMeddraHltString(Atom atom) {

		if (this.MEDDRA_AUI_HLT.containsKey(atom.getAui()))
			return this.MEDDRA_AUI_HLT.get(atom.getAui());
		else {
			String result = "";

			List<Atom> hlts = this.getMeddraHLT(atom);
			if (hlts.size() == 1)
				result = hlts.get(0).getTerm();
			else {
				StringBuilder sb = new StringBuilder();
				for (Atom hlt : this.getMeddraHLT(atom)) {
					sb.append(hlt.getTerm());
					sb.append(";");
				}
				String r = sb.toString();
				result = r.substring(0, r.length() - 1);
			}
			this.MEDDRA_AUI_HLT.put(atom.getAui(), result);
			return result;
		}
	}

	public List<Atom> getMeddraHLT(Atom atom) {
		HashMap<String, Atom> atoms = new HashMap<>();
		for (Atom a : this.getMeddraParents(atom))
			if (a.getTty().equals("HT"))
				atoms.put(a.getAui(), a);
		return atoms.values().stream().collect(Collectors.toList());
	}

	public List<Atom> getMeddraHLGT(Atom atom) {
		HashMap<String, Atom> atoms = new HashMap<>();
		for (Atom a : this.getMeddraParents(atom))
			if (a.getTty().equals("HG"))
				atoms.put(a.getAui(), a);
		return atoms.values().stream().collect(Collectors.toList());
	}

	public List<Atom> getDirectMeddraParents(Atom atom) {
		List<Atom> atoms = new ArrayList<>();
		try {

			if (this.MEDDRA_ATOMS.size() == 0)
				this.loadMedDRA();

			if (atom != null) {

				List<String> allPaths = this.getMedDRAHierarchyPaths(atom);
				if (allPaths != null) {

					for (String path : allPaths) {
						// single path
						String[] hier = path.split("\\.");
						String parentAui = hier[hier.length - 1];

						if (this.MEDDRA_ATOMS.containsKey(parentAui) == true) {
							atoms.add(this.MEDDRA_ATOMS.get(parentAui));
						}
					}
				}
			}

		} catch (Exception e) {
			log.log("Error getting meddra parents: " + e.toString());
		}
		return atoms;
	}

	public List<Atom> getMeddraParents(Atom atom) {
		try {

			if (this.MEDDRA_ATOMS.size() == 0)
				this.loadMedDRA();

			if (atom != null) {
				List<Atom> atoms = new ArrayList<>();
				List<String> allPaths = this.getMedDRAHierarchyPaths(atom);
				if (allPaths != null) {
					for (String path : allPaths) {
						// single path
						String[] hier = allPaths.get(0).split("\\.");
						for (String aui : hier) {
							if (this.MEDDRA_ATOMS.containsKey(aui) == true) {
								Atom parent = this.MEDDRA_ATOMS.get(aui);
								atoms.add(parent);
							} else {
								if (!aui.equals(MDR_ROOT))
									log.log("Missing MedDRA AUI: " + aui);
							}
						}
					}
				}

				return atoms;

			}
		} catch (Exception e) {
			log.log("Error getting meddra parents: " + e.toString());
		}
		return null;
	}

	public List<String> getMedDRAHierarchyPaths(Atom atom) {

		// Cache all AUI paths
		if (this.MEDDRA_AUI_PATHS.size() == 0) {

			try {
				log.log("Loading MedDRA hierarchy...");

				// Load Meddra from MRCONSO
				String infile = DATA_PATH + MRHIER;

				// See if we have a gzip'd file or not
				InputStream fileInputStream = null;
				if (infile.toLowerCase().endsWith("gz")) {
					System.out.println("Decompressing archive file on the fly...");
					fileInputStream = new GZIPInputStream(new FileInputStream(infile));
				} else {
					fileInputStream = new FileInputStream(infile);
				}

				// Loading hierarchy
				final Reader reader = new InputStreamReader(fileInputStream);
				CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter('|').withQuote(null));
				for (CSVRecord record : parser) {

					String aui = record.get(1); // child AUI
					String parent_aui = record.get(3);
					if (parent_aui.length() > 0) {
						if (this.MEDDRA_HIER.containsKey(parent_aui) == false) {
							this.MEDDRA_HIER.put(parent_aui, new HashMap<String, Boolean>());
						}
						this.MEDDRA_HIER.get(parent_aui).put(aui, true);
					}

					List<String> paths = new ArrayList<>();
					if (this.MEDDRA_AUI_PATHS.containsKey(aui) == true)
						paths = this.MEDDRA_AUI_PATHS.get(aui);

					String path = record.get(6); // PTR record
					paths.add(path);
					this.MEDDRA_AUI_PATHS.put(aui, paths);
				}

				// close input stream
				reader.close();
				parser.close();

			} catch (Exception e) {
				log.log("Error loading hierarchy paths: " + e.toString());
			}

		}

		// perform the lookup
		if (MEDDRA_AUI_PATHS.containsKey(atom.getAui()))
			return MEDDRA_AUI_PATHS.get(atom.getAui());

		return null;
	}

	/**
	 * Simple exact term search mapping Use hierarchy to get terms in preferred
	 * order
	 * 
	 * @param search
	 * @return
	 */
	public Atom searchForMeddraAtom(String search) {

		Atom match = null;
		String lcSearch = search.toLowerCase().trim();
		if (this.MEDDRA_TERMS.containsKey(lcSearch) == true) {

			List<String> matches = this.MEDDRA_TERMS.get(lcSearch);
			if (matches.size() == 1) {
				// return only atom
				match = this.MEDDRA_ATOMS.get(matches.get(0));
			} else if (matches.size() > 1) {

				// prefer PT if we have it
				for (String aui : matches) {
					Atom atom = this.MEDDRA_ATOMS.get(aui);
					if (atom != null)
						if (atom.getTty().contentEquals("PT"))
							match = atom;
				}

				// LLT next
				if (match == null) {
					for (String aui : matches) {
						Atom atom = this.MEDDRA_ATOMS.get(aui);
						if (atom != null)
							if (atom.getTty().contentEquals("LLT"))
								match = atom;
					}
				}

				// HLT next
				if (match == null) {
					for (String aui : matches) {
						Atom atom = this.MEDDRA_ATOMS.get(aui);
						if (atom != null)
							if (atom.getTty().contentEquals("HT"))
								match = atom;
					}
				}

				// HLGT next
				if (match == null) {
					for (String aui : matches) {
						Atom atom = this.MEDDRA_ATOMS.get(aui);
						if (atom != null)
							if (atom.getTty().contentEquals("HG"))
								match = atom;
					}
				}

				// SOC final match point
				if (match == null) {
					for (String aui : matches) {
						Atom atom = this.MEDDRA_ATOMS.get(aui);
						if (atom != null)
							if (atom.getTty().contentEquals("OS"))
								match = atom;
					}
				}
			}
		}
		return match;
	}

	/**
	 * Returns the Atom of the closest matching MeSH term
	 * 
	 * @param entry
	 * @param mshTerms
	 * @param THRESHOLD
	 * @return
	 */
	public Atom findClosestMatchingMeddraAtom(String term, double THRESHOLD) {

		term = term.toLowerCase().trim();

		// check that MedDRA is loaded
		if (this.MEDDRA_ATOMS.size() == 0)
			this.loadMedDRA();

		// First check for exact match
		Atom exactMatch = searchForMeddraAtom(term);
		if (exactMatch != null)
			return exactMatch;

		setupMeddraBigrams();

		// find closest match to this term
		double max_score = 0.0;
		Atom bestMatch = null;
		if (term.length() > 2) {
			BiGramSequence b1 = new BiGramSequence(term);
			for (String aui : this.MEDDRA_BIGRAM.keySet()) {
				BiGramSequence b2 = this.MEDDRA_BIGRAM.get(aui);
				double score = b1.compare(b2);
				if (score > max_score) {
					max_score = score;
					bestMatch = this.MEDDRA_ATOMS.get(aui);
				}
			}

			if (max_score >= THRESHOLD) {
				return bestMatch;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public void setupMeddraBigrams() {
		// compute all bigrams just once
		if (this.MEDDRA_BIGRAM.size() == 0) {
			log.log("Computing all bigrams...");
			for (String aui : this.MEDDRA_ATOMS.keySet()) {
				Atom ma = this.MEDDRA_ATOMS.get(aui);
				if (ma.getTerm().length() > 2)
					this.MEDDRA_BIGRAM.put(aui, new BiGramSequence(ma.getTerm()));
			}
			log.log("Bigrams computed: " + this.MEDDRA_BIGRAM.size());
		}
	}

	public HashMap<String, HashMap<String, Boolean>> getMedDRAHierarchy() {
		if (this.MEDDRA_AUI_PATHS.size() == 0) {
			Atom test = new Atom();
			// force load
			getMedDRAHierarchyPaths(test);
		}
		return this.MEDDRA_HIER;
	}

}
