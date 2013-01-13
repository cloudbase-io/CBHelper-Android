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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.Handler;
import android.util.Log;
import android.webkit.MimeTypeMap;

/**
 * A Runnable object used by the CBHelper class to execute HTTP calls asynchronously. This should not be called
 * directly.
 * @author Stefano Buliani
 *
 */
public class CBHelperRequest implements Runnable {

	private String url;
	private String function;
	private String fileId;
	private Hashtable<String, String> postData;
	private ArrayList<File> files;
	
	private String temporaryFilePath;
	
	private CBHelperResponder responder;
	private CBHelperResponse resp;
	private Handler mHandler;
	
	public CBHelperRequest(String url, String func) {
		this.url = url;
		this.function = func;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(this.url);

		try {
		    // Add your data
		    Enumeration<String> params = this.postData.keys();
		    
		    // prepare the request adding all of the parameters.
		    CBMultipartEntity entity = new CBMultipartEntity();
		    
		    while (params.hasMoreElements())
		    {
		    	String curKey = params.nextElement();
		    	entity.addPart(new CBStringPart(curKey, this.postData.get(curKey)));
		    }
		    
		    // if we have file attachments then add each file to the multipart request
		    if (this.files != null && this.files.size() > 0) {
		    	int fileCounter = 0;
			    for (File curFile : this.files) {
			    	String name = curFile.getName();
			    	int pos = name.lastIndexOf('.');
			    	String ext = name.substring(pos+1);
			    	entity.addPart(new CBFilePart("file" + fileCounter, curFile, null, 
			    				(pos > -1?MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext):null)));
			    	fileCounter++;
			    }
		    }
		    // add the multipart request to the http connection
		    httppost.setEntity(entity);

		    // Execute HTTP Post Request
		    HttpResponse response = httpclient.execute(httppost);
		    
		    // if we have a responder then parse the response data into the global CBHelperResponse object
		    if (this.responder != null) {
		    	try {
			    	resp = new CBHelperResponse();
			    	resp.setFunction(this.function);
			    	
			    	// if it's a download then we need to save the file content into a temporary file in
			    	// application cache folder. Then return that file to the responder
				    if (this.function.equals("download")) {
				    	InputStream input = response.getEntity().getContent();
				    	
				    	File outputFile = File.createTempFile(fileId, null, new File(this.getTemporaryFilePath()));
				    	OutputStream fos = new BufferedOutputStream(new FileOutputStream(outputFile));
				    	try {
					    	
					    	byte[] buffer = new byte[(int) 4096];
					    	int readBytes;
					    	while (((readBytes = input.read(buffer, 0, buffer.length)) != -1)) {
					    		fos.write(buffer, 0, readBytes);
					    	}
				    	} catch (IOException e) {
				    		e.printStackTrace();
				    	}
				    		
				    	if (fos != null) {
				    		try {
				    			fos.close();
				    		} catch (IOException e) {
				    		}
				    	}
	
				    	if (input != null) {
				    		try {
				    			input.close();
				    		} catch (IOException e) {
				    		}
				    	}
				    	resp.setDownloadedFile(outputFile);
				    } else {
				    	
				    	// if it's not a download parse the JSON response and set all 
				    	// the variables in the CBHelperResponse object
				    	String responseString = EntityUtils.toString(response.getEntity());
				    	
					    resp.setResponseDataString(responseString);
					    Log.d("test", resp.getResponseDataString());
					    // Use the cloudbase.io deserializer to get the data in a Map<String, Object>
					    // format.
					    GsonBuilder gsonBuilder = new GsonBuilder();
					    gsonBuilder.registerTypeAdapter(Object.class, new CBNaturalDeserializer());
					    Gson gson = gsonBuilder.create();
					    Map<String, Object> responseData = gson.fromJson(responseString,Map.class);
						Map<String, Object> outputData = (Map<String, Object>)responseData.get(this.function);
					    resp.setData(outputData.get("message"));
					    resp.setErrorMessage((String)outputData.get("error"));
					    resp.setSuccess(((String)outputData.get("status")).equals("OK"));
			    	}
				    
				    // now that the response object is ready use the Handler we have been handed from the
				    // CBHelper class on the main thread to call the responder object. This way the data
				    // is available to the UI thread
				    mHandler.post(new Runnable() {
			            @Override
			            public void run() {
			            	responder.handleResponse(resp);
			            }
			          });
		    	} catch (Exception e) { 
		    		Log.e("CBHelper", "Error sending request", e);
		    	}
		    }
		    
		} catch (ClientProtocolException e) {
		    Log.e("REQUEST", "Error " + e.getMessage(), e);
		} catch (IOException e) {
			Log.e("REQUEST", "Error " + e.getMessage(), e);
		}
	}
	public Hashtable<String, String> getPostData() {
		return postData;
	}
	public void setPostData(Hashtable<String, String> postData) {
		this.postData = postData;
	}
	public CBHelperResponder getResponder() {
		return responder;
	}
	public void setResponder(CBHelperResponder responder) {
		this.responder = responder;
	}
	public ArrayList<File> getFiles() {
		return files;
	}
	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public String getTemporaryFilePath() {
		return temporaryFilePath;
	}

	public void setTemporaryFilePath(String temporaryFilePath) {
		this.temporaryFilePath = temporaryFilePath;
	}
}
