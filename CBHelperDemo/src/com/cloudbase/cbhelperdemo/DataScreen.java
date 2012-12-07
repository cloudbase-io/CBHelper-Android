package com.cloudbase.cbhelperdemo;

import com.cloudbase.CBHelperResponder;
import com.cloudbase.CBHelperResponse;
import com.cloudbase.CBSearchCondition;
import com.cloudbase.CBSearchConditionOperator;

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
			Log.d("DEMOAPP", "Pressed insert button");
			TestDataObject obj = this.createObject();
			
			((MainActivity)this.getActivity()).helper.insertDocument(obj, "android_demo_collection");
		}
		
		if (v.getId() == R.id.insertFileButton) {
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, 1);
		}
		
		if (v.getId() == R.id.searchButton) {
			CBSearchCondition cond = new CBSearchCondition("firstName", CBSearchConditionOperator.CBOperatorEqual, "cloud");
			
			((MainActivity)this.getActivity()).helper.searchDocument("android_demo_collection", cond, this);
		}
		
		if (v.getId() == R.id.downloadButton) {
			TextView fileIdText = (TextView)this.getView().findViewById(R.id.fileIdText);
			String fileId = fileIdText.getText().toString();
			
			((MainActivity)this.getActivity()).helper.downloadFile(fileId, this);
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
			 
			 ((MainActivity)this.getActivity()).helper.insertDocument(obj, "android_demo_collection", attachments, null);
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
	public void handleResponse(CBHelperResponse res) {
		if (res.getFunction().equals("download")) {
			if (res.getDownloadedFile() != null) {
				try {
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
