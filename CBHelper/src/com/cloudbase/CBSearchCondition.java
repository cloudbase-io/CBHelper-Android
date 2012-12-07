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
package com.cloudbase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.location.Location;

/**
 * This object represents a set of conditions to run a search for a document in a cloudbase.io collection.<br/><br/>
 * It is the equivalent of a WHERE clause in old fashioned SQL. A number of search conditions can be concatenated together
 * using <strong>CBSearchConditionLink</strong> operators within a CBSearchCondition.
 * @author Stefano Buliani
 *
 */
public class CBSearchCondition {
	private List<CBSearchCondition> subConditions;
	private String field;
	private Object value;
	private CBSearchConditionOperator operator;
	private CBSearchConditionLink link;
	
	public static final String CBSearchKey = "cb_search_key";
	
	/**
	 * Creates an empty search condition object
	 */
	public CBSearchCondition() {
	}
	
	/**
	 * Creates a new "simple" search condition with the given values
	 * @param fname The name of the field to run the search on
	 * @param op The CBSearchConditionOperator to use in the search
	 * @param value The value we are looking for in the field
	 */
	public CBSearchCondition(String fname, CBSearchConditionOperator op, Object value) {
		this.setField(fname);
		this.setOperator(op);
		this.setValue(value);
	}
	
	/**
	 * Creates a new search condition for geographical searches. This looks for documents whose location data
	 * places them near the given location.
	 * @param nearLoc The location we are looking for
	 * @param maxDistance The maximum distance in meters from the given location
	 */
	public CBSearchCondition(Location nearLoc, int maxDistance) {
		List<Double> points = new ArrayList<Double>();
        points.add(Double.valueOf(nearLoc.getLatitude()));
        points.add(Double.valueOf(nearLoc.getLongitude()));
        
        Map<String, Object> searchQuery = new HashMap<String, Object>(); 
        this.setField("cb_location");
        this.setOperator(CBSearchConditionOperator.CBOperatorEqual);
        
        searchQuery.put("$near", points);
        if (maxDistance > 0)
        	searchQuery.put("$maxDistance", Integer.valueOf(maxDistance));

        this.setValue(searchQuery);
	}
	
	/**
	 * Creates a new search condition for geographical searches. This looks for documents within a given boundary box
	 * defined by the coordinates of its North-Eastern and South-Western corners.
	 * @param NECorner The coordinates for the north eastern corner
	 * @param SWCorner The coordinates for the south western corner
	 */
	@SuppressWarnings("rawtypes")
	public CBSearchCondition(Location NECorner, Location SWCorner)
	{
		List<ArrayList> box = new ArrayList<ArrayList>();
		ArrayList<Double> NECornerList = new ArrayList<Double>();
		NECornerList.add(Double.valueOf((NECorner.getLatitude())));
		NECornerList.add(Double.valueOf((NECorner.getLongitude())));
		ArrayList<Double> SWCornerList = new ArrayList<Double>();
		SWCornerList.add(Double.valueOf((SWCorner.getLatitude())));
		SWCornerList.add(Double.valueOf((SWCorner.getLongitude())));
		box.add(SWCornerList);
		box.add(NECornerList);
		
		Map<String, Object> boxCondition = new HashMap<String, Object>();
		boxCondition.put("$box", box);
		
		Map<String, Object> searchQuery = new HashMap<String, Object>();
		searchQuery.put("$within", boxCondition);
		
		this.setField("cb_location");
		this.setOperator(CBSearchConditionOperator.CBOperatorEqual);
		this.setValue(searchQuery);
	}
	
	public void addAnd(String field, CBSearchConditionOperator op, Object value)
	{
		if (this.getSubConditions() == null)
			this.setSubConditions(new ArrayList<CBSearchCondition>());
		
		CBSearchCondition newCond = new CBSearchCondition();
		newCond.setField(field);
		newCond.setOperator(op);
		newCond.setLink(CBSearchConditionLink.CBConditionLinkAnd);
		newCond.setValue(value);
		
		this.subConditions.add(newCond);
	}
	
