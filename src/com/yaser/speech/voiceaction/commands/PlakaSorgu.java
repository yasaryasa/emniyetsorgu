/*
 * Copyright 2011 Greg Milette and Adam Stroud
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yaser.speech.voiceaction.commands;

import java.util.List;

import android.content.Context;

import com.yaser.service.RetreiveJsonTask;
import com.yaser.service.RetreivePlakaJsonTask;
import com.yaser.speech.command_utils.AllCommands;
import com.yaser.speech.text.WordList;
import com.yaser.speech.text.match.StemmedWordMatcher;
import com.yaser.speech.voiceaction.OnNotUnderstoodListener;
import com.yaser.speech.voiceaction.OnUnderstoodListener;
import com.yaser.speech.voiceaction.VoiceActionCommand;
import com.yaser.speech.voiceaction.VoiceActionExecutor;
import com.yaser.speech.voiceaction.VoiceAlertDialog;
import com.yaser.thesis.R;

public class PlakaSorgu implements VoiceActionCommand
{
    private VoiceActionExecutor executor;
    private Context context;

    public PlakaSorgu(Context context, VoiceActionExecutor executor)
    {
    	 this.context = context;
         this.executor = executor;
    }

    public boolean interpret(WordList heard, float[] confidence)
    {
        boolean success = true;
        String[] words = heard.getWords();
        
        String plakaNo = null;
        String toSpeak = null;
        for (int i = 0; i < words.length; i++) {
        	
        	try {
				Integer.parseInt(words[i]);
				if (plakaNo == null) {
					plakaNo = words[i].concat("");
					toSpeak = words[i].concat(" ");
				} else {
					plakaNo += words[i].concat("");
					toSpeak += words[i].concat(" ");
				}
			} catch (NumberFormatException e) {
				if (plakaNo == null) {
					plakaNo = words[i].substring(0,1).concat("");
					toSpeak = words[i].concat(" ");
				} else {
					plakaNo += words[i].substring(0,1).concat("");
					toSpeak += words[i].concat(" ");
				}

			}
        	
		}
        final String tcknoFormat = plakaNo.replaceAll(" ", "");
        //TODO burada yes-no confirmation olmasý lazým
        final VoiceAlertDialog confirmDialog = new VoiceAlertDialog();
        // add listener for positive response
        // use relaxed matching to increase chance of understanding user
        confirmDialog.addRelaxedPositive(new OnUnderstoodListener()
        {
            @Override
            public void understood()
            {
            	 RetreivePlakaJsonTask task = new RetreivePlakaJsonTask(PlakaSorgu.this);
                 task.execute(tcknoFormat);
            }
        });
        
        confirmDialog.addRelaxedNeutral(new OnUnderstoodListener()
        {
        	@Override
        	public void understood()
        	{
        		executor.execute(AllCommands.INSTANCE.makePlakaMenu(context, executor));
        	}
        }, new String[]{"düzelt"});
        
        confirmDialog.addRelaxedNegative(new OnUnderstoodListener()
        {
        	@Override
        	public void understood()
        	{
        		executor.speak("Plaka sorgulama iptal edildi.");
        	}
        });


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
        
        String toSay="Sorgulamak istediðiniz plaka. "+toSpeak+".   Devam etmek istiyor musunuz?";
//        executor.speakQueue(toSay);
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
