package com.yaser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {

	public String parseJSON() {
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(
				"http://trandroid.com/wp-content/dosyalar/androidjsonparsesample.php");
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
					JSONObject obj = new JSONObject(json.toString());
					stringBuilder.append(obj.getString("ad") + " "
							+ obj.getString("soyad"));
					JSONObject takip = new JSONObject(
							obj.getString("takipettikleri"));
					stringBuilder.append(" " + takip.getString("kategori")
							+ " kategorisinde þu siteleri takip ediyor :\n\n");
					JSONArray siteler = takip.getJSONArray("siteler");

					int i;
					for (i = 0; i < siteler.length(); i++) {
						JSONObject ss = siteler.getJSONObject(i);
						stringBuilder.append("\tSite adresi: "
								+ ss.getString("adresi") + "\n");
						stringBuilder.append("\tSloganý: "
								+ ss.getString("slogan") + "\n\n");
					}
					
					instream.close();
					message  = stringBuilder.toString();

				} catch (Exception e) {
					message = e.toString();
				}
			}

		} catch (Exception e) {
			message = e.toString();
		}
		return message;
		
	}
	
}
