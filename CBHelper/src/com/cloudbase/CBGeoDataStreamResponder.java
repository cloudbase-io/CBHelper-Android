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

import android.location.Location;

/**
 * Objects implementing this class will interact with a CBGeoDataStream
 * object and will instruct it how to download the data as well as receive the 
 * data stream
 */

public interface CBGeoDataStreamResponder {
	/**
	 * Returns the latest known position to the CBGeoDataStream object.
	 * This is used to retrieve the data and compute the movement speed to
	 * increase or decrease the speed of refresh
	 *
	 * @param streamName the unique identifier of the stream asking for the value
	 * @return A valid Location object
	 */
	Location getLatestPosition(String streamName);
	
	/**
	 * receives a new point to be visualized
	 *
	 * @param streamName the unique identifier of the stream passing the value
	 * @param CBGeoLocatedObject An object representing a new point on the map
	 */
	void receivedPoint(String streamName, CBGeoLocatedObject point);
	
	/**
	 * Informs the application that the CBGeoDataStream is removing a point from its cache
	 *
	 * @param streamName the unique identifier of the stream removing the point
	 * @param CBGeoLocatedObject The point being removed
	 */
	void removingPoint(String streamName, CBGeoLocatedObject point);
}
