package com.yaser.speech.activation.launcher;

import com.yaser.speech.activation.MovementActivator;
import com.yaser.speech.activation.SpeechActivationListener;
import com.yaser.speech.activation.SpeechActivator;
import com.yaser.speech.activation.WordActivator;
import com.yaser.thesis.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * demonstrate starting and stopping speech activator
 * 
 */
public class SpeechActivatorStartStop extends Activity implements
        SpeechActivationListener
{
    private static final String TAG = "SpeechActivatorStartStop";

    /**
     * store if currently listening
     */
    private boolean isListeningForActivation;

    /**
     * if paused, store what was happening so that onResume can restart it
     */
    private boolean wasListeningForActivation;

    private SpeechActivator speechActivator;

    /**
     * for saving {@link #wasListeningForActivation} 
     * in the saved instance state
     */
    private static final String WAS_LISTENING_STATE = "WAS_LISTENING";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speechactivationstart_stop);

        isListeningForActivation = false;
        speechActivator = new WordActivator(this, this, "asistan");

        // start and stop buttons
        Button start = (Button) findViewById(R.id.btn_start);
        start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivator();
            }
        });

        Button stop = (Button) findViewById(R.id.btn_stop);
        stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopActivator();
            }
        });
    }

    private void startActivator()
    {
        if (isListeningForActivation)
        {
            Toast.makeText(this, "Not started: already started",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "not started, already started");
            // only activate once
            return;
        }

        if (speechActivator != null)
        {
            isListeningForActivation = true;
            Toast.makeText(this, "Started movement activator",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "started");
            speechActivator.detectActivation();
        }
    }

    private void stopActivator()
    {
        if (speechActivator != null)
        {
            Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "stopped");
            speechActivator.stop();
        }
        isListeningForActivation = false;
    }

    @Override
    public void activated(boolean success)
    {
        Log.d(TAG, "activated...");

        //don't allow multiple activations
        if (!isListeningForActivation)
        {
            Toast.makeText(this, "Not activated because stopped",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (success)
        {
            Toast.makeText(this, "Activated, no longer listening",
                    Toast.LENGTH_SHORT).show();
            //start speech recognition here
            
            //TODO burada uygulama aktif olmus olacak 
            
        }
        else
        {
            Toast.makeText(this, "activation failed, no longer listening",
                    Toast.LENGTH_SHORT).show();
        }

        isListeningForActivation = false;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "ON PAUSE stop");
        // save before stopping
        wasListeningForActivation = isListeningForActivation;
        stopActivator();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "ON RESUME was listening: " + wasListeningForActivation);
        if (wasListeningForActivation)
        {
            startActivator();
        }
    }

    // Note: onDestroy not needed since the activator was
    // stopped during onPause()

    // if the activity was destroyed these two methods are needed
    // to restore wasListening
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putBoolean(WAS_LISTENING_STATE, isListeningForActivation);
        Log.d(TAG, "saved state: " + isListeningForActivation);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        wasListeningForActivation =
                savedInstanceState.getBoolean(WAS_LISTENING_STATE);
        Log.d(TAG, "restored state: " + wasListeningForActivation);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
