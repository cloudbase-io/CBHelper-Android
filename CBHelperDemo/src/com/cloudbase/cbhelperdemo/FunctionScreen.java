package com.cloudbase.cbhelperdemo;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cloudbase.CBHelperResponder;
import com.cloudbase.CBHelperResponse;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FunctionScreen extends Fragment implements OnClickListener, CBHelperResponder  {
	
	public FunctionScreen() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.functions, container, false);
		
		return fragmentView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		((Button)this.getView().findViewById(R.id.callFunctionButton)).setOnClickListener(this);
		((Button)this.getView().findViewById(R.id.callAppletButton)).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		TextView functionText = (TextView)this.getView().findViewById(R.id.functionCodeText);
		
		if (v.getId() == R.id.callFunctionButton) {
			((MainActivity)this.getActivity()).helper.runCloudFunction(functionText.getText().toString(), null, this);
		}
		
		if (v.getId() == R.id.callAppletButton) {
			String appletCode = "cb_twitter_search";
			Map<String, String> params = new HashMap<String, String>();
			params.put("search", "#bgee");
			
			((MainActivity)this.getActivity()).helper.runApplet(appletCode, params, this);
		}
    }
	
	@Override
	public void handleResponse(CBHelperResponse res) {
		if (res.getFunction().equals("applet")) {
			if (res.getData() instanceof Map) {
				@SuppressWarnings("unchecked")
				Iterator<String> tweetsIt = ((Map<String, String>)res.getData()).keySet().iterator();
				String output = "";
				int cnt = 0;
				
				while (tweetsIt.hasNext()) {
					String curKey = (String)tweetsIt.next();
					@SuppressWarnings("unchecked")
					Map<String, String> tweet = (Map<String, String>)((Map<String, Object>)res.getData()).get(curKey);
					
					output += "On " + tweet.get("created_at").substring(0, 17) + " " + 
							tweet.get("from_user_name") + " said " +
							tweet.get("text") + "\n\n";
					
					cnt++;
					if (cnt == 5)
						break;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
				builder.setTitle("Twitter search output");
				builder.setMessage(output);
				builder.setPositiveButton("OK", null);
				builder.show();
			}
		} else {
			Log.d("DEMOAPP", "CloudFunction output: " + res.getResponseDataString());
		}
	}
}
