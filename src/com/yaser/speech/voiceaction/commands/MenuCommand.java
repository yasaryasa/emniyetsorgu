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

import android.content.Context;

import com.yaser.Constants;
import com.yaser.service.RetreiveJsonTask;
import com.yaser.speech.text.WordList;
import com.yaser.speech.text.match.StemmedWordMatcher;
import com.yaser.speech.text.match.WordMatcher;
import com.yaser.speech.voiceaction.VoiceActionCommand;
import com.yaser.speech.voiceaction.VoiceActionExecutor;

public class MenuCommand implements VoiceActionCommand
{
    private VoiceActionExecutor executor;
    private Context context;
    private WordMatcher matcher;

    public MenuCommand(Context context, VoiceActionExecutor executor)
    {
        this.context = context;
        this.executor = executor;
        
        if (Constants.useStemming) {
        	this.matcher = new StemmedWordMatcher("kimlik","kiþi","plaka","araç");
        } else {
        	this.matcher = new WordMatcher("kimlik","kiþi","plaka","araç");
        }
        
//        this.matcher = new StemmedWordMatcher("kimlik","kiþi","plaka","araç");
    }

    public boolean interpret(WordList heard, float[] confidence)
    {
        boolean success = false;
        String[] words = heard.getWords();
        
        if (matcher.isIn(heard.getWords()))
        {
            success = true;
        }
        
        return success;
    }
}
