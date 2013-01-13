package com.cloudbase.cbhelperdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class PayPal extends Activity implements OnClickListener   {

	private String payPalUrl;
	
	public PayPal() {
		
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paypal);
		
		Intent intent = getIntent();
		this.payPalUrl = intent.getStringExtra("url");

		((Button)this.findViewById(R.id.closeButton)).setOnClickListener(this);
		
		WebView web = (WebView)this.findViewById(R.id.webView);
		web.getSettings().setJavaScriptEnabled(true);
		
		PayPalWebViewClient client = new PayPalWebViewClient();
		client.setCaller(this);
		
		web.setWebViewClient(client);
		
		web.loadUrl(this.payPalUrl);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.closeButton) {
			this.finish();
		}
    }
}
