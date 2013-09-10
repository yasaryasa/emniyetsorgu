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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.yaser.service.RetreiveJsonTask;
import com.yaser.service.model.Person;
import com.yaser.speech.core.SpeechRecognitionUtil;
import com.yaser.speech.core.SpeechRecognizingActivity;
import com.yaser.speech.text.match.StemmedWordMatcher;
import com.yaser.speech.tts.TextToSpeechInitializer;
import com.yaser.speech.tts.TextToSpeechStartupListener;
import com.yaser.thesis.R;

/**
 * A simple app to demonstrate a barebones usecase for playing a simple text to
 * speech sound
 * 
 * @author Greg Milette &#60;<a
 *         href="mailto:gregorym@gmail.com">gregorym@gmail.com</a>&#62;
 */
public class SayMagicWordDemo extends SpeechRecognizingActivity implements
		TextToSpeechStartupListener {
	private static final String TAG = "SayMagicWordDemo";
	private Button speak;
	private TextToSpeechInitializer ttsInit;
	private TextToSpeech tts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.magicworddemo);
		hookButtons();
		init();
	}

	private void hookButtons() {
		speak = (Button) findViewById(R.id.btn_speak);
		speak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO acquireGuess metodundan once, menu i�in tts kullan�lmal�
				// menuden sonra ise kullan�c�n�n inputu beklenir vs. dongu gibi
				// gider bu
				acquireGuess();
			}
		});
	}

	private void init() {
		deactivateUi();
		ttsInit = new TextToSpeechInitializer(this, Locale.getDefault(), this);
	}

	@Override
	public void onSuccessfulInit(TextToSpeech tts) {
		Log.d(TAG, "successful init");
		this.tts = tts;
		activateUi();
		setTtsListener();
	}

	/**
	 * set the TTS listener to call {@link #onDone(String)} depending on the
	 * Build.Version
	 */
	private void setTtsListener() {
		final SayMagicWordDemo callWithResult = this;
		if (Build.VERSION.SDK_INT >= 15) {
			int listenerResult = tts
					.setOnUtteranceProgressListener(new UtteranceProgressListener() {
						@Override
						public void onDone(String utteranceId) {
							callWithResult.onDone(utteranceId);
						}

						@Override
						public void onError(String utteranceId) {
							Log.e(TAG, "TTS error");
						}

						@Override
						public void onStart(String utteranceId) {
							Log.d(TAG, "TTS start");
						}
					});
			if (listenerResult != TextToSpeech.SUCCESS) {
				Log.e(TAG, "failed to add utterance progress listener");
			}
		} else {
			int listenerResult = tts
					.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
						@Override
						public void onUtteranceCompleted(String utteranceId) {
							callWithResult.onDone(utteranceId);
						}
					});
			if (listenerResult != TextToSpeech.SUCCESS) {
				Log.e(TAG, "failed to add utterance completed listener");
			}
		}
	}

	public void onDone(String utteranceId) {
		Log.d(TAG, "utterance completed: " + utteranceId);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				activateUi();
			}
		});
	}

	@Override
	public void onFailedToInit() {
		DialogInterface.OnClickListener onClickOk = makeOnFailedToInitHandler();
		AlertDialog a = new AlertDialog.Builder(this).setTitle("Error")
				.setMessage("Unable to create text to speech")
				.setNeutralButton("Ok", onClickOk).create();
		a.show();
	}

	@Override
	public void onRequireLanguageData() {
		DialogInterface.OnClickListener onClickOk = makeOnClickInstallDialogListener();
		DialogInterface.OnClickListener onClickCancel = makeOnFailedToInitHandler();
		AlertDialog a = new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage(
						"Requires Language data to proceed,"
								+ " would you like to install?")
				.setPositiveButton("Ok", onClickOk)
				.setNegativeButton("Cancel", onClickCancel).create();
		a.show();
	}

	@Override
	public void onWaitingForLanguageData() {
		// either wait for install
		DialogInterface.OnClickListener onClickWait = makeOnFailedToInitHandler();
		DialogInterface.OnClickListener onClickInstall = makeOnClickInstallDialogListener();

		AlertDialog a = new AlertDialog.Builder(this)
				.setTitle("Info")
				.setMessage(
						"Please wait for the language data "
								+ "to finish installing and try again.")
				.setNegativeButton("Wait", onClickWait)
				.setPositiveButton("Retry", onClickInstall).create();
		a.show();
	}

	private DialogInterface.OnClickListener makeOnClickInstallDialogListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ttsInit.installLanguageData();
			}
		};
	}

	private DialogInterface.OnClickListener makeOnFailedToInitHandler() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		};
	}

	private void acquireGuess() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"What is the magic word?");

		recognize(intent);
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
//		String magicWord = "ara�";
//		String mostLikelyThingHeard = heard.get(0);
//		String message = "";

		boolean found = false;
		StemmedWordMatcher command = new StemmedWordMatcher("kimlik","plaka");
		for (String said : heard) {
			if (command.isIn(said.split("\\s"))) {
				 found = true;
				break;
			}
		}
		
//		 RetreiveJsonTask json = new RetreiveJsonTask(this);
//		 json.execute();

		// //TODO burada menu soylendikten sonra kullan�c�n�n soyledikleri
		// //kars�last�r�l�p menu olup olmad�g� kontrol edilecek
		// if (mostLikelyThingHeard.equals(magicWord))
		// {
		// message =
		// "Do�ru: Sihirli kelimeyi bildiniz " + mostLikelyThingHeard;
		// }
		// else
		// {
		// message = "Yanl��: Sihirli kelime " + mostLikelyThingHeard + "de�il";
		// }
		if (!found) {
			AlertDialog a =
					new AlertDialog.Builder(this).setTitle("Sonu�lar")
					.setMessage(heard.get(0)).setPositiveButton("Tamam ", null)
					.create();
			a.show();
			
			deactivateUi();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "anyid");
			tts.speak(heard.get(0), TextToSpeech.QUEUE_ADD, params);
		} else {
			HashMap<String, String> params = new HashMap<String, String>();
			 params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "anyid");
			 tts.speak("Komut bulundu", TextToSpeech.QUEUE_ADD, params);
		}
	}

	public void readPerson(Person p) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "anyid");

		deactivateUi();
		tts.speak("Ki�i TC Kimlik No", TextToSpeech.QUEUE_ADD, params);
		tts.speak(p.getTckno(), TextToSpeech.QUEUE_ADD, params);
		tts.speak("Ki�i ad�", TextToSpeech.QUEUE_ADD, params);
		tts.speak(p.getName(), TextToSpeech.QUEUE_ADD, params);
		tts.speak("Ki�i �ifresi", TextToSpeech.QUEUE_ADD, params);
		tts.speak(p.getSurname(), TextToSpeech.QUEUE_ADD, params);
	}

	protected void recognitionFailure(int errorCode) {
		AlertDialog a = new AlertDialog.Builder(this).setTitle("Hata ")
				.setMessage(SpeechRecognitionUtil.diagnoseErrorCode(errorCode))
				.setPositiveButton("Nabal�m :(", null).create();
		a.show();
	}

	// activate and deactivate the UI based on various states

	private void deactivateUi() {
		Log.d(TAG, "deactivate ui");
		// don't enable until the initialization is complete
		speak.setEnabled(false);
	}

	private void activateUi() {
		Log.d(TAG, "activate ui");
		speak.setEnabled(true);
	}

	@Override
	protected void onDestroy() {
		tts.shutdown();
		super.onDestroy();
	}

}