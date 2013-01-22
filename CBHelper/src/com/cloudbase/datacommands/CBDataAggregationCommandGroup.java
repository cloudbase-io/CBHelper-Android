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
 * The group aggregation command. This works exaclty in the same way a GROUP BY
 * command would work in SQL.
 * The outputField array contains the number of fields for the output to be
 * "grouped by".
 * There's a number of operators to apply to the grouped field defined as
 * CBDataAggregationGroupOperator
 */
public class CBDataAggregationCommandGroup extends CBDataAggregationCommand {

	protected List<String> idFields;
	protected Map<String, Map<String, String>> groupFields;
	
	public CBDataAggregationCommandGroup() {
		this.idFields = new ArrayList<String>();
		this.groupFields = new HashMap<String, Map<String, String>>();
		
		this.setCommandType(CBDataAggregationCommandType.CBDataAggregationGroup);
	}
	
	/**
	 * Adds a field to the list of fields the output should be
	 * grouped by
	 * @param An NSString with the name of the field
	 */
	public void addOutputField(String fieldName) {
		this.idFields.add("$" + fieldName);
	}
	
	/**
	 * Adds a calculated field to the output of this group clause using the value of another field
	 * @param outputFieldName The name of the output field
	 * @param operator The operator to apply to the selected variable field
	 * @param fieldName The name of the variable field to be used with the operator
	 */
	public void addGroupFormulaForField(String outputFieldName, CBDataAggregationGroupOperator operator, String fieldName) {
		this.addGroupFormulaForValue(outputFieldName, operator, "$" + fieldName);
	}
	/**
	 * Adds a calculated field to the output of this group clause using a static value
	 * @param outputFieldName The name of the output field
	 * @param operator The operator to apply to the selected variable field
	 * @param value A value to be used with the operator
	 */
	public void addGroupFormulaForValue(String outputFieldName, CBDataAggregationGroupOperator operator, String value) {
		HashMap<String, String> newOperator = new HashMap<String, String>();
		newOperator.put(operator.toString(), value);
		this.groupFields.put(outputFieldName, newOperator);
	}
	
	@Override
	public Object serializeAggregateConditions() {
		Map<String, Object> finalSet = new HashMap<String, Object>();
		
		if (this.idFields.size() > 1) {
			finalSet.put("_id", this.idFields);
		} else {
			finalSet.put("_id", this.idFields.get(0));
		}
		
		finalSet.putAll(this.groupFields);
		
		return finalSet;
	}

}
