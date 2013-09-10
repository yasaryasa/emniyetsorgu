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

import com.yaser.MultiTurnDemo;
import com.yaser.speech.activation.SpeechActivationService;
import com.yaser.speech.text.WordList;
import com.yaser.speech.text.match.StemmedWordMatcher;
import com.yaser.speech.text.match.WordMatcher;
import com.yaser.speech.voiceaction.VoiceActionCommand;
import com.yaser.speech.voiceaction.VoiceActionExecutor;

import android.content.Context;

public class CancelCommand implements VoiceActionCommand
{
    private VoiceActionExecutor executor;
    private String cancelledPrompt;
    private WordMatcher matcher;
    
    public CancelCommand(Context context, VoiceActionExecutor executor)
    {
        this.executor = executor;
        this.cancelledPrompt = "Ýþlem Ýptal Edildi.";
        this.matcher = new StemmedWordMatcher("iptal","son");
    }
    
    @Override
    public boolean interpret(WordList heard, float [] confidence)
    {
        boolean understood = false;
        if (matcher.isIn(heard.getWords()))
        {
            executor.speakQueue(cancelledPrompt);
            understood = true;
            
            try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
            SpeechActivationService.activator.detectActivation();
        }
        return understood;
    }
}
