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

/**
 * This abstract class should be implemented by any class to send 
 * Data Aggregation commands to cloudbase.io
 *
 * The serializeAggregateConditions should resturn a Map
 * exactly in the format needed by the CBHelper class to be added
 * to the list of parmeters, serliazed and sent to cloudbase.io
 */
public abstract class CBDataAggregationCommand {

	private CBDataAggregationCommandType commandType; 

	public CBDataAggregationCommandType getCommandType() {
		return commandType;
	}

	public void setCommandType(CBDataAggregationCommandType commandType) {
		this.commandType = commandType;
	}
	
	/**
	 * Serializes the Command object to its JSON representation
	 *
	 * @return A NSDictionary representation of the Command object. This
	 *  method should be implemented in each subclass
	 */
	public abstract Object serializeAggregateConditions();

}
