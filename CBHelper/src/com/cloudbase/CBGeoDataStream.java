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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import android.location.Location;
import android.util.Log;

/**
 * Opens a persistent connection to a particular geo-coded connection on a
 * cloudbase.io Cloud Database and retrieves geo located data for the application.
 *
 * Data is handed back to the application using the protocol.
 *
 * This is meant to be used for augment reality applications.
 */
public class CBGeoDataStream implements CBHelperResponder {
	
	protected static final long queryInterval = 		2000; // 2 seconds
	protected static final long refreshRadiusRatio = 	4;
	
	protected CBHelper helper;
	protected Location previousPosition;
	protected double previousSpeed;
	protected double queryRadius;
	protected Map<String, CBGeoLocatedObject> foundObjects;
	protected String streamName;
	private Timer queryTimer;
	
	/**
	 * The object implementing the CBGeoDataStreamDelegate interface
	 */
	private CBGeoDataStreamResponder responder;
	/**
	 * The radius for the next search in meters from the point returned by the 
	 * getLatestPosition method
	 */
	private double searchRadius;
	/**
	 * The collection on which to run the search
	 */
	private String collection;
	
	/**
	 * Initializes a new CBGeoDataStream object and uses the given CBHelper
	 * object to retrieve data from the cloudbase.io APIS.
	 *
	 * @param name A unique identifier for this stream object. It's always passed to the responder
	 * @param helper An initialized CBHelper object
	 * @param collection The name of the collection to search
	 */
	public CBGeoDataStream(String name, CBHelper helper, String collection) {
		this.helper = helper;
		this.streamName = name;
		//helper.
		this.setCollection(collection);
		this.previousSpeed = 0.0;
		this.queryRadius = 50; // by default we use 50 meters
		
		this.foundObjects = new HashMap<String, CBGeoLocatedObject>();
	}
	
	/**
	 * Begins querying the cloudbase.io APIs and returning data periodically.
	 * 
	 * @param resp The responder object to receive data
	 */
	public void startStream(CBGeoDataStreamResponder resp) {
		this.responder = resp;
		this.queryTimer = new Timer();
		CBGeoDataStreamTask task = new CBGeoDataStreamTask(this);
		
		this.queryTimer.scheduleAtFixedRate(task, CBGeoDataStream.queryInterval, CBGeoDataStream.queryInterval);
	}
	
	/**
	 * Stops the data stream
	 */
	public void stopStream() {
		this.queryTimer.cancel();
		this.queryTimer.purge();
	}
	
	public CBGeoDataStreamResponder getResponder() {
		return responder;
	}
	public void setResponder(CBGeoDataStreamResponder responder) {
		this.responder = responder;
	}
	
	public double getSearchRadius() {
		return searchRadius;
	}

	public void setSearchRadius(double searchRadius) {
		this.searchRadius = searchRadius;
		this.queryRadius = searchRadius;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void handleResponse(CBQueuedRequest req, CBHelperResponse res) {
		if (res.isSuccess()) {
			
			List<Map<String, Object>> output = (List<Map<String, Object>>) res.getData();
			
			if (output.size() > 0) {
				for (Map<String, Object> curItem : output) {
					CBGeoLocatedObject obj = new CBGeoLocatedObject();
					Location loc = new Location("cloudbase.io");
					Map<String, Double> locationData = (Map<String, Double>) curItem.get("cb_location");
					loc.setLatitude(Double.valueOf(locationData.get("lat")));
					loc.setLongitude(Double.valueOf(locationData.get("lng")));
					if (curItem.containsKey("cb_location_altitude")) {
						loc.setAltitude(Double.valueOf((Double) curItem.get("cb_location_altitude")));
						obj.setAltitude(Double.valueOf((Double) curItem.get("cb_location_altitude")));
					}
					obj.setCoordinate(loc);
					
					
					curItem.remove("cb_location");
					curItem.remove("cb_location_altitude");
					
					obj.setObjectData(curItem);
					
					this.foundObjects.put(String.valueOf(obj.hash()), obj);
					this.responder.receivedPoint(this.streamName, obj);
				}
			}
		} else {
			if (this.helper.isDebugMode()) {
				Log.d(CBHelper.logTag, "Error while calling the cloudbase.io APIs");
			}
		}
		
		List<String> itemsToRemove = new ArrayList<String>();
		
		Iterator<String> it = this.foundObjects.keySet().iterator();
		
		while (it.hasNext()) {
			String key = it.next();
			CBGeoLocatedObject curObj = this.foundObjects.get(key);
			if (curObj.getCoordinate().distanceTo(this.previousPosition) > this.searchRadius) {
				this.responder.removingPoint(this.streamName, curObj);
				itemsToRemove.add(key);
			}
		}
		
		for (String curKey : itemsToRemove) {
			this.foundObjects.remove(curKey);
		}
	}

}
