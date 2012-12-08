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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cloudbase.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SettingsScreen extends Fragment implements OnClickListener {

	private static String appCode_ = "";
	private static String appUniq_ = "";
	private static String appPwd_ = "";
	private static String c2dmEmail_ = "";
	
	private static SharedPreferences settings;
	private static SharedPreferences.Editor editor;
	
	public SettingsScreen() {
		
	}
	
	private static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.settings, container, false);
		
		return fragmentView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		((Button)this.getView().findViewById(R.id.saveButton)).setOnClickListener(this);
		
		settings = this.getActivity().getSharedPreferences("cb_helper_demo", Context.MODE_PRIVATE);
		// if we have the app_code value in the settings then try and load the settings and 
		// initialise the CBHelper object
		if (!settings.getString("app_code", "").equals("")) {
			appCode_ = settings.getString("app_code", "");
			appUniq_ = settings.getString("app_uniq", "");
			appPwd_ = settings.getString("app_pwd", "");
			c2dmEmail_ = settings.getString("c2dm_email", "");
			
			TextView appText = (TextView)this.getView().findViewById(R.id.appCodeText);
			appText.setText(appCode_);
			TextView appUniqText = (TextView)this.getView().findViewById(R.id.appUniqText);
			appUniqText.setText(appUniq_);
			TextView c2dmText = (TextView)this.getView().findViewById(R.id.c2dmText);
			c2dmText.setText(c2dmEmail_);
			
			this.initHelper(false);
		}
	}
	
	@Override
	// save the settings to the SharedPreferences and initialise the helper
	public void onClick(View v) {
		Log.d("DEMOAPP", "onClick"); 
    	editor = settings.edit();
    	
    	TextView appText = (TextView)this.getView().findViewById(R.id.appCodeText);
		TextView appUniqText = (TextView)this.getView().findViewById(R.id.appUniqText);
		TextView pwdText = (TextView)this.getView().findViewById(R.id.appPwdText);
		TextView c2dmText = (TextView)this.getView().findViewById(R.id.c2dmText);
		
		appCode_ = appText.getText().toString();
		appUniq_ = appUniqText.getText().toString();
		appPwd_ = md5(pwdText.getText().toString());
		c2dmEmail_ = c2dmText.getText().toString();
		
		editor.putString("app_code", appCode_);
		editor.putString("app_uniq", appUniq_);
		editor.putString("app_pwd", appPwd_);
		editor.putString("c2dm_email", c2dmEmail_);
		editor.commit(); 
		
		this.initHelper(true);
    } 
	
	private void initHelper(boolean reInit) {
		if (((MainActivity)this.getActivity()).helper == null || reInit) {
			((MainActivity)this.getActivity()).helper = new CBHelper(appCode_, appUniq_, this.getActivity());
			((MainActivity)this.getActivity()).helper.setPassword(appPwd_);
			Log.d("DEMOAPP", "initialised helper...");
		}
		// if an email address for the C2DM service is set then try and register for push
		// notifications from the main Activity
		if (c2dmEmail_ != null && !c2dmEmail_.equals("")) {
			((MainActivity)this.getActivity()).registerForNotifications(c2dmEmail_);
		}
	}
}
