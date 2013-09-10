package com.yaser.service;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.yaser.service.model.Person;

import android.util.Log;

public final class CallWebService {

	private CallWebService(){}
	
	private static String URL = "http://m.muzikfon.com/beta/server/user/login?username=yas&password=yas";
	
	public static String callWebService(String q){
		String result = "";
    	HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(URL);
//		request.addHeader("deviceId", deviceId);
		ResponseHandler<String> handler = new BasicResponseHandler();
		try {
			result = httpclient.execute(request, handler);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown(); 
		Log.i("Your Logcat tag: ", result);
		System.out.println("Sonuc = "+result);
		createPerson(result);
		return result;
    } // end callWebService()
	
	public static Person createPerson(String json) {
		Person p = null;
		try {
			JSONObject obj = new JSONObject(json);
			p = new Person();
			p.setName(obj.getString("name"));
			p.setSurname(obj.getString("surname"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
	
}
