package com.yaser.speech.voiceaction.commands;

import java.util.List;

import android.content.Context;

import com.yaser.service.RetreiveJsonTask;
import com.yaser.speech.activation.SpeechActivationService;
import com.yaser.speech.command_utils.AllCommands;
import com.yaser.speech.text.WordList;
import com.yaser.speech.text.match.StemmedWordMatcher;
import com.yaser.speech.text.match.WordMatcher;
import com.yaser.speech.voiceaction.OnNotUnderstoodListener;
import com.yaser.speech.voiceaction.OnUnderstoodListener;
import com.yaser.speech.voiceaction.VoiceActionCommand;
import com.yaser.speech.voiceaction.VoiceActionExecutor;
import com.yaser.speech.voiceaction.VoiceAlertDialog;
import com.yaser.thesis.R;

public class KimlikSorgu implements VoiceActionCommand
{
    private VoiceActionExecutor executor;
    private Context context;
    private WordMatcher match;

    public KimlikSorgu(Context context, VoiceActionExecutor executor)
    {
        this.context = context;
        this.executor = executor;
        this.match = new StemmedWordMatcher("kimlik sorgu, kiþi sorgu");
    }

    public boolean interpret(WordList heard, float[] confidence)
    {
        boolean success = true;
        String[] words = heard.getWords();
        
        
        int matchIndex = match.isInAt(heard.getWords());
        
        //kimlik sorgulama match edildi
        if (matchIndex >= 0)
        {
        	//bir sonra söylenen kelime, kimlik numarasýdýr
            String tckNo = heard.getStringAfter(matchIndex);
        }
        
        String tckno = null;
        for (int i = 0; i < words.length; i++) {
        	if (tckno == null) {
        		tckno = words[i].concat("  ");
        	} else {
        		tckno += words[i].concat("  ");
        	}
		}
        final String tcknoFormat = tckno.replaceAll(" ", "");
        //TODO burada yes-no confirmation olmasý lazým
        final VoiceAlertDialog confirmDialog = new VoiceAlertDialog();
        // add listener for positive response
        // use relaxed matching to increase chance of understanding user
        confirmDialog.addRelaxedPositive(new OnUnderstoodListener()
        {
            @Override
            public void understood()
            {
                RetreiveJsonTask task = new RetreiveJsonTask(KimlikSorgu.this);
                task.execute(tcknoFormat);
            }
        });
        
        confirmDialog.addRelaxedNegative(new OnUnderstoodListener()
        {
        	@Override
        	public void understood()
        	{
        		executor.speak("kimlik sorgulama iptal edildi.");
        		try {
    				Thread.sleep(2200);
    			} catch (InterruptedException e) {
    			}
                SpeechActivationService.activator.detectActivation();
        	}
        });
        
        confirmDialog.addRelaxedNeutral(new OnUnderstoodListener()
        {
        	@Override
        	public void understood()
        	{
        		executor.execute(AllCommands.makeKimlikMenu(context, executor));
        	}
        }, new String[]{"düzelt"});


        // if the user says anything else besides the yes words cancel
        confirmDialog.setNotUnderstood(new OnNotUnderstoodListener()
        {
            @Override
            public void notUnderstood(List<String> heard, int reason)
            {
                String toSayCancelled = context.getResources().getString(
                        R.string.voiceaction_dontunderstand);
                executor.reExecute(toSayCancelled);
            }
        });
        
        String toSay="Sorgulamak istediðiniz kimlik numarasý. "+tckno+". Devam etmek istiyor musunuz?";
        confirmDialog.setPrompt(toSay);
        confirmDialog.setSpokenPrompt(toSay);
        
        executor.execute(confirmDialog);
        return success;
    }
    
    public VoiceActionExecutor getExecutor() {
		return executor;
	}
    
    public Context getContext() {
		return context;
	}
}
