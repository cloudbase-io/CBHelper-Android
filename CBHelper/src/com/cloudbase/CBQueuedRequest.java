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

import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This object represents a cloudbase.io API request. It is used by the helper class
 * to serialize the data of the call to a file for request queueing.
 *
 */
public class CBQueuedRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The API url
	 */
	private String url;
	/**
	 * The cloudbase function to be called log, data etc
	 */
	private String cloudbaseFunction;
	/**
	 * The id of the file to be downloaded if the request was for a file 
	 * download
	 */
	private String fileId;
	/**
	 * All of the parameters for the HTTP post
	 */
	private Hashtable<String, String> parameters;
	/**
	 * The file attachments
	 */
	private ArrayList<File> files;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCloudbaseFunction() {
		return cloudbaseFunction;
	}
	public void setCloudbaseFunction(String cloudbaseFunction) {
		this.cloudbaseFunction = cloudbaseFunction;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public Hashtable<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Hashtable<String, String> parameters) {
		this.parameters = parameters;
	}
	public ArrayList<File> getFiles() {
		return files;
	}
	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

}