	public void addOr(String field, CBSearchConditionOperator op, Object value)
	{
		if (this.getSubConditions() == null)
			this.setSubConditions(new ArrayList<CBSearchCondition>());
		
		CBSearchCondition newCond = new CBSearchCondition();
		newCond.setField(field);
		newCond.setOperator(op);
		newCond.setLink(CBSearchConditionLink.CBConditionLinkOr);
		newCond.setValue(value);
		
		this.subConditions.add(newCond);
	}
	
	public void addNor(String field, CBSearchConditionOperator op, Object value)
	{
		if (this.getSubConditions() == null)
			this.setSubConditions(new ArrayList<CBSearchCondition>());
		
		CBSearchCondition newCond = new CBSearchCondition();
		newCond.setField(field);
		newCond.setOperator(op);
		newCond.setLink(CBSearchConditionLink.CBConditionLinkNor);
		newCond.setValue(value);
		
		this.subConditions.add(newCond);
	}
	
	// returns the Map of conditions to be serialized to json and included 
	// in a request
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map serializeConditions() {
		Map conds = this.serializeConditions(this);
	    Map finalConditions = new HashMap();
	    
	    if (!conds.containsKey(CBSearchKey))
	    {
	        finalConditions.put(CBSearchKey, conds);
	    }
	    else
	        finalConditions = conds;
	    
	    return finalConditions;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map serializeConditions(CBSearchCondition conditionsGroup) {
		Map<Object, Object> output = new HashMap<Object, Object>();
	    
		// This is not a condition but a collection of sub-conditions. loop over them
		// and serialize them one by one
		if (conditionsGroup.getField() == null)
	    {
	        if (conditionsGroup.getSubConditions().size() > 1) {
	            ArrayList<Object> curObject = new ArrayList<Object>();
	            
	            CBSearchConditionLink prevLink = null; // used to store the link from the previous condition
	            int count = 0;
	            for (CBSearchCondition curGroup : conditionsGroup.getSubConditions())
	            {
	                if (prevLink != null && prevLink != curGroup.getLink()) {
	                	output.put(prevLink.toString(), curGroup);
	                    curObject = new ArrayList<Object>();
	                }
	                curObject.add(this.serializeConditions(curGroup));
	                prevLink = curGroup.getLink();
	                count++;
	                if (count == conditionsGroup.getSubConditions().size()) {
	                	output.put(prevLink.toString(), curGroup);
	                }
	            }
	        }
	        else if (conditionsGroup.getSubConditions().size() == 1)
	        {
	            output = this.serializeConditions(conditionsGroup.getSubConditions().get(0));
	        }
	    }
	    else // it's a single condition with a field. Generate the Map for it.s
	    {
	    	Map cond = new HashMap();
	    	List modArray = new ArrayList();
	        switch (conditionsGroup.getOperator()) {
	            case CBOperatorEqual:
	                output.put(conditionsGroup.getField(), conditionsGroup.value.toString());//[output setValue:conditionsGroup.value forKey:conditionsGroup.field];
	                break;
	            case CBOperatorAll:
	            case CBOperatorExists:
	            case CBOperatorNe:
	            case CBOperatorIn:
	            case CBOperatorNin:
	            case CBOperatorSize:
	            case CBOperatorType:
	            	cond.put(conditionsGroup.getOperator().toString(), conditionsGroup.getValue().toString());
	            	output.put(conditionsGroup.getField(), cond);
	                break;
	            case CBOperatorMod:
	            	modArray.add(conditionsGroup.getValue());
	            	modArray.add(Integer.valueOf(1));
	            	cond.put(conditionsGroup.getOperator().toString(), modArray);
	            	output.put(conditionsGroup.getField(), cond);
	            default:
	                break;
	        }
	    }
		
	    return output;
	}

	
	public List<CBSearchCondition> getSubConditions() {
		return subConditions;
	}
	public void setSubConditions(List<CBSearchCondition> subConditions) {
		this.subConditions = subConditions;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public CBSearchConditionOperator getOperator() {
		return operator;
	}
	public void setOperator(CBSearchConditionOperator operator) {
		this.operator = operator;
	}
	public CBSearchConditionLink getLink() {
		return link;
	}
	public void setLink(CBSearchConditionLink link) {
		this.link = link;
	}
}
