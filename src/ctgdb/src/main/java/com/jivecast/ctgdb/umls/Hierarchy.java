package com.jivecast.ctgdb.umls;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

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
//  All rights reserved.
//

@Data
public class Hierarchy {

	
	Atom root;
	String source;
	HashMap<Integer, List<Integer>> cui2aui;
	HashMap<Integer, Atom> entries;
	
	public Hierarchy(String src)
	{
		this.root    = null;
		this.source  = src;
		this.entries = new HashMap<Integer, Atom>();
		this.cui2aui    = new HashMap<Integer, List<Integer>>();
	}
}
