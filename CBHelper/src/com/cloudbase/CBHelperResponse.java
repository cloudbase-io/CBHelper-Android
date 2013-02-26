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

import java.io.File;

/**
 * The object representing a parsed response from the cloudbase.io APIs. This object will contain all of the data
 * returned and will be passed to the <strong>CBHelperResponder</strong> object once a call is completed.
 * @author Stefano Buliani
 *
 */
public class CBHelperResponse {
	private String errorMessage;
	private String function;
	private String responseDataString;
	private File downloadedFile;
	private Object data;
	private boolean success;
	private int httpStatus;
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public File getDownloadedFile() {
		return downloadedFile;
	}
	public void setDownloadedFile(File downloadedFile) {
		this.downloadedFile = downloadedFile;
	}
	
	/**
	 * Whether the API call was successful. If an error occurred then the full error message is stored in the <strong>errorMessage</strong>
	 * field
	 * @return Whether the call was successful
	 */
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	/**
	 * The String representation of the response. All of the raw data (unparsed) returned from cloudbase.io is stored in this
	 * variable
	 * @return The string response
	 */
	public String getResponseDataString() {
		return responseDataString;
	}
	public void setResponseDataString(String responseDataString) {
		this.responseDataString = responseDataString;
	}
	public int getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
}
