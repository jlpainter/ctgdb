package com.jivecast.ctgdb.umls;

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
 * UMLS atom is the unique identified term, code, tty and source object
 * AUIs are unique, cui's are concept identifiers for synonomy among AUIs
 * 
 * @author Jeffery Painter <jeff@jivecast.com>
 * @created: 2021-Oct-28
 *
 */

@Data
public class Atom {

	int refId;
	String aui;
	String cui;
	String term;
	String code;
	String tty;
	String src;
	
	public Atom() {
		this.refId = -1;
		this.aui = "";
		this.cui = "";
		this.term = "";
		this.code = "";
		this.tty = "";
		this.src = "";
	}
	
	/**
	 * Return CSV format
	 * 
	 * @param DELIMITER
	 * @return
	 */
	public String toCSV(String DELIMITER) {
		
		StringBuilder csv = new StringBuilder();
		csv.append(this.getRefId());
		csv.append(DELIMITER);

		csv.append(Util.cleanAndQuoteContent(this.getAui()));
		csv.append(DELIMITER);

		csv.append(Util.cleanAndQuoteContent(this.getCui()));
		csv.append(DELIMITER);

		csv.append(Util.cleanAndQuoteContent(this.getTty()));
		csv.append(DELIMITER);

		csv.append(Util.cleanAndQuoteContent(this.getCode()));
		csv.append(DELIMITER);

		csv.append(Util.cleanAndQuoteContent(this.getTerm()));

		return csv.toString();
	}
	
}
