package com.cloudbase.cbhelperdemo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
		
		if (v.getId() == R.id.subscribeButton) {
			((MainActivity)this.getActivity()).helper.notificationSubscribeDevice(regId, channelText.getText().toString());
		}
		
		if (v.getId() == R.id.unsubscribeButton) {
			((MainActivity)this.getActivity()).helper.notificationUnsubscribeDevice(regId, channelText.getText().toString());
		}
		
		if (v.getId() == R.id.pushNotifButton) {
			((MainActivity)this.getActivity()).helper.sendNotification(notifText.getText().toString(), channelText.getText().toString());
		}
    }
}
