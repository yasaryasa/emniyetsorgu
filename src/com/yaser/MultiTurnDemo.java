/*
 * Copyright 2011 Greg Milette and Adam Stroud
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *              http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yaser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.yaser.service.model.Person;
import com.yaser.speech.activation.SpeechActivationService;
import com.yaser.speech.activation.SpeechActivator;
import com.yaser.speech.core.SpeechRecognizingAndSpeakingActivity;
import com.yaser.speech.voiceaction.MultiCommandVoiceAction;
import com.yaser.speech.voiceaction.VoiceAction;
import com.yaser.speech.voiceaction.VoiceActionCommand;
import com.yaser.speech.voiceaction.VoiceActionExecutor;
import com.yaser.speech.voiceaction.WhyNotUnderstoodListener;
import com.yaser.speech.voiceaction.commands.CancelCommand;
import com.yaser.speech.voiceaction.commands.KimlikSorguMenu;
import com.yaser.speech.voiceaction.commands.PlakaSorguMenu;
import com.yaser.thesis.R;

public class MultiTurnDemo extends SpeechRecognizingAndSpeakingActivity {
	private Button kimlik;
	// private Button plaka;
	private TextToSpeech tts;

	public static VoiceActionExecutor executor;
	private static boolean past = false;

	private static final String TAG = "SpeechActivatorStartStop";
	/**
	 * store if currently listening
	 */
	public static boolean isListeningForActivation;
	/**
	 * if paused, store what was happening so that onResume can restart it
	 */
	private boolean wasListeningForActivation;
	public static SpeechActivator speechActivator;
	public static VoiceAction menuVoiceAction;
	public static VoiceAction grammerVoiceAction;

	private RadioGroup radioMethod;
	private RadioGroup radioAlgo;
	
	private Button yardim;

	private CheckBox chkWordSpotting, chkStemming;

	/**
	 * for saving {@link #wasListeningForActivation} in the saved instance state
	 */
	private static final String WAS_LISTENING_STATE = "WAS_LISTENING";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		setContentView(R.layout.multiturndemo);
		hookButtons();
		// init();
		initDialog();
		isListeningForActivation = false;
		// speechActivator = new WordActivator(this, this, "asistan");

		// final WallpaperManager wallpaperManager =
		// WallpaperManager.getInstance(this);
		// final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
		// getWindow().setBackgroundDrawable(wallpaperDrawable);
	}

	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	// setContentView(R.layout.multiturndemo2);
	// }

	private void initDialog() {
		if (executor == null) {
			executor = new VoiceActionExecutor(this);
		}
		// menuVoiceAction = makeStartMenu(this, executor);
	}

	public VoiceAction makeStartMenu(Context context,
			VoiceActionExecutor executor) {
		String giris = "Merhaba, emniyet sorguya hoþgeldiniz. Benimle birlikte,"
				+ " kimlik numarasý veya plaka ile sorgulama yapabilirsiniz.";

		String LOOKUP_PROMPT = giris
				+ "Kimlik sorgulama için Kimlik. Plaka sorgulamak için plaka komutlarýný kullanýn."
				+ " Çýkýþ yapmak için iptal demeniz yeterli.";

		// final String LOOKUP_PROMPT = "Sorgu baþladý.";
		VoiceActionCommand kimlikSorgu = new KimlikSorguMenu(context, executor);
		VoiceActionCommand plakaSorgu = new PlakaSorguMenu(context, executor);
		VoiceActionCommand cancel = new CancelCommand(context, executor);
		VoiceAction voiceAction = new MultiCommandVoiceAction(Arrays.asList(
				cancel, kimlikSorgu, plakaSorgu));
		voiceAction.setNotUnderstood(new WhyNotUnderstoodListener(context,
				executor, true));

		voiceAction.setPrompt(LOOKUP_PROMPT);
		voiceAction.setSpokenPrompt(LOOKUP_PROMPT);

		return voiceAction;
	}

	private void hookButtons() {
		kimlik = (Button) findViewById(R.id.btn_kimlik);
		
		yardim = (Button) findViewById(R.id.buttonYardim);

		radioMethod = (RadioGroup) findViewById(R.id.radioGroupMethod);
		
		chkWordSpotting = (CheckBox) findViewById(R.id.checkWordSpotting);
		chkStemming = (CheckBox) findViewById(R.id.checkBoxStemming);

		kimlik.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// acquireGuess();
				// if (past) {
				// String prompt =
				// "Kimlik numarasý ile mi yoksa plaka ile mi sorgu yapmak istersiniz?";
				// menuVoiceAction.setPrompt(prompt);
				// menuVoiceAction.setSpokenPrompt(prompt);
				// }
				// past = true;
				// executor.execute(menuVoiceAction);

				// startActivator();

				// SpeechActivationService.makeServiceStopIntent(MultiTurnDemo.this);

				int selectedMethodId = radioMethod.getCheckedRadioButtonId();

				if (selectedMethodId == R.id.radioGrammer) {
					Constants.useGrammer = true;
					Constants.useMenu = false;
				} else if (selectedMethodId == R.id.radioMenu) {
					Constants.useMenu = true;
					Constants.useGrammer = false;
				}


				if (speechActivator != null) {
					speechActivator.stop();
					speechActivator.detectActivation();
				} else {
					Intent i = SpeechActivationService.makeStartServiceIntent(
							MultiTurnDemo.this, "Speech");
					MultiTurnDemo.this.startService(i);
				}
			}
		});

		chkWordSpotting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					Constants.useWordSpotting = true;
				} else {
					Constants.useWordSpotting = false;
				}
			}
		});
		
		chkStemming.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					Constants.useStemming = true;
				} else {
					Constants.useStemming = false;
				}
			}
		});
		
		yardim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						YardimActivity.class);
				startActivityForResult(i, 100);
			}
		});

		// plaka = (Button) findViewById(R.id.btn_plaka);
		// plaka.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // acquireGuess();
		// }
		// });

	}

	@Override
	public void onSuccessfulInit(TextToSpeech tts) {
		super.onSuccessfulInit(tts);
		executor.setTts(getTts());
	}

	private DialogInterface.OnClickListener makeOnFailedToInitHandler() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		};
	}

	public void speechNotAvailable() {
		DialogInterface.OnClickListener onClickOk = makeOnFailedToInitHandler();
		AlertDialog a = new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage(
						"This device does not support "
								+ "speech recognition. Click ok to quit.")
				.setPositiveButton("Ok", onClickOk).create();
		a.show();
	}

	@Override
	protected void directSpeechNotAvailable() {
		// not using it
	}

	protected void languageCheckResult(String languageToUse) {
		// not used
	}

	/**
	 * determine if the user said the magic word and speak the result
	 */
	protected void receiveWhatWasHeard(List<String> heard,
			float[] confidenceScores) {

		executor.handleReceiveWhatWasHeard(heard, confidenceScores);
	}

	public void readPerson(Person p) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "anyid");

		deactivateUi();
		tts.speak("Kiþi TC Kimlik No", TextToSpeech.QUEUE_ADD, params);
		tts.speak(p.getTckno(), TextToSpeech.QUEUE_ADD, params);
		tts.speak("Kiþi adý", TextToSpeech.QUEUE_ADD, params);
		tts.speak(p.getName(), TextToSpeech.QUEUE_ADD, params);
		tts.speak("Kiþi þifresi", TextToSpeech.QUEUE_ADD, params);
		tts.speak(p.getSurname(), TextToSpeech.QUEUE_ADD, params);
	}

	protected void recognitionFailure(int errorCode) {
		// AlertDialog a = new AlertDialog.Builder(this).setTitle("Hata ")
		// .setMessage(SpeechRecognitionUtil.diagnoseErrorCode(errorCode))
		// .setPositiveButton("Nabalým :(", null).create();
		// a.show();
		// executor.speak("Kusura bakmayýn anlayamadým. Lütfen tekrar eder misiniz?");
		// executor.execute(kimlikVoiceAction);
	}

	@Override
	protected void onDestroy() {
		// tts.shutdown();
		// super.onDestroy();
	}

	private void startActivator() {
		if (isListeningForActivation) {
			Toast.makeText(this, "Not started: already started",
					Toast.LENGTH_SHORT).show();
			Log.d(TAG, "not started, already started");
			// only activate once
			return;
		}

		if (speechActivator != null) {
			isListeningForActivation = true;
			Toast.makeText(this, "Started movement activator",
					Toast.LENGTH_SHORT).show();
			Log.d(TAG, "started");
			speechActivator.detectActivation();
		}
	}

	private void stopActivator() {
		if (speechActivator != null) {
			Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "stopped");
			speechActivator.stop();
		}
		isListeningForActivation = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Log.d(TAG, "ON PAUSE stop");
		// // save before stopping
		// wasListeningForActivation = isListeningForActivation;
		// stopActivator();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Log.d(TAG, "ON RESUME was listening: " + wasListeningForActivation);
		// if (wasListeningForActivation)
		// {
		// startActivator();
		// }
	}

}