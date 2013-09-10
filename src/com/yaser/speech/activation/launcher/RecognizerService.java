package com.yaser.speech.activation.launcher;

import android.content.Intent;
import android.speech.RecognitionService;
import android.util.Log;
import android.widget.Toast;

public class RecognizerService extends RecognitionService {
	private static final String TAG = "MyService";

	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onCancel(Callback listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onStartListening(Intent recognizerIntent, Callback listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onStopListening(Callback listener) {
		// TODO Auto-generated method stub
		
	}

}