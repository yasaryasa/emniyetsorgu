package com.yaser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yaser.thesis.R;

public class YardimActivity extends Activity{

	  @Override
	    public void onCreate(Bundle savedInstanceState)
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        
	        initButton();
	    }

	private void initButton() {
		
		Button close = (Button) findViewById(R.id.buttonYardimKapat);
		
		
		close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(getApplicationContext(),
						MultiTurnDemo.class);
				setResult(100, in);
				// Closing PlayListView
				finish();
			}
		});
	}
	
}
