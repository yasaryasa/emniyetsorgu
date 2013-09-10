package com.yaser.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.yaser.MultiTurnDemo;
import com.yaser.service.model.Plaka;
import com.yaser.speech.voiceaction.VoiceActionExecutor;
import com.yaser.speech.voiceaction.commands.GrammerMenu;

public class RetreivePlakaGrammerJsonTask extends AsyncTask<String, Void, Plaka> {

	private GrammerMenu sorguContext;

	public RetreivePlakaGrammerJsonTask() {
	}

	public RetreivePlakaGrammerJsonTask(GrammerMenu plakaSorgu) {
		this.sorguContext = plakaSorgu;
	}

	@Override
	protected Plaka doInBackground(String... params) {
		getExecutor().speakQueue("Sorgulama yapýlýyor. Lütfen bekleyin.");
		
		Plaka plaka= null;
		
		HttpClient httpclient = new DefaultHttpClient();
		// HttpGet httpget = new HttpGet(
		// "http://trandroid.com/wp-content/dosyalar/androidjsonparsesample.php");
		HttpGet httpget = new HttpGet("http://thesis.muzikfon.com/?plaka="
				+ params[0]);
		HttpResponse response;

		String message = null;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {

				try {
					InputStream instream = entity.getContent();
					String line = "";
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(instream));
					StringBuilder json = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						json.append(line + "\n");
					}

					JSONObject obj = new JSONObject(json.toString());
					
					plaka = new Plaka();
					plaka.setPlaka(obj.getString("plaka"));
					plaka.setOwner_name(obj.getString("owner_name"));
					plaka.setOwner_surname(obj.getString("owner_surname"));

					instream.close();
					return plaka;
				} catch (Exception e) {
					getExecutor()
							.speakQueue(
									"Sorgulama tamamlandý. Bu plaka numarasýna ait kayýt bulunamadý.");
					message = e.toString();
					
				}
			}

		} catch (Exception e) {
			message = e.toString();
			getExecutor().speakQueue(
					"Plaka sorgulanýrken hata oluþtu. Lütfen tekrar deneyin.");
		}
		return plaka;
	}

	@Override
	protected void onPostExecute(Plaka result) {
		// AlertDialog a =
		// new AlertDialog.Builder(null)
		// .setTitle("Hata ")
		// .setMessage("Sonuç : "+result)
		// .setPositiveButton("Nabalým :(", null).create();
		// a.show();
		if (result != null && !result.getPlaka().isEmpty())  {
			speakPlaka(result);
		} else {
			try {
				Thread.sleep(9000);
			} catch (InterruptedException e1) {
			}
			MultiTurnDemo.speechActivator.detectActivation();
		}
	}

	private void speakPlaka(Plaka result) {
		getExecutor().speakQueue("Sorgulama tamamlandý.");
		getExecutor().speakQueue("Araç sahibi adý " + result.getOwner_name());
		getExecutor().speakQueue("Araç sahibi soyadý " + result.getOwner_surname());
		
		try {
			Thread.sleep(9000);
		} catch (InterruptedException e) {
		}
        MultiTurnDemo.speechActivator.detectActivation();
//
//		final VoiceAlertDialog confirmDialog = new VoiceAlertDialog();
//		
//		// add listener for positive response
//		// use relaxed matching to increase chance of understanding user
//		confirmDialog.addRelaxedPositive(new OnUnderstoodListener() {
//			@Override
//			public void understood() {
//				speakPerson(person);
//			}
//		});
//
//		confirmDialog.addRelaxedNegative(new OnUnderstoodListener() {
//			@Override
//			public void understood() {
//				getExecutor().speak("Ýþlem tamamlandý.");
//			}
//		});
//
//		// if the user says anything else besides the yes words cancel
//		confirmDialog.setNotUnderstood(new OnNotUnderstoodListener() {
//			@Override
//			public void notUnderstood(List<String> heard, int reason) {
//				String toSayCancelled = sorguContext.getContext()
//						.getResources()
//						.getString(R.string.voiceaction_cancelled_response);
//				getExecutor().speak(toSayCancelled);
//			}
//		});
//
//		String prompt = "Sonucu tekrar dinlemek ister misiniz?";
//		confirmDialog.setPrompt(prompt);
//		confirmDialog.setSpokenPrompt(prompt);
//		getExecutor().execute(confirmDialog);
	}

	private VoiceActionExecutor getExecutor() {
		return sorguContext.getExecutor();
	}
}
