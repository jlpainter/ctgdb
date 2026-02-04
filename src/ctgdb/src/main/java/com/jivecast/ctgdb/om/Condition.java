package com.jivecast.ctgdb.om;

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
 * @author Jeffery Painter <jeff@jivecast.com>
 * @created: 2021-Oct-28
 *
 */
@Data
public class Condition {

	int refId;
	String term;
	
	// umls meta-data
	int meddraRefId;
	boolean exactMatch;
	
	/**
	 * Initialize everything to empty strings
	 */
	public Condition() {
		this.refId = -1;
		this.term = "";
		this.meddraRefId = -1;
		this.exactMatch = false;
	}
}
