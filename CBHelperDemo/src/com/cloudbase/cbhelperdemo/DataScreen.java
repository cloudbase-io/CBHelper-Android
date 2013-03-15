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

import com.cloudbase.CBHelperResponder;
import com.cloudbase.CBHelperResponse;
import com.cloudbase.CBQueuedRequest;
import com.cloudbase.datacommands.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataScreen extends Fragment implements OnClickListener, CBHelperResponder  {
	
	public DataScreen() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.data, container, false);
		
		return fragmentView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		((Button)this.getView().findViewById(R.id.insertButton)).setOnClickListener(this);
		((Button)this.getView().findViewById(R.id.insertFileButton)).setOnClickListener(this);
		((Button)this.getView().findViewById(R.id.searchButton)).setOnClickListener(this);
		((Button)this.getView().findViewById(R.id.downloadButton)).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.insertButton) {
			TestDataObject obj = this.createObject();
			// use the shared object and send an insert to the basic data object
			MainActivity.helper.insertDocument(obj, "android_demo_collection", true);
		}
		
		if (v.getId() == R.id.insertFileButton) {
			// create a photopicker to attach a file. the insert completes in the onActivityResult
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, 1);
		}
		
		if (v.getId() == R.id.searchButton) {
			// run a search over the test collection for all the objects with firstName of "cloud". Should be all of them.
			// this Fragment is also the responder
			CBSearchCondition cond = new CBSearchCondition("firstName", CBSearchConditionOperator.CBOperatorEqual, "cloud");
			cond.addSortField("firstName", -1);
			cond.setLimit(1);
			
			/*
			List<CBDataAggregationCommand> aggregationCommands = new ArrayList<CBDataAggregationCommand>();
			
			CBDataAggregationCommandProject projectCommand = new CBDataAggregationCommandProject();
			projectCommand.getIncludeFields().add("Symbol");
			projectCommand.getIncludeFields().add("Price");
			projectCommand.getIncludeFields().add("total");
			aggregationCommands.add(projectCommand);
			
			CBDataAggregationCommandGroup groupCommand = new CBDataAggregationCommandGroup();
			groupCommand.addOutputField("Symbol");
			groupCommand.addGroupFormulaForField("total", CBDataAggregationGroupOperator.CBDataAggregationGroupSum, "Price");
			aggregationCommands.add(groupCommand);
			*/
			MainActivity.helper.searchDocument("android_demo_collection", cond, this);
			//MainActivity.helper.searchDocumentAggregate("security_master_3", aggregationCommands, this);
			
		}
		
		if (v.getId() == R.id.downloadButton) {
			TextView fileIdText = (TextView)this.getView().findViewById(R.id.fileIdText);
			String fileId = fileIdText.getText().toString();
			
			// download file and show the image in a popup view - This Fragment is also the responder
			MainActivity.helper.downloadFile(fileId, this);
		}
    }
	
	 @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (resultCode != Activity.RESULT_OK) return;
	 
		 if (requestCode == 1) {
			 Uri mImageCaptureUri = data.getData();
			 
			 ArrayList<File> attachments = new ArrayList<File>();
			 attachments.add(new File(getRealPathFromURI(mImageCaptureUri)));
			 
			 TestDataObject obj = this.createObject();
			 // we have the file now run the insert
			 MainActivity.helper.insertDocument(obj, "android_demo_collection", attachments, null);
		 }
	 }
	 
	 private String getRealPathFromURI(Uri contentURI) {
		 Cursor cursor = this.getActivity().getContentResolver()
		               .query(contentURI, null, null, null, null); 
		 cursor.moveToFirst(); 
		 int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
		 return cursor.getString(idx); 
	 }
	 
	 private TestDataObject createObject() {
		TestDataObject obj = new TestDataObject();
		obj.setFirstName("cloud");
		obj.setLastName("base");
		obj.setTitle(".io");
		return obj;
	 }

	
	@SuppressWarnings("unchecked")
	@Override
	public void handleResponse(CBQueuedRequest req, CBHelperResponse res) {
		// we are downloading a file
		if (res.getFunction().equals("download")) {
			if (res.getDownloadedFile() != null) {
				try {
					// resize the downloaded image and display it in an ImageView
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(res.getDownloadedFile().getAbsolutePath(), options);
					int imageHeight = options.outHeight;
					int imageWidth = options.outWidth;
					
					int reqWidth = 200;
					int reqHeight = 200;
					int inSampleSize = 1;

					if (imageHeight > reqHeight || imageWidth > reqWidth) {
						if (imageWidth > imageHeight) {
							inSampleSize = Math.round((float)imageHeight / (float)reqHeight);
						} else {
							inSampleSize = Math.round((float)imageWidth / (float)reqWidth);
						}
					}
					options.inSampleSize = inSampleSize;
					
					options.inJustDecodeBounds = false;
					Bitmap myBitmap = BitmapFactory.decodeFile(res.getDownloadedFile().getAbsolutePath(), options); //BitmapFactory.decodeStream(new FileInputStream(res.getDownloadedFile()), options);
					
					ImageView imageView = new ImageView(this.getActivity());
			        imageView.setImageBitmap(myBitmap);
					
					imageView.setOnClickListener(new OnClickListener() {
						@Override
			            public void onClick(View v) {
							((ViewGroup)v.getParent()).removeView(v);
			            }
					});
			        //setting image position
					LayoutParams par = new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
			        imageView.setLayoutParams(par);
					this.getActivity().addContentView(imageView, par);
			        
				} catch (Exception ex) {
					Log.e("DEMOAPP", "Error while opening downloaded file", ex);
				}
			}
		} else {
			if (res.getData() instanceof List) {
				// if we are not downloading and just running the search then read the array of result and
				// print the size.
				Log.d("DEMOAPP", "Is is array");
				AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
				builder.setTitle("Received response");
				builder.setMessage("total items: " + ((List<Object>)res.getData()).size());
				builder.setPositiveButton("OK", null);
				builder.show();
			} else {
				Log.d("DEMOAPP", "Data not array: " + res.getData().getClass().toString());
			}
		}
		
	}
}
