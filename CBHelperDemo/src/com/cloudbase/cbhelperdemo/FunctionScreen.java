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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cloudbase.CBHelperResponder;
import com.cloudbase.CBHelperResponse;
import com.cloudbase.CBPayPalBill;
import com.cloudbase.CBPayPalBillItem;

import android.app.AlertDialog;
import android.content.Intent;
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
		((Button)this.getView().findViewById(R.id.callPayPalButton)).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		TextView functionText = (TextView)this.getView().findViewById(R.id.functionCodeText);
		
		if (v.getId() == R.id.callFunctionButton) {
			// execute the cloudfunction
			MainActivity.helper.runCloudFunction(functionText.getText().toString(), null, this);
		}
		
		if (v.getId() == R.id.callAppletButton) {
			// run the standard twitter search applet. This Fragment is also the responder and will display
			// a list of the results in an AlertDialog
			String appletCode = "cb_twitter_search";
			Map<String, String> params = new HashMap<String, String>();
			params.put("search", "#bgee");
			
			MainActivity.helper.runApplet(appletCode, params, this);
		}
		
		if (v.getId() == R.id.callPayPalButton) {
			CBPayPalBillItem billItem = new CBPayPalBillItem();
			billItem.setName("test paypal paymnet");
			billItem.setDescription("this is a test paypal bill for $9.99");
			billItem.setAmount(9.99);
			billItem.setTax(0);
			billItem.setQuantity(1);
			
			CBPayPalBill bill = new CBPayPalBill();
			bill.setName("test paypal bill");
			bill.setDescription("test papal bill for $9.99");
			bill.setCurrency("USD");
			bill.setInvoiceNumber("test-invoice-01");
			bill.setPaymentCancelledFunction("");
			bill.setPaymentCompletedFunction("");
			bill.addNewItem(billItem);
			
			MainActivity.helper.preparePayPalPurchase(bill, false, this);
		}
    }
	
	@Override
	public void handleResponse(CBHelperResponse res) {
		if (res.getFunction().equals("applet")) { // we have called the twitter applet. parse and print the Alert
			if (res.getData() instanceof Map) {
				@SuppressWarnings("unchecked") 
				// this function returns a map of tweets:
				// { 
				//	{"1" : { "tweetdata" : "datavlaue" .. },
				//	{"2" : { "tweetdata" : "datavalue" ...}
				// }
				Iterator<String> tweetsIt = ((Map<String, String>)res.getData()).keySet().iterator();
				String output = "";
				int cnt = 0;
				
				while (tweetsIt.hasNext()) {
					String curKey = (String)tweetsIt.next();
					@SuppressWarnings("unchecked")
					Map<String, String> tweet = (Map<String, String>)((Map<String, Object>)res.getData()).get(curKey);
					
					// read the values we need for each tweet
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
		} else if (res.getFunction().equals("cloudfunction") ){
			// if we just called a cloudfunction then simply log the output
			Log.d("DEMOAPP", "CloudFunction output: " + res.getResponseDataString());
		} else {
			// paypal
			if (res.getData() instanceof Map) {
				@SuppressWarnings("rawtypes")
				String url = (String)((Map)res.getData()).get("checkout_url");
				
				Intent intent = new Intent(this.getActivity(), PayPal.class);
				intent.putExtra("url", url);
				this.getActivity().startActivity(intent);
			}
		}
	}
}
