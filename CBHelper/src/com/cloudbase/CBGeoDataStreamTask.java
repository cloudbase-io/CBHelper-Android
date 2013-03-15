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

import java.util.TimerTask;

import com.cloudbase.datacommands.CBSearchCondition;

import android.location.Location;
import android.util.Log;

/**
 * A task fetching the data from cloudbase.io called by the CBGeoDataStream
 * object using a Timer
 */
public class CBGeoDataStreamTask extends TimerTask {

	private CBGeoDataStream streamObject;
	
	/**
	 * Creates a new instance of the TimerTask receiving the
	 * CBGeoDataStream object running the timer
	 * 
	 * @param father The CBGeoDataStream object running the task 
	 */
	public CBGeoDataStreamTask(CBGeoDataStream father) {
		this.setStreamObject(father);
	}
	
	@Override
	public void run() {
		//Looper.prepare();
		this.streamObject.helper.getApplicationActivity().runOnUiThread(new Runnable(){

			@Override
			public void run() {
				Location currentLocation = streamObject.getResponder().getLatestPosition(streamObject.streamName);
				
				if (streamObject.previousPosition != null) {
					double distance = currentLocation.distanceTo(streamObject.previousPosition);
					
					if (distance < streamObject.queryRadius / CBGeoDataStream.refreshRadiusRatio) {
						if (isDebugMode()) {
							Log.d(CBHelper.logTag, "Not enough distance between the two points. returning without fetching data");
						}
						return;
					}
					
					double speed = distance / CBGeoDataStream.queryInterval;
					double ratio = 1.0;
					
					if (isDebugMode()) {
						Log.d(CBHelper.logTag, "Computed speed " + speed + " meters per second");
					}
					
					if (streamObject.previousSpeed != 0.0) {
						ratio = speed / streamObject.previousSpeed;
					}
					
					if (streamObject.queryRadius * ratio < streamObject.getSearchRadius()) {
						streamObject.queryRadius = streamObject.getSearchRadius();
					} else {
						streamObject.queryRadius = streamObject.queryRadius * ratio;
					}
					
					streamObject.previousSpeed = speed;
				}
				streamObject.previousPosition = currentLocation;
				
				CBSearchCondition condition = new CBSearchCondition(currentLocation, streamObject.queryRadius);
				
				streamObject.helper.searchDocument(streamObject.getCollection(), condition, streamObject);
				
			}
			
		});
	}
	
	private boolean isDebugMode() {
		return this.streamObject.helper.isDebugMode();
	}

	public CBGeoDataStream getStreamObject() {
		return streamObject;
	}

	public void setStreamObject(CBGeoDataStream streamObject) {
		this.streamObject = streamObject;
	}
}
