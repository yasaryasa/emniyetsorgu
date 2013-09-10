package com.yaser.speech.activation;

import java.util.Arrays;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.yaser.Constants;
import com.yaser.MultiTurnDemo;
import com.yaser.speech.voiceaction.MultiCommandVoiceAction;
import com.yaser.speech.voiceaction.VoiceAction;
import com.yaser.speech.voiceaction.VoiceActionCommand;
import com.yaser.speech.voiceaction.VoiceActionExecutor;
import com.yaser.speech.voiceaction.WhyNotUnderstoodListener;
import com.yaser.speech.voiceaction.commands.CancelCommand;
import com.yaser.speech.voiceaction.commands.GrammerMenu;
import com.yaser.speech.voiceaction.commands.KimlikSorguMenu;
import com.yaser.speech.voiceaction.commands.PlakaSorguMenu;

/**
 * Persistently run a speech activator in the background.
 * Use {@link Intent}s to start and stop it
 * 
 */
public class SpeechActivationService extends Service implements
        SpeechActivationListener
{
    private static final String TAG = "SpeechActivationService";
    public static final String ACTIVATION_TYPE_INTENT_KEY =
            "ACTIVATION_TYPE_INTENT_KEY";
    public static final String ACTIVATION_RESULT_INTENT_KEY =
            "ACTIVATION_RESULT_INTENT_KEY";
    public static final String ACTIVATION_RESULT_BROADCAST_NAME =
            "com.yaser.speech.activation.ACTIVATION";

    /**
     * send this when external code wants the Service to stop
     */
    public static final String ACTIVATION_STOP_INTENT_KEY =
            "ACTIVATION_STOP_INTENT_KEY";

    public static final int NOTIFICATION_ID = 10298;

    public static boolean isStarted;

    public static SpeechActivator activator;

    @Override
    public void onCreate()
    {
        super.onCreate();
        isStarted = false;
        
    }

    public static Intent makeStartServiceIntent(Context context,
            String activationType)
    {
    	MultiTurnDemo.menuVoiceAction = makeStartMenu(context, MultiTurnDemo.executor);
    	MultiTurnDemo.grammerVoiceAction = makeGrammerMenu(context, MultiTurnDemo.executor);
        Intent i = new Intent(context, SpeechActivationService.class);
        i.putExtra(ACTIVATION_TYPE_INTENT_KEY, activationType);
        return i;
    }

    public static Intent makeServiceStopIntent(Context context)
    {
        Intent i = new Intent(context, SpeechActivationService.class);
        i.putExtra(ACTIVATION_STOP_INTENT_KEY, true);
        return i;
    }

    /**
     * stop or start an activator based on the activator type and if an
     * activator is currently running
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent != null)
        {
            if (intent.hasExtra(ACTIVATION_STOP_INTENT_KEY))
            {
                Log.d(TAG, "stop service intent");
                activated(false);
            }
            else
            {
                if (isStarted)
                {
                    // the activator is currently started
                    // if the intent is requesting a new activator
                    // stop the current activator and start
                    // the new one
                    if (isDifferentType(intent))
                    {
                        Log.d(TAG, "is differnet type");
                        stopActivator();
                        startDetecting(intent);
                    }
                    else
                    {
                        Log.d(TAG, "already started this type");
                    }
                }
                else
                {
                    // activator not started, start it
                    startDetecting(intent);
                }
            }
        }

        // restart in case the Service gets canceled
        return START_REDELIVER_INTENT;
    }

    private void startDetecting(Intent intent)
    {
        activator = getRequestedActivator(intent);
        Log.d(TAG, "started: " + activator.getClass().getSimpleName());
        isStarted = true;
        activator.detectActivation();
        MultiTurnDemo.speechActivator = activator;
        startForeground(NOTIFICATION_ID, getNotification());
        
    }

    private SpeechActivator getRequestedActivator(Intent intent)
    {
        String type = intent.getStringExtra(ACTIVATION_TYPE_INTENT_KEY);
        // create based on a type name
        SpeechActivator speechActivator =
                SpeechActivatorFactory.createSpeechActivator(this, this, type);
        return speechActivator;
    }

    /**
     * determine if the intent contains an activator type 
     * that is different than the currently running type
     */
    private boolean isDifferentType(Intent intent)
    {
        boolean different = false;
        if (activator == null)
        {
            return true;
        }
        else
        {
            SpeechActivator possibleOther = getRequestedActivator(intent);
            different = !(possibleOther.getClass().getName().
                    equals(activator.getClass().getName()));
        }
        return different;
    }

    @Override
    public void activated(boolean success)
    {
        // make sure the activator is stopped before doing anything else
        stopActivator();

        // broadcast result
//        Intent intent = new Intent(ACTIVATION_RESULT_BROADCAST_NAME);
//        intent.putExtra(ACTIVATION_RESULT_INTENT_KEY, success);
//        sendBroadcast(intent);
//
        // always stop after receive an activation
        stopSelf();
        
//        Intent i = new Intent(SpeechActivationService.this, MultiTurnDemo.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        startActivity(i);
        
        if (Constants.useGrammer) {
        	MultiTurnDemo.executor.execute(MultiTurnDemo.grammerVoiceAction);
        } else {
        	MultiTurnDemo.executor.execute(MultiTurnDemo.menuVoiceAction);
        }
        
        
    }

    public static VoiceAction makeGrammerMenu(Context context,
    		VoiceActionExecutor executor) {
    	String LOOKUP_PROMPT = "Dilbilgisi kurallarýna göre kimlik veya plaka sorgulamasý yapabilirsiniz.";
    	
//    	final String LOOKUP_PROMPT = "Sorgu baþladý.";
    	VoiceActionCommand kimlikSorgu = new GrammerMenu(context, executor);
    	VoiceAction voiceAction = new MultiCommandVoiceAction(Arrays.asList(kimlikSorgu));
    	voiceAction.setNotUnderstood(new WhyNotUnderstoodListener(context,
    			executor, true));
    	
    	voiceAction.setPrompt(LOOKUP_PROMPT);
    	voiceAction.setSpokenPrompt(LOOKUP_PROMPT);
    	
    	return voiceAction;
    }
    
    public static VoiceAction makeStartMenu(Context context,
			VoiceActionExecutor executor) {
//		String giris = "Merhaba, emniyet sorguya hoþgeldiniz. Benimle birlikte,"
//				+ " kimlik numarasý veya plaka ile sorgulama yapabilirsiniz.";

//		String LOOKUP_PROMPT = giris
//				+ "Kimlik sorgulama için Kimlik. Plaka sorgulamak için plaka komutlarýný kullanýn."
//				+ " Çýkýþ yapmak için iptal demeniz yeterli.";
		
		String LOOKUP_PROMPT = "Kimlik sorgulama için Kimlik. Plaka sorgulamak için plaka komutlarýný kullanýn."
				+ " Çýkýþ yapmak için iptal demeniz yeterli.";
    	 
//    	final String LOOKUP_PROMPT = "Sorgu baþladý.";
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

    
    @Override
    public void onDestroy()
    {
        Log.d(TAG, "On destroy");
        super.onDestroy();
        stopActivator();
        stopForeground(true);
    }

    public static void stopActivator()
    {
        if (activator != null)
        {
            Log.d(TAG, "stopped: " + activator.getClass().getSimpleName());
            activator.stop();
            isStarted = false;
        }
    }

    private Notification getNotification()
    {
        // determine label based on the class
        String name = SpeechActivatorFactory.getLabel(this, activator);
        String message ="Notification for listening" + " " + name;
        String title = "Notification Title";

        PendingIntent pi =
                PendingIntent.getService(this, 0, makeServiceStopIntent(this),
                        0);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setWhen(System.currentTimeMillis()).setTicker(message)
                    .setContentTitle(title).setContentText(message)
                    .setContentIntent(pi);
            notification = builder.getNotification();
        }
        else
        {
            notification =
                    new Notification(1, message,
                            System.currentTimeMillis());
            notification.setLatestEventInfo(this, title, message, pi);
        }

        return notification;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}