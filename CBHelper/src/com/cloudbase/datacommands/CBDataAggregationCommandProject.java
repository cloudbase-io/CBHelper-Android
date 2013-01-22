/* Copyright (C) 2012 cloudbase.io
 
 This program is free software; you can redistribute it and/or modify it under
 the terms of the GNU General Public License, version 2, as published by
 the Free Software Foundation.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; see the file COPYING.  If not, write to the Free
 Software Foundation, 59 Temple Place - Suite 330, Boston, MA
 02111-1307, USA.
 */
package com.cloudbase.datacommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The project aggregation command filters the number of fields selected
 * from a document.
 * You can either populate the <strong>includeFields</strong> property
 * to exclude all fields and only include the ones selected or use
 * the <strong>excludeFields</strong> to set up an exclusion list.
 */
public class CBDataAggregationCommandProject extends CBDataAggregationCommand {

	private List<String> includeFields;
	private List<String> excludeFields;
	
	public CBDataAggregationCommandProject() {
		this.includeFields = new ArrayList<String>();
		this.excludeFields = new ArrayList<String>();
		
		this.setCommandType(CBDataAggregationCommandType.CBDataAggregationProject);
	}
	
	@Override
	public Object serializeAggregateConditions() {
		Map<String, Integer> fieldList = new HashMap<String, Integer>();
		
		for (String field : this.getIncludeFields()) {
			fieldList.put(field, Integer.valueOf(1));
		}
		
		for (String field : this.getExcludeFields()) {
			fieldList.put(field, Integer.valueOf(0));
		}
		
		return fieldList;
	}

	public List<String> getIncludeFields() {
		return includeFields;
	}

	public void setIncludeFields(List<String> includeFields) {
		this.includeFields = includeFields;
	}

	public List<String> getExcludeFields() {
		return excludeFields;
	}

	public void setExcludeFields(List<String> excludeFields) {
		this.excludeFields = excludeFields;
	}

}
