package com.cloudbase.cbhelperdemo;

import com.google.android.gcm.GCMBaseIntentService;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GCMIntentService  extends GCMBaseIntentService {
	@Override
	protected void onRegistered(Context context, String regId) {
		Log.d("CloudBase", "Registered: " + regId);
		MainActivity.gcmRegistrationId = regId;
	}
	
	@Override
	protected void onUnregistered(Context context, String regId) {
		Log.d("CloudBase", "Unregistered: " + regId);
	}
	
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.d("CloudBase", "Message!!");
	}
	
	@Override
	protected void onError(Context context, String errorId) {
		Log.d("CloudBase", "Error: " + errorId);
	}
	
	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.d("CloudBase", "Recoverable Error: " + errorId);
		return false;
	}
	
}
