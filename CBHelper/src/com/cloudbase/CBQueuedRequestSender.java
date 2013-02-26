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

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import android.os.Handler;
import android.util.Log;

/**
 * This object is used internally to send the request queued on disk
 * asynchronously. 
 *
 */
public class CBQueuedRequestSender implements Runnable {

	private ArrayList<String> requests;
	private CBHelper helper;
	
	/**
	 * Creates a new instance of the request sender.
	 * @param req A list of String containing the path to a queued request file
	 * @param helperObject An initialized CBHelper object to use for settings and callbacks
	 */
	public CBQueuedRequestSender(ArrayList<String> req, CBHelper helperObject) {
		this.requests = req;
		this.helper = helperObject;
	}
	
	/**
	 * Starts sending the requests. This method sends request synchronously therefore it should
	 * always be called from inside a thread to avoid blocking the application
	 */
	@Override
	public void run() {
		for (String curRequest : this.requests) {
			try {
				FileInputStream fis = new FileInputStream(curRequest);
				ObjectInputStream is = new ObjectInputStream(fis);
				CBQueuedRequest requestObject = (CBQueuedRequest) is.readObject();
				is.close();
				
				CBHelperRequest req = new CBHelperRequest(requestObject, this.helper);
				if (this.helper.getDefaultQueueResponder() != null) {
					req.setResponder(this.helper.getDefaultQueueResponder());
					Handler handler = new Handler();
					req.setmHandler(handler);
				}
				
				req.setQueueFileName(curRequest);
				
				req.setTemporaryFilePath(this.helper.getTemporaryFilesPath());
				
				req.run();
				
				if (this.helper.isDebugMode()) {
					Log.i(CBHelper.logTag, "Sent queued request: " + curRequest);
				}
			
			} catch (Exception e) {
				Log.e(CBHelper.logTag, "Error while opening queued request " + curRequest, e);
			}
		}
		
		this.helper.removeQueueLock();
	}

}
