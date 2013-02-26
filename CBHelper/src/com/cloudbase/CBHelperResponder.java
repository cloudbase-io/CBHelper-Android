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

/**
 * Objects created to handle the responses received from the cloudbase.io APIs should implement this interface.
 * The method handleResponse of the object is called whenever a response is received.<br/><br/>
 * A CBHelperResponse object with the parsed response information is passed
 * @author Stefano Buliani
 *
 */
public interface CBHelperResponder {
	/**
	 * Receive the response data as well as the original request data
	 * @param req A CBQueuedRequest object with all of the details of the call
	 * @param res A populated CBHelperResponse object
	 */
	void handleResponse(CBQueuedRequest req, CBHelperResponse res);
}
