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

import java.util.Map;

import android.location.Location;

/**
 * Represents an object returned by a CBGeoDataStream with its 
 * coordinates, altitude and additional information stored in the 
 * cloud database collection
 */
public class CBGeoLocatedObject {
	/**
	 * The coordinate position of the object
	 */
	private Location coordinate;
	public Location getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(Location coordinate) {
		this.coordinate = coordinate;
	}
	/**
	 * All of the other data stored in the cloud database for the 
	 * document
	 */
	private Map<String, Object> objectData;
	public Map<String, Object> getObjectData() {
		return objectData;
	}
	public void setObjectData(Map<String, Object> objectData) {
		this.objectData = objectData;
	}
	/**
	 * The altitude of the object if the cb_location_altitude field
	 * exists in the document
	 */
	private double altitude;
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	/**
	 * Generates a unique code identifying this object
	 * @return an int unique to this object
	 */
	public int hash() {
		int prime = 31;
	    int result = 1;
	    
	    result = prime * result + this.coordinate.hashCode();
	    result = prime * result + (int)this.altitude;
	    result = prime * result + this.objectData.hashCode();
	    
	    return result;

	}
	
}
