package com.cloudbase.cbhelperdemo;

import com.cloudbase.CBLogLevel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class LogScreen extends Fragment implements OnClickListener  {
	
	public LogScreen() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.log, container, false);
		
		return fragmentView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// we have only one button so we implement the onclick in this object without
		// declaring a new one
		((Button)this.getView().findViewById(R.id.logButton)).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		TextView logLineText = (TextView)this.getView().findViewById(R.id.logLineText);
		TextView logCategory = (TextView)this.getView().findViewById(R.id.logCategoryText);
		Spinner logSeveritySpinner = (Spinner)this.getView().findViewById(R.id.logSeveritySpinner);
		
		String logSeverityString = logSeveritySpinner.getSelectedItem().toString();
		
		if (logLineText == null || logLineText.getText() == null) {
			Log.d("DEMOAPP", "Log line text is null");
			return;
		}
		if (logCategory == null || logCategory.getText() == null) {
			Log.d("DEMOAPP", "Log category is null");
			return;
		}
		// call the log APIs from the shared CBHelper in the MainActivity
		((MainActivity)this.getActivity()).helper.log(
				logLineText.getText().toString(), 
				CBLogLevel.valueOf(logSeverityString), 
				logCategory.getText().toString());
    } 
}
