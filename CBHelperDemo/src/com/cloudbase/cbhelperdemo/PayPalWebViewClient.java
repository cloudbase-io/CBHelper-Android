package com.cloudbase.cbhelperdemo;

import java.util.Map;

import com.cloudbase.CBHelperResponder;
import com.cloudbase.CBHelperResponse;
import com.cloudbase.CBQueuedRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;



public class PayPalWebViewClient extends WebViewClient implements CBHelperResponder, OnClickListener {

	private Activity caller;
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (url.indexOf("paypal/update-status") == -1) {
			view.loadUrl(url);
		} else {
			MainActivity.helper.completePayPalPurchase(url, this);
		}
		
		return false;
	}

	public Activity getCaller() {
		return caller;
	}

	public void setCaller(Activity caller) {
		this.caller = caller;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void handleResponse(CBQueuedRequest req, CBHelperResponse res) {
		if (res.getData() instanceof Map) {
			
			if (((Map)res.getData()).containsKey("amount")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(caller);
				builder.setTitle("Received response");
				builder.setMessage("payment details: " + res.getResponseDataString());
				builder.setPositiveButton("OK", this);
				builder.show();
				
			} else {
				String paymentId = (String)((Map)res.getData()).get("payment_id");
				Log.d("HelperDemo", "payment_id: " + paymentId + " completed");
				MainActivity.helper.getPayPalPaymentDetails(paymentId, this);
			}
		}
		
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		this.caller.finish();
	}
    
}
