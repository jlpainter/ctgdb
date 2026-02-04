package com.jivecast.ctgdb.om;

import java.util.ArrayList;
import java.util.List;

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
 * Interventions are drugs/treatments in a particular study being investigated
 * 
 * @author Jeffery Painter <jeff@jivecast.com>
 * @created: 2021-Oct-28
 *
 */
@Data
public class Intervention {

	int refId;
	String iType;
	String name;
	List<String> otherNames;

	public Intervention() {
		this.refId = -1;
		this.iType = "";
		this.otherNames = new ArrayList<>();
	}
}
