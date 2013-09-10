package com.yaser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.yaser.thesis.R;

public class JsonParse extends Activity {

	TextView jsonDataTextView = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.json_sample);

		jsonDataTextView = (TextView) findViewById(R.id.jsonData);
		Button btnJson = (Button) findViewById(R.id.btnGetJson);

		btnJson.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new RetreiveJsonTask().execute();
			}
		});

	}

	class RetreiveJsonTask extends AsyncTask<String, Void, String> {

		private Exception exception;

		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			// HttpGet httpget = new HttpGet(
			// "http://trandroid.com/wp-content/dosyalar/androidjsonparsesample.php");
			HttpGet httpget = new HttpGet(
					"http://m.muzikfon.com/beta/server/user/login?username=yas&password=yas");
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
						System.out.println("getting lines");
						while ((line = reader.readLine()) != null) {
							json.append(line + "\n");
						}

						StringBuilder stringBuilder = new StringBuilder();
						json.replace(0, 2, "");
						json.replace(json.length()-3, json.length()-1, "");
						 JSONObject obj = new JSONObject(json.toString());
						 stringBuilder.append(obj.getString("userName") + " "
						 + obj.getString("password"));
						//
						//
						//
						// JSONObject takip = new JSONObject(
						// obj.getString("takipettikleri"));
						// stringBuilder.append(" " +
						// takip.getString("kategori")
						// + " kategorisinde þu siteleri takip ediyor :\n\n");
						// JSONArray siteler = takip.getJSONArray("siteler");
						//
						// int i;
						// for (i = 0; i < siteler.length(); i++) {
						// JSONObject ss = siteler.getJSONObject(i);
						// stringBuilder.append("\tSite adresi: "
						// + ss.getString("adresi") + "\n");
						// stringBuilder.append("\tSloganý: "
						// + ss.getString("slogan") + "\n\n");
						// }

						instream.close();
						message = stringBuilder.toString();

					} catch (Exception e) {
						message = e.toString();
					}
				}

			} catch (Exception e) {
				message = e.toString();
			}
			return message;
		}

		@Override
		protected void onPostExecute(String result) {
			// AlertDialog a =
			// new AlertDialog.Builder(null)
			// .setTitle("Hata ")
			// .setMessage("Sonuç : "+result)
			// .setPositiveButton("Nabalým :(", null).create();
			// a.show();
			jsonDataTextView.setText(result);
		}

	}

}