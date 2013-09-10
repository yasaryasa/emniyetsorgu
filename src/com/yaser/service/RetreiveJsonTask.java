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
import com.yaser.service.model.Person;
import com.yaser.speech.activation.SpeechActivationService;
import com.yaser.speech.voiceaction.VoiceActionExecutor;
import com.yaser.speech.voiceaction.commands.KimlikSorgu;

public class RetreiveJsonTask extends AsyncTask<String, Void, Person> {

	private KimlikSorgu sorguContext;

	public RetreiveJsonTask() {
	}

	public RetreiveJsonTask(KimlikSorgu kimlikSorgu) {
		this.sorguContext = kimlikSorgu;
	}

	@Override
	protected Person doInBackground(String... params) {
		getExecutor().speakQueue("Sorgulama yapýlýyor. Lütfen bekleyin.");
		Person person = null;
		HttpClient httpclient = new DefaultHttpClient();
		// HttpGet httpget = new HttpGet(
		// "http://trandroid.com/wp-content/dosyalar/androidjsonparsesample.php");
		HttpGet httpget = new HttpGet("http://thesis.muzikfon.com/?tckno="
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

					person = new Person();
					person.setTckno(obj.getString("tckno"));
					person.setName(obj.getString("name"));
					person.setSurname(obj.getString("surname"));

					instream.close();
					return person;
				} catch (Exception e) {
					getExecutor()
							.speakQueue(
									"Sorgulama tamamlandý. Bu kimlik numarasýna ait kayýt bulunamadý.");
					message = e.toString();
					
				}
			}

		} catch (Exception e) {
			message = e.toString();
			getExecutor().speakQueue(
					"Kiþi sorgulanýrken hata oluþtu. Lütfen tekrar deneyin.");
		}
		return person;
	}

	@Override
	protected void onPostExecute(Person result) {
		// AlertDialog a =
		// new AlertDialog.Builder(null)
		// .setTitle("Hata ")
		// .setMessage("Sonuç : "+result)
		// .setPositiveButton("Nabalým :(", null).create();
		// a.show();
		if (result != null) {
			speakPerson(result);
		} else {
			try {
				Thread.sleep(9000);
			} catch (InterruptedException e1) {
			}
			MultiTurnDemo.speechActivator.detectActivation();
		}
	}

	private void speakPerson(Person person) {
		getExecutor().speakQueue("Sorgulama tamamlandý.");
		getExecutor().speakQueue("Kiþi adý " + person.getName());
		getExecutor().speakQueue("Kiþi soyadý " + person.getSurname());
		
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
