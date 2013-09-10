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

import java.util.Arrays;
import java.util.Set;

import android.content.Context;

import com.yaser.Constants;
import com.yaser.speech.text.WordList;
import com.yaser.speech.text.match.StemmedWordMatcher;
import com.yaser.speech.text.match.WordMatcher;
import com.yaser.speech.voiceaction.MultiCommandVoiceAction;
import com.yaser.speech.voiceaction.VoiceAction;
import com.yaser.speech.voiceaction.VoiceActionCommand;
import com.yaser.speech.voiceaction.VoiceActionExecutor;
import com.yaser.speech.voiceaction.WhyNotUnderstoodListener;

public class KimlikSorguMenu implements VoiceActionCommand
{
    private VoiceActionExecutor executor;
    private Context context;
    private WordMatcher matcher;
    
    private String[] words = {"kimlik","kiþi"};

    public KimlikSorguMenu(Context context, VoiceActionExecutor executor)
    {
        this.context = context;
        this.executor = executor;
        if (Constants.useStemming) {
        	this.matcher = new StemmedWordMatcher(words);
        } else {
        	this.matcher = new WordMatcher(words);
        }
    }

    public boolean interpret(WordList heard, float[] confidence)
    {
        boolean success = false;
        
        if (Constants.useWordSpotting) {
        	
        	if (Constants.useStemming) {
//        		if (matcher instanceof StemmedWordMatcher) {
//        			
//        		} else {
//        			matcher = new WordMatcher(words);
//        		}
        		matcher = new StemmedWordMatcher(words);
        	} else {
        		matcher = new WordMatcher(words);
        	}
        	
        	if (matcher.isIn(heard.getWords()))
        	{
        		executor.execute(makeKimlikMenu(context, executor));
        		success = true;
        	}
        	
        } else {
        	Set<String> words = matcher.getWords();
        	String hrd = "";
        	for (String string : heard.getWords()) {
				hrd = hrd + string;
			}
        	
        	for (String word : words) {
				if (word.equalsIgnoreCase(hrd.trim())) {
					executor.execute(makeKimlikMenu(context, executor));
	        		success = true;
				}
			}
        }
        
        return success;
    }
    
    public VoiceAction makeKimlikMenu(Context context, VoiceActionExecutor executor) {
		String prompt = "Lütfen 11 haneli TC kimlik numarasýný söyleyin.";
        VoiceActionCommand lookup = new KimlikSorgu(context, executor);
        VoiceActionCommand cancel = new CancelCommand(context, executor);
        VoiceAction voiceAction = new MultiCommandVoiceAction(Arrays.asList(cancel, lookup));
        voiceAction.setNotUnderstood(new WhyNotUnderstoodListener(context, executor, true));
        
        voiceAction.setSpokenPrompt(prompt);
        voiceAction.setPrompt(prompt);
        
        return voiceAction;
	}
}
