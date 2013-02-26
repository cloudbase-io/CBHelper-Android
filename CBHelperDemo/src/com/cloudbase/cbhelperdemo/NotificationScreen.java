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
package com.cloudbase.cbhelperdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NotificationScreen extends Fragment implements OnClickListener  {
	
	public NotificationScreen() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.notifications, container, false);
		
		return fragmentView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		((Button)this.getView().findViewById(R.id.subscribeButton)).setOnClickListener(this);
		((Button)this.getView().findViewById(R.id.unsubscribeButton)).setOnClickListener(this);
		((Button)this.getView().findViewById(R.id.pushNotifButton)).setOnClickListener(this);	
	}
	
	@Override
	public void onClick(View v) {
		TextView channelText = (TextView)this.getView().findViewById(R.id.channelText);
		TextView notifText = (TextView)this.getView().findViewById(R.id.notifText);
		
		String regId = ((MainActivity)this.getActivity()).c2dmRegistrationId;
		if (regId != null && !regId.equals("")) { // if we have a registration id.
			if (v.getId() == R.id.subscribeButton) {
				// subscribe to a notification channel
				MainActivity.helper.notificationSubscribeDevice(regId, channelText.getText().toString());
			}
			
			if (v.getId() == R.id.unsubscribeButton) {
				// unsubscribe from a channel
				MainActivity.helper.notificationUnsubscribeDevice(regId, channelText.getText().toString());
			}
			
			if (v.getId() == R.id.pushNotifButton) {
				// send a push notification. This needs to be enabled in the security settings in the 
				// application's cloudbase.io control panel - allow client devices to send notifications
				MainActivity.helper.sendNotification(notifText.getText().toString(), channelText.getText().toString(), false);
			}
		} else {
			Log.d("DEMOAPP", "The application is not registered for C2DM notifications");
		}
    }
}
